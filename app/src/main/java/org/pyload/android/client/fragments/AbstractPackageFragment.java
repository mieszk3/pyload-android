package org.pyload.android.client.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.thrift.TException;
import org.pyload.android.client.R;
import org.pyload.android.client.components.ExpandableListFragment;
import org.pyload.android.client.components.TabHandler;
import org.pyload.android.client.dialogs.FileInfoDialog;
import org.pyload.android.client.module.GuiTask;
import org.pyload.android.client.module.Utils;
import org.pyload.android.client.pyLoadApp;
import org.pyload.thrift.Destination;
import org.pyload.thrift.DownloadStatus;
import org.pyload.thrift.FileData;
import org.pyload.thrift.PackageData;
import org.pyload.thrift.Pyload.Client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

public abstract class AbstractPackageFragment extends ExpandableListFragment
        implements TabHandler {

    private final Comparator<Object> mOrderComparator = (a, b) -> {
        if (a == null && b == null)
            return 0;
        else if (a == null)
            return 1;
        else if (b == null)
            return -1;
        else if (a instanceof PackageData && b instanceof PackageData)
            return ((Short) ((PackageData) a).order).compareTo(((PackageData) b).order);
        else if (a instanceof FileData && b instanceof FileData)
            return ((Short) ((FileData) a).order).compareTo(((FileData) b).order);
        return 0;
    };
    protected int dest;
    private List<PackageData> data;
    private pyLoadApp app;
    private final Runnable mUpdateResults = this::onDataReceived;
    private Client client;
    // tab position
    private int pos = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (pyLoadApp) getActivity().getApplicationContext();
        data = new ArrayList<>();

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        registerForContextMenu(view.findViewById(android.R.id.list));
        PackageListAdapter adp = new PackageListAdapter(app, data,
                R.layout.package_item, R.layout.package_child_item);
        setListAdapter(adp);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        Log.d("pyLoad", dest + " onContextItemSelected " + item);

        // filter event und allow to proceed
        if (!app.isCurrentTab(pos))
            return false;

        ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) item
                .getMenuInfo();

        int type = ExpandableListView
                .getPackedPositionType(info.packedPosition);
        if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            int groupPos = ExpandableListView
                    .getPackedPositionGroup(info.packedPosition);
            int childPos = ExpandableListView
                    .getPackedPositionChild(info.packedPosition);

            final FileData file;
            try {
                file = data.get(groupPos).links.get(childPos);
            } catch (IndexOutOfBoundsException e) {
                return false;
            }

            switch (item.getItemId()) {
                case R.id.restart:

                    app.addTask(new GuiTask(() -> {
                        try {
                            client = app.getClient();
                            client.restartFile(file.fid);
                        } catch (TException e) {
                            throw new RuntimeException(e);
                        }
                    }, app.handleSuccess));

                    break;
                case R.id.delete:

                    app.addTask(new GuiTask(() -> {
                        try {
                            client = app.getClient();
                            ArrayList<Integer> fids = new ArrayList<>();
                            fids.add(file.fid);

                            client.deleteFiles(fids);
                        } catch (TException e) {
                            throw new RuntimeException(e);
                        }
                    }, app.handleSuccess));

                    break;

                case R.id.move:
                    Toast.makeText(getActivity(), R.string.cant_move_files,
                            Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }

            return true;
        } else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
            int groupPos = ExpandableListView
                    .getPackedPositionGroup(info.packedPosition);

            final PackageData pack;
            try {
                pack = data.get(groupPos);
            } catch (IndexOutOfBoundsException e) {
                return false; // pack does not exists anymore
            }

            switch (item.getItemId()) {
                case R.id.restart:

                    app.addTask(new GuiTask(() -> {
                        try {
                            client = app.getClient();
                            client.restartPackage(pack.pid);
                        } catch (TException e) {
                            throw new RuntimeException(e);
                        }
                    }, app.handleSuccess));

                    break;
                case R.id.delete:

                    app.addTask(new GuiTask(() -> {
                        try {
                            client = app.getClient();
                            ArrayList<Integer> pids = new ArrayList<>();
                            pids.add(pack.pid);
                            client.deletePackages(pids);
                        } catch (TException e) {
                            throw new RuntimeException(e);
                        }
                    }, app.handleSuccess));

                    break;

                case R.id.move:

                    app.addTask(new GuiTask(() -> {
                        try {
                            client = app.getClient();
                            Destination newDest;
                            if (dest == 0) {
                                newDest = Destination.Collector;
                            } else {
                                newDest = Destination.Queue;
                            }

                            client.movePackage(newDest, pack.pid);
                        } catch (TException e) {
                            throw new RuntimeException(e);
                        }
                    }, app.handleSuccess));

                    break;

                default:
                    break;
            }

            return true;
        }

        return false;

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.package_list, null, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int group,
                                int child, long id) {

        PackageData pack;
        FileData file;
        try {
            pack = data.get(group);
            file = pack.links.get(child);
        } catch (Exception e) {
            return true;
        }

        FileInfoDialog dialog = FileInfoDialog.newInstance(pack, file);
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null && !fragmentManager.isStateSaved()) {
            dialog.show(fragmentManager, FileInfoDialog.class.getName());
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.package_context_menu, menu);
        menu.setHeaderTitle(R.string.choose_action);
    }

    @Override
    public void onSelected() {
        app = (pyLoadApp) getActivity().getApplicationContext();
        refresh();
    }

    @Override
    public void onDeselected() {
    }

    @Override
    public void setPosition(int pos) {
        this.pos = pos;
    }

    public void refresh() {

        if (!app.hasConnection())
            return;

        app.setProgress(true);

        GuiTask task = new GuiTask(() -> {
            try {
                client = app.getClient();
                if (dest == 0)
                    data = client.getQueueData();
                else
                    data = client.getCollectorData();
            } catch (TException e) {
                throw new RuntimeException(e);
            }
        }, mUpdateResults);

        app.addTask(task);
    }

    private void onDataReceived() {
        app.setProgress(false);
        Collections.sort(data, mOrderComparator);
        for (PackageData pak : data)
            Collections.sort(pak.links, mOrderComparator);

        PackageListAdapter adapter = (PackageListAdapter) getExpandableListAdapter();
        adapter.setData(data);
    }
}

class PackageListAdapter extends BaseExpandableListAdapter {

    private final int groupRes;
    private final int childRes;
    private final LayoutInflater layoutInflater;
    private List<PackageData> data;

    PackageListAdapter(@NonNull pyLoadApp app, List<PackageData> data,
                       int groupRes, int childRes) {

        this.data = data;
        this.groupRes = groupRes;
        this.childRes = childRes;

        layoutInflater = (LayoutInflater) app
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<PackageData> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public int getGroupCount() {
        return data.size();
    }

    public int getChildrenCount(int group) {
        return data.get(group).links.size();
    }

    public Object getGroup(int group) {
        return data.get(group);
    }

    public Object getChild(int group, int child) {
        return data.get(group).links.get(child);
    }

    public long getGroupId(int group) {
        return group;
    }

    public long getChildId(int group, int child) {
        return child;
    }

    public boolean hasStableIds() {
        return false;
    }

    public View getGroupView(int group, boolean isExpanded, View convertView,
                             ViewGroup parent) {

        PackageData pack = data.get(group);
        if (convertView == null) {
            convertView = layoutInflater.inflate(groupRes, null);
            GroupViewHolder holder = new GroupViewHolder();
            holder.name = convertView.findViewById(R.id.name);
            holder.progress = convertView
                    .findViewById(R.id.package_progress);
            holder.size = convertView.findViewById(R.id.size_stats);
            holder.links = convertView.findViewById(R.id.link_stats);
            convertView.setTag(holder);
        }

        GroupViewHolder holder = (GroupViewHolder) convertView.getTag();
        holder.name.setText(pack.name);

        if (pack.linkstotal == 0)
            pack.linkstotal = 1;

        holder.progress.setProgress((pack.linksdone * 100) / pack.links.size());
        holder.size.setText(Utils.formatSize(pack.sizedone) + " / "
                + Utils.formatSize(pack.sizetotal));
        holder.links.setText(pack.linksdone + " / " + pack.links.size());

        return convertView;
    }

    public View getChildView(int group, int child, boolean isLastChild,
                             View convertView, ViewGroup parent) {

        FileData file = data.get(group).links.get(child);

        if (file == null)
            return null;

        if (convertView == null) {
            convertView = layoutInflater.inflate(childRes, null);
            ChildViewHolder holder = new ChildViewHolder();
            holder.name = convertView.findViewById(R.id.name);
            holder.status = convertView.findViewById(R.id.status);
            holder.size = convertView.findViewById(R.id.size);
            holder.plugin = convertView.findViewById(R.id.plugin);
            holder.status_icon = convertView
                    .findViewById(R.id.status_icon);
            convertView.setTag(holder);
        }

        ChildViewHolder holder = (ChildViewHolder) convertView.getTag();

        // seems to occure according to bug report
        // no idea why, and what about other data, so returning the view instantly
        if (file.name == null) {
            holder.name.setText(R.string.lambda);
            return convertView;
        }

        if (!file.name.equals(holder.name.getText()))
            holder.name.setText(file.name);

        holder.status.setText(file.statusmsg);
        holder.size.setText(Utils.formatSize(file.size));
        holder.plugin.setText(file.plugin);

        if (file.status == DownloadStatus.Failed
                || file.status == DownloadStatus.Aborted
                || file.status == DownloadStatus.Offline) {
            holder.status_icon.setImageResource(R.drawable.stop);
        } else if (file.status == DownloadStatus.Finished) {
            holder.status_icon.setImageResource(R.drawable.tick);
        } else if (file.status == DownloadStatus.Waiting) {
            holder.status_icon.setImageResource(R.drawable.menu_clock);
        } else if (file.status == DownloadStatus.Skipped) {
            holder.status_icon.setImageResource(R.drawable.tag);
        } else {
            holder.status_icon.setImageResource(0);
        }

        return convertView;
    }

    public boolean isChildSelectable(int group, int child) {
        return true;
    }

    private static class GroupViewHolder {
        private TextView name;
        private ProgressBar progress;
        private TextView size;
        private TextView links;
    }

    private static class ChildViewHolder {
        private TextView name;
        private TextView status;
        private TextView size;
        private TextView plugin;
        private ImageView status_icon;
    }
}
