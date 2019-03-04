package org.pyload.android.client.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.ListFragment
import org.apache.thrift.TException
import org.pyload.android.client.R
import org.pyload.android.client.module.GuiTask
import org.pyload.android.client.module.SeparatedListAdapter
import org.pyload.android.client.pyLoadApp
import org.pyload.thrift.ConfigSection
import kotlin.collections.Map.Entry

class SettingsFragment : ListFragment() {

    private lateinit var app: pyLoadApp
    private lateinit var adp: SeparatedListAdapter
    private lateinit var general: SettingsAdapter
    private lateinit var plugins: SettingsAdapter
    private var generalData: Map<String, ConfigSection>? = null
    private var pluginData: Map<String, ConfigSection>? = null
    private val mRefresh = Runnable { this@SettingsFragment.update() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.settings_list, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.run {
            app = applicationContext as pyLoadApp

            adp = SeparatedListAdapter(app)

            general = SettingsAdapter(app)
            plugins = SettingsAdapter(app)

            adp.addSection(getString(R.string.general_config), general)
            adp.addSection(getString(R.string.plugin_config), plugins)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listAdapter = adp
    }

    override fun onStart() {
        super.onStart()
        update()
    }

    private fun update() {
        if (!app.hasConnection()) {
            return
        }

        app.setProgress(true)

        val task = GuiTask(Runnable {
            try {
                val client = app.getClient()
                generalData = client.config
                pluginData = client.pluginConfig
            } catch (e: TException) {
                throw RuntimeException(e)
            }
        }, Runnable {
            general.setData(generalData ?: emptyMap())
            plugins.setData(pluginData ?: emptyMap())
            adp.notifyDataSetChanged()

            app.setProgress(false)
        })

        app.addTask(task)
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)
        fragmentManager?.run {
            val item = adp.getItem(position) as Entry<String, ConfigSection>

            val ft = beginTransaction()

            val args = Bundle()
            if (position > generalData?.size ?: 0) {
                args.putString("type", "plugin")
            } else {
                args.putString("type", "core")
            }
            args.putSerializable("section", item.value)

            val f = ConfigSectionFragment(mRefresh)
            f.arguments = args

            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)

            ft.addToBackStack(null)

            ft.replace(R.id.layout_root, f)
            ft.commit()
        }
    }
}

internal class SettingsAdapter(context: Context) : BaseAdapter() {
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private var data: ArrayList<Entry<String, ConfigSection>> = ArrayList()

    fun setData(map: Map<String, ConfigSection>) {
        this.data = ArrayList(map.entries)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): Any {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(row: Int, convertView: View?, viewGroup: ViewGroup): View {
        val view = convertView ?: let {
            val result = layoutInflater.inflate(R.layout.settings_item, viewGroup, false)
            val holder = ViewHolder(result.findViewById(R.id.section),
                    result.findViewById(R.id.section_desc))
            result.tag = holder
            result
        }

        val holder = view.tag as ViewHolder

        val section = data[row].value

        holder.name.text = section.description

        if (section.outline != null) {
            holder.desc.text = section.outline
            holder.desc.maxHeight = 100
        } else {
            holder.desc.maxHeight = 0
        }

        return view
    }

    private data class ViewHolder(val name: TextView,
                                  val desc: TextView)

}