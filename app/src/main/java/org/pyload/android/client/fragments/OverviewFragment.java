package org.pyload.android.client.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.thrift.TException;
import org.pyload.android.client.R;
import org.pyload.android.client.components.TabHandler;
import org.pyload.android.client.dialogs.CaptchaDialog;
import org.pyload.android.client.module.GuiTask;
import org.pyload.android.client.module.Utils;
import org.pyload.android.client.pyLoadApp;
import org.pyload.thrift.CaptchaTask;
import org.pyload.thrift.DownloadInfo;
import org.pyload.thrift.DownloadStatus;
import org.pyload.thrift.Pyload.Client;
import org.pyload.thrift.ServerStatus;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.ListFragment;

public class OverviewFragment extends ListFragment implements
        OnDismissListener, TabHandler {

    private final Handler mHandler = new Handler();
    private pyLoadApp app;
    private Client client;
    private OverviewAdapter adp;
    private List<DownloadInfo> downloads;
    private ServerStatus status;
    private CaptchaTask captcha;
    private int lastCaptcha = -1;
    private int interval = 5;
    private boolean update = false;
    private final Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            refresh();
            if (update)
                mHandler.postDelayed(this, interval * 1000);
        }
    };
    private final Runnable cancelUpdate = this::stopUpdate;
    private boolean dialogOpen = false;
    // tab position
    private int pos = -1;
    /**
     * GUI Elements
     */
    private TextView statusServer;
    private TextView reconnect;
    private TextView speed;
    private TextView active;
    private final Runnable mUpdateResults = this::onDataReceived;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = (pyLoadApp) getActivity().getApplicationContext();

        downloads = new ArrayList<DownloadInfo>();
        adp = new OverviewAdapter(app, R.layout.overview_item, downloads);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.overview, null, false);

        statusServer = v.findViewById(R.id.status_server);
        reconnect = v.findViewById(R.id.reconnect);
        speed = v.findViewById(R.id.speed);
        active = v.findViewById(R.id.active);

        // toggle pause on click
        statusServer.setOnClickListener(v12 -> app.addTask(new GuiTask(() -> {
            Client client;
            try {
                client = app.getClient();
                client.togglePause();
            } catch (TException e) {
                throw new RuntimeException(e);
            }
        }, app.handleSuccess)));

        // toggle reconnect on click
        reconnect.setOnClickListener(v1 -> app.addTask(new GuiTask(() -> {
            Client client;
            try {
                client = app.getClient();
                client.toggleReconnect();
            } catch (TException e) {
                throw new RuntimeException(e);
            }
        }, app.handleSuccess)));

        if (status != null && downloads != null)
            onDataReceived();

        registerForContextMenu(v.findViewById(android.R.id.list));

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(adp);
    }

    @Override
    public void onStart() {
        super.onStart();
        onSelected();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.overview_context_menu, menu);
        menu.setHeaderTitle(R.string.choose_action);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (!app.isCurrentTab(pos))
            return false;

        AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item
                .getMenuInfo();
        final int id = menuInfo.position;
        final DownloadInfo info = downloads.get(id);
        switch (item.getItemId()) {
            case R.id.abort:

                app.addTask(new GuiTask(() -> {
                    try {
                        client = app.getClient();
                        ArrayList<Integer> fids = new ArrayList<>();
                        fids.add(info.fid);
                        client.stopDownloads(fids);
                    } catch (TException e) {
                        throw new RuntimeException(e);
                    }
                }, this::refresh));
                return true;

            default:
                return super.onContextItemSelected(item);
        }

    }

    @Override
    public void onSelected() {
        startUpdate();
    }

    @Override
    public void onDeselected() {
        stopUpdate();
    }

    private void startUpdate() {
        // already update running
        if (update)
            return;
        try {
            interval = Integer.parseInt(app.prefs.getString("refresh_rate", "5"));
        } catch (NumberFormatException e) {
            // somehow contains illegal value
            interval = 5;
        }

        update = true;
        mHandler.post(mUpdateTimeTask);
    }

    private void stopUpdate() {
        update = false;
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    /**
     * Called when Status data received
     */
    private void onDataReceived() {
        OverviewAdapter adapter = (OverviewAdapter) getListAdapter();

        adapter.setDownloads(downloads);

        statusServer.setText(app.verboseBool(status.download));
        reconnect.setText(app.verboseBool(status.reconnect));
        speed.setText(Utils.formatSize(status.speed) + "/s");
        active.setText(String.format("%d / %d", status.active, status.total));

        if (captcha != null && app.prefs.getBoolean("pull_captcha", true)
                && captcha.resultType != null // string null bug
                && captcha.resultType.equals("textual")
                && lastCaptcha != captcha.tid) {
            showDialog();
        }

    }

    public void refresh() {
        if (!app.hasConnection())
            return;

        GuiTask task = new GuiTask(() -> {
            try {
                client = app.getClient();
                downloads = client.statusDownloads();
                status = client.statusServer();
                if (client.isCaptchaWaiting()) {
                    Log.d("pyLoad", "Captcha available");
                    captcha = client.getCaptchaTask(false);
                    Log.d("pyload", captcha.resultType);
                }
            } catch (TException e) {
                throw new RuntimeException(e);
            }
        }, mUpdateResults);
        task.setCritical(cancelUpdate);

        app.addTask(task);
    }

    private void showDialog() {

        if (dialogOpen || captcha == null)
            return;

        CaptchaDialog dialog = CaptchaDialog.newInstance(captcha);
        lastCaptcha = captcha.tid;

        Log.d("pyLoad", "Got Captcha Task");

        dialog.setOnDismissListener(this);

        dialogOpen = true;
        try {
            FragmentManager fragmentManager = getFragmentManager();
            if (fragmentManager != null && !fragmentManager.isStateSaved()) {
                dialog.show(fragmentManager, CaptchaDialog.class.getName());
            }
        } catch (IllegalStateException e) {
            dialogOpen = false;
            // seems to appear when overview is already closed
            Log.e("pyLoad", "Dialog state error", e);
        } catch (NullPointerException e) {
            dialogOpen = false;
            // something is null, but why?
            Log.e("pyLoad", "Dialog null pointer error", e);
        }

    }

    public void onDismiss(DialogInterface arg0) {
        captcha = null;
        dialogOpen = false;
    }

    @Override
    public void setPosition(int pos) {
        this.pos = pos;
    }
}

/**
 * Renders the single ListView items
 *
 * @author RaNaN
 */
class OverviewAdapter extends BaseAdapter {

    private final int rowResID;
    private final LayoutInflater layoutInflater;
    private List<DownloadInfo> downloads;

    OverviewAdapter(@NonNull final pyLoadApp app, final int rowResID,
                    List<DownloadInfo> downloads) {
        this.rowResID = rowResID;
        this.downloads = downloads;

        layoutInflater = (LayoutInflater) app
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    void setDownloads(List<DownloadInfo> downloads) {
        this.downloads = downloads;
        notifyDataSetChanged();
    }

    public int getCount() {
        return downloads.size();
    }

    public Object getItem(int id) {
        return downloads.get(id);
    }

    public long getItemId(int pos) {
        return pos;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        DownloadInfo info = downloads.get(position);
        if (convertView == null) {
            convertView = layoutInflater.inflate(rowResID, null);
            ViewHolder holder = new ViewHolder();
            holder.name = convertView.findViewById(R.id.name);
            holder.progress = convertView
                    .findViewById(R.id.progress);
            holder.size = convertView.findViewById(R.id.size);
            holder.speed = convertView.findViewById(R.id.speed);
            holder.size_done = convertView
                    .findViewById(R.id.size_done);
            holder.eta = convertView.findViewById(R.id.eta);
            holder.percent = convertView.findViewById(R.id.percent);
            convertView.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();


        // name is null sometimes somehow
        if (info.name != null && !info.name.equals(holder.name.getText())) {
            holder.name.setText(info.name);
        }

        holder.progress.setProgress(info.percent);

        if (info.status == DownloadStatus.Downloading) {
            holder.size.setText(Utils.formatSize(info.size));
            holder.percent.setText(info.percent + "%");
            holder.size_done.setText(Utils.formatSize(info.size - info.bleft));

            holder.speed.setText(Utils.formatSize(info.speed) + "/s");
            holder.eta.setText(info.format_eta);

        } else if (info.status == DownloadStatus.Waiting) {
            holder.size.setText(R.string.lambda);
            holder.percent.setText(R.string.lambda);
            holder.size_done.setText(R.string.lambda);

            holder.speed.setText(info.statusmsg);
            holder.eta.setText(info.format_wait);

        } else {
            holder.size.setText(R.string.lambda);
            holder.percent.setText(R.string.lambda);
            holder.size_done.setText(R.string.lambda);

            holder.speed.setText(info.statusmsg);
            holder.eta.setText(R.string.lambda);
        }

        return convertView;

    }

    public boolean hasStableIds() {
        return false;
    }

    private static class ViewHolder {
        private TextView name;
        private ProgressBar progress;
        private TextView size;
        private TextView percent;
        private TextView size_done;
        private TextView speed;
        private TextView eta;
    }

}
