package org.pyload.android.client.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.thrift.TException;
import org.pyload.android.client.R;
import org.pyload.android.client.module.GuiTask;
import org.pyload.android.client.module.SeparatedListAdapter;
import org.pyload.android.client.pyLoadApp;
import org.pyload.thrift.ConfigSection;
import org.pyload.thrift.Pyload.Client;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.ListFragment;

public class SettingsFragment extends ListFragment {

    private pyLoadApp app;
    private SeparatedListAdapter adp;
    private SettingsAdapter general;
    private Map<String, ConfigSection> generalData;
    private SettingsAdapter plugins;
    private Map<String, ConfigSection> pluginData;

    private Runnable mRefresh = SettingsFragment.this::update;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_list, null, false);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = (pyLoadApp) getActivity().getApplicationContext();

        adp = new SeparatedListAdapter(app);

        general = new SettingsAdapter(app);
        plugins = new SettingsAdapter(app);

        adp.addSection(getString(R.string.general_config), general);
        adp.addSection(getString(R.string.plugin_config), plugins);

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListAdapter(adp);
    }

    public void onStart() {
        super.onStart();
        update();
    }

    private void update() {
        if (!app.hasConnection())
            return;

        app.setProgress(true);

        GuiTask task = new GuiTask(() -> {
            Client client;
            try {
                client = app.getClient();
                generalData = client.getConfig();
                pluginData = client.getPluginConfig();
            } catch (TException e) {
                throw new RuntimeException(e);
            }
        }, () -> {
            general.setData(generalData);
            plugins.setData(pluginData);
            adp.notifyDataSetChanged();

            app.setProgress(false);
        });

        app.addTask(task);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Entry<String, ConfigSection> item = (Entry<String, ConfigSection>) adp
                .getItem(position);

        FragmentTransaction ft = getFragmentManager().beginTransaction();

        Bundle args = new Bundle();
        if (position > generalData.size())
            args.putString("type", "plugin");
        else
            args.putString("type", "core");
        args.putSerializable("section", item.getValue());

        Fragment f = new ConfigSectionFragment(mRefresh);
        f.setArguments(args);

        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        ft.addToBackStack(null);

        ft.replace(R.id.layout_root, f);
        ft.commit();
    }

}

class SettingsAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private ArrayList<Entry<String, ConfigSection>> data;

    SettingsAdapter(@NonNull pyLoadApp app) {
        layoutInflater = LayoutInflater.from(app);

        data = new ArrayList<>();
    }

    public void setData(Map<String, ConfigSection> map) {
        this.data = new ArrayList<>(map.entrySet());
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int arg0) {
        return data.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int row, View convertView, ViewGroup viewGroup) {

        ViewHolder holder;

        if (convertView == null) {

            convertView = layoutInflater.inflate(R.layout.settings_item, null);

            holder = new ViewHolder();

            holder.name = convertView.findViewById(R.id.section);
            holder.desc = convertView
                    .findViewById(R.id.section_desc);

            convertView.setTag(holder);

        }

        ConfigSection section = data.get(row).getValue();
        holder = (ViewHolder) convertView.getTag();

        holder.name.setText(section.description);

        if (section.outline != null) {
            holder.desc.setText(section.outline);
            holder.desc.setMaxHeight(100);
        } else {
            holder.desc.setMaxHeight(0);
        }

        return convertView;
    }

    private static class ViewHolder {
        private TextView name;
        private TextView desc;
    }

}
