package org.pyload.android.client.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fileinfo_dialog.*
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

        name.text = file.name
        status.text = file.statusmsg
        plugin.text = file.plugin
        size.text = file.format_size
        error.text = file.error
        packageValue.text = pack.name
        folder.text = pack.folder
        close.setOnClickListener { dismiss() }

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