package org.pyload.android.client.module;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.pyload.android.client.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FileChooser extends ListActivity {

    public final static int CHOOSE_FILE = 0;
    public final static String SD_CARD = Environment.getExternalStorageDirectory().getPath();
    private File currentDir;
    private FileArrayAdapter adapter;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentDir = new File(SD_CARD);
        fill(currentDir);
    }

    private void fill(@NonNull File f) {
        File[] dirs = f.listFiles();
        this.setTitle(getString(R.string.current_dir) + f.getName());
        List<Option> dir = new ArrayList<>();
        List<Option> fls = new ArrayList<>();
        try {
            for (File ff : dirs) {
                if (ff.isDirectory())
                    dir.add(new Option(ff.getName(), getString(R.string.folder), ff
                            .getAbsolutePath()));
                else {
                    fls.add(new Option(ff.getName(), getString(R.string.file_size)
                            + Utils.formatSize(ff.length()), ff.getAbsolutePath()));
                }
            }
        } catch (Exception e) {
            // ignore
        }
        Collections.sort(dir);
        Collections.sort(fls);
        dir.addAll(fls);
        if (!f.getName().equalsIgnoreCase("sdcard"))
            dir.add(0, new Option("..", getString(R.string.parent_dir), f.getParent()));
        adapter = new FileArrayAdapter(FileChooser.this, R.layout.file_view,
                dir);
        this.setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);
        Option o = adapter.getItem(position);
        if (o.getData().equalsIgnoreCase(getString(R.string.folder))
                || o.getData().equalsIgnoreCase(getString(R.string.parent_dir))) {
            currentDir = new File(o.getPath());
            fill(currentDir);
        } else {
            onFileClick(o);
        }
    }

    private void onFileClick(@NonNull Option o) {
        Intent intent = new Intent();
        intent.putExtra("filepath", o.getPath());
        intent.putExtra("filename", o.getName());
        setResult(RESULT_OK, intent);
        finish();
    }
}

class Option implements Comparable<Option> {
    private String name;
    private String data;
    private String path;

    Option(String n, String d, String p) {
        name = n;
        data = d;
        path = p;
    }

    public String getName() {
        return name;
    }

    public String getData() {
        return data;
    }

    String getPath() {
        return path;
    }

    @Override
    public int compareTo(Option o) {
        if (this.name != null)
            return this.name.toLowerCase().compareTo(o.getName().toLowerCase());
        else
            throw new IllegalArgumentException();
    }
}

class FileArrayAdapter extends ArrayAdapter<Option> {
    @NonNull
    private final Context c;
    private int id;
    @NonNull
    private final List<Option> items;

    FileArrayAdapter(@NonNull Context context, int textViewResourceId,
                     @NonNull List<Option> objects) {
        super(context, textViewResourceId, objects);
        c = context;
        id = textViewResourceId;
        items = objects;
    }

    @Override
    public Option getItem(int i) {
        return items.get(i);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = LayoutInflater.from(c);
            v = vi.inflate(id, null);
        }
        final Option o = items.get(position);
        if (o != null) {
            TextView t1 = v.findViewById(R.id.TextView01);
            TextView t2 = v.findViewById(R.id.TextView02);

            if (t1 != null)
                t1.setText(o.getName());
            if (t2 != null)
                t2.setText(o.getData());

        }
        return v;
    }
}
