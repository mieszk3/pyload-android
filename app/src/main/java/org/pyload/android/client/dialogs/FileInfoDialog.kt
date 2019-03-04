package org.pyload.android.client.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import org.pyload.android.client.R
import org.pyload.thrift.FileData
import org.pyload.thrift.PackageData

class FileInfoDialog : DialogFragment() {
    private lateinit var pack: PackageData
    private lateinit var file: FileData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pack = arguments?.getSerializable("pack") as PackageData
        file = arguments?.getSerializable("file") as FileData
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val dialog = inflater.inflate(R.layout.fileinfo_dialog, container, false)

        var view = dialog.findViewById<TextView>(R.id.name)
        view.text = file.name

        view = dialog.findViewById(R.id.status)
        view.text = file.statusmsg

        view = dialog.findViewById(R.id.plugin)
        view.text = file.plugin

        view = dialog.findViewById(R.id.size)
        view.text = file.format_size

        view = dialog.findViewById(R.id.error)
        view.text = file.error

        view = dialog.findViewById(R.id.packageValue)
        view.text = pack.name

        view = dialog.findViewById(R.id.folder)
        view.text = pack.folder

        val button = dialog.findViewById<Button>(R.id.close)
        button.setOnClickListener { dismiss() }

        return dialog
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setTitle(R.string.fileinfo_title)
        return dialog
    }

    companion object {

        @JvmStatic
        fun newInstance(pack: PackageData, file: FileData): FileInfoDialog {
            val dialog = FileInfoDialog()
            val args = Bundle()

            args.putSerializable("pack", pack)
            args.putSerializable("file", file)

            dialog.arguments = args
            return dialog
        }
    }
}