package org.pyload.android.client.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import org.apache.thrift.TException
import org.pyload.android.client.R
import org.pyload.android.client.module.GuiTask
import org.pyload.android.client.pyLoadApp
import org.pyload.thrift.ConfigItem
import org.pyload.thrift.ConfigSection

class ConfigSectionFragment constructor(
        /**
         * Called after settings were saved
         */
        private val mRefresh: Runnable) : Fragment() {
    private lateinit var app: pyLoadApp
    private lateinit var section: ConfigSection
    private var type: String? = null
    private val items: MutableMap<String, ConfigItemView> = HashMap()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.config_section, container, false)
        createLayout(view)
        val t = view.findViewById<TextView>(R.id.list_header_title)
        t.text = section.description

        view.findViewById<View>(R.id.button_submit)
                .setOnClickListener { onSubmit() }

        view.findViewById<View>(R.id.button_cancel)
                .setOnClickListener { onCancel() }

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.run {
            val appContext = applicationContext
            if (appContext is pyLoadApp) {
                app = appContext
            }
        }

        arguments?.run {
            section = getSerializable("section") as ConfigSection
            type = getString("type")
        }
    }

    private fun createLayout(view: View) {
        val ll = view.findViewById<LinearLayout>(R.id.layout_root)
        ll.orientation = LinearLayout.VERTICAL

        val context = context
        if (context != null) {
            for (item in section.items) {
                val c = ConfigItemView(context, item)
                items[item.name] = c
                ll.addView(c)
            }
        }
    }

    private fun onSubmit() {

        if (!app.hasConnection()) {
            return
        }

        app.addTask(GuiTask(Runnable {
            try {
                val client = app.getClient()

                for (item in section.items) {
                    val view = items[item.name]
                    if (view != null) {
                        val newValue = view.value
                        if (item.value != newValue) {
                            Log.d("pyLoad", String.format(
                                    "Set config value: %s, %s, %s", type,
                                    section.name, item.name))

                            client.setConfigValue(section.name, item.name,
                                    newValue, type)
                        }
                    }
                }
            } catch (e: TException) {
                // ignore
            }

            fragmentManager?.popBackStack()

        }, mRefresh))
    }

    private fun onCancel() {
        fragmentManager?.popBackStack()
    }
}

internal class ConfigItemView(context: Context, private val item: ConfigItem) : LinearLayout(context) {
    private val v: View
    private var sp: Spinner? = null
    private var choices: MutableList<String>? = null

    /**
     * Returns the string representation of the config item
     *
     * @return value
     */
    val value: String
        get() {
            if (item.type == "bool") {
                val cb = v as CheckBox
                return if (cb.isChecked) {
                    "True"
                } else {
                    "False"
                }
            } else {
                sp?.let { spinner ->
                    choices?.let {
                        return it[spinner.selectedItemPosition]
                    }
                }
            }
            return (v as EditText).text.toString()
        }

    init {

        orientation = VERTICAL

        if (item.type != "bool") {
            val tv = TextView(context)
            tv.text = item.description
            tv.setTextColor(Color.WHITE)
            tv.textSize = 16f
            tv.setPadding(2, 0, 0, 0)
            addView(tv)
        }

        when {
            item.type == "int" -> {
                val et = EditText(context)
                et.inputType = InputType.TYPE_CLASS_NUMBER
                et.setText(item.value)
                v = et
            }
            item.type == "password" -> {
                val et = EditText(context)
                et.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                et.transformationMethod = PasswordTransformationMethod
                        .getInstance()
                et.setText(item.value)
                v = et
            }
            item.type == "bool" -> {
                val cb = CheckBox(context)
                cb.text = item.description

                if (item.value == "True") {
                    cb.isChecked = true
                }

                v = cb
            }
            item.type.contains(";") -> {
                sp = Spinner(context).also { spinner ->
                    choices = mutableListOf<String>().apply {
                        addAll(item.type.split(";".toRegex()).dropLastWhile { it.isEmpty() })

                        val adp = ArrayAdapter(context,
                                android.R.layout.simple_spinner_item, this)
                        adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                        spinner.adapter = adp
                        spinner.setSelection(indexOf(item.value))
                    }

                    v = spinner
                }
            }
            else -> {
                v = EditText(context).also {
                    it.setText(item.value)
                }
            }
        }

        addView(v)
    }
}