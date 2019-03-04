package org.pyload.android.client.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.content.DialogInterface.OnDismissListener
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import org.apache.commons.codec.binary.Base64
import org.pyload.android.client.R
import org.pyload.android.client.pyLoad
import org.pyload.thrift.CaptchaTask

class CaptchaDialog : DialogFragment() {

    private var listener: OnDismissListener? = null
    private lateinit var task: CaptchaTask
    private lateinit var text: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        task = arguments?.getSerializable("task") as CaptchaTask
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val dialog = inflater.inflate(R.layout.captcha_dialog, container,
                false)
        text = dialog.findViewById(R.id.text)

        val image = dialog.findViewById<ImageView>(R.id.image)

        val decoded = Base64.decodeBase64(task.getData())

        val bm = BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
        image.setImageBitmap(bm)

        val enter = dialog.findViewById<Button>(R.id.enter)

        enter.setOnClickListener {
            this@CaptchaDialog.onClick()
            dismiss()
        }

        val cancel = dialog.findViewById<Button>(R.id.cancel)

        cancel.setOnClickListener { dismiss() }

        return dialog
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setTitle(getString(R.string.captcha_dialog_titel))
        return dialog
    }

    private fun onClick() {
        (activity as pyLoad).setCaptchaResult(task.tid, text.text.toString())
    }

    fun setOnDismissListener(listener: OnDismissListener?) {
        this.listener = listener
    }

    override fun onDismiss(dialog: DialogInterface) {
        listener?.onDismiss(dialog)
        // clear reference
        listener = null
    }

    companion object {
        @JvmStatic
        fun newInstance(task: CaptchaTask): CaptchaDialog {
            val dialog = CaptchaDialog()
            val args = Bundle()
            args.putSerializable("task", task)
            dialog.arguments = args
            return dialog
        }
    }
}