package org.pyload.android.client.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.pyload.android.client.R;
import org.pyload.thrift.FileData;
import org.pyload.thrift.PackageData;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class FileInfoDialog extends DialogFragment {

    private PackageData pack = null;
    private FileData file = null;

    public static FileInfoDialog newInstance(PackageData pack, FileData file) {

        FileInfoDialog dialog = new FileInfoDialog();
        Bundle args = new Bundle();

        args.putSerializable("pack", pack);
        args.putSerializable("file", file);

        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pack = (PackageData) getArguments().getSerializable("pack");
        file = (FileData) getArguments().getSerializable("file");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View dialog = inflater.inflate(R.layout.fileinfo_dialog, container, false);

        TextView view = dialog.findViewById(R.id.name);
        view.setText(file.name);

        view = dialog.findViewById(R.id.status);
        view.setText(file.statusmsg);

        view = dialog.findViewById(R.id.plugin);
        view.setText(file.plugin);

        view = dialog.findViewById(R.id.size);
        view.setText(file.format_size);

        view = dialog.findViewById(R.id.error);
        view.setText(file.error);

        view = dialog.findViewById(R.id.packageValue);
        view.setText(pack.name);

        view = dialog.findViewById(R.id.folder);
        view.setText(pack.folder);

        Button button = dialog.findViewById(R.id.close);
        button.setOnClickListener(v -> dismiss());

        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle(R.string.fileinfo_title);
        return dialog;
    }
}
