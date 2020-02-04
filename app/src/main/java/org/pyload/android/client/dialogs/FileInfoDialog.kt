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

        dialog.findViewById<TextView>(R.id.name).text = file.name
        dialog.findViewById<TextView>(R.id.status).text = file.statusmsg
        dialog.findViewById<TextView>(R.id.plugin).text = file.plugin
        dialog.findViewById<TextView>(R.id.size).text = file.format_size
        dialog.findViewById<TextView>(R.id.error).text = file.error
        dialog.findViewById<TextView>(R.id.packageValue).text = pack.name
        dialog.findViewById<TextView>(R.id.folder).text = pack.folder
        dialog.findViewById<Button>(R.id.close).setOnClickListener { dismiss() }

        return dialog
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setTitle(R.string.fileinfo_title)
        return dialog
    }

    companion object {

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