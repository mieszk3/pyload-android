// http://jsharkey.org/blog/2008/08/18/separating-lists-with-headers-in-android-09/

package org.pyload.android.client.module

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import org.pyload.android.client.R

class SeparatedListAdapter(context: Context) : BaseAdapter() {
    private val sections = LinkedHashMap<String, Adapter>()
    private val headers: ArrayAdapter<String> = ArrayAdapter(context, R.layout.list_header)

    fun addSection(section: String, adapter: Adapter) {
        this.headers.add(section)
        this.sections[section] = adapter
    }

    override fun getItem(position: Int): Any? {
        var positionVariable = position
        for (section in this.sections.keys) {
            val adapter = sections[section]
            if (adapter != null) {
                val size = adapter.count + 1

                // check if position inside this section
                if (positionVariable == 0) return section
                if (positionVariable < size) return adapter.getItem(positionVariable - 1)

                // otherwise jump into next section
                positionVariable -= size
            }
        }
        return null
    }

    override fun getCount(): Int {
        // total together all sections, plus one for each section header
        var total = 0
        for (adapter in this.sections.values) {
            total += adapter.count + 1
        }
        return total
    }

    override fun getViewTypeCount(): Int {
        // assume that headers count as one, then total all sections
        var total = 1
        for (adapter in this.sections.values) {
            total += adapter.viewTypeCount
        }
        return total
    }

    override fun getItemViewType(position: Int): Int {
        var positionVariable = position
        var type = 1
        for (section in this.sections.keys) {
            val adapter = sections[section]
            if (adapter != null) {
                val size = adapter.count + 1

                // check if position inside this section
                if (positionVariable == 0) return TYPE_SECTION_HEADER
                if (positionVariable < size) return type + adapter.getItemViewType(positionVariable - 1)

                // otherwise jump into next section
                positionVariable -= size
                type += adapter.viewTypeCount
            }
        }
        return -1
    }

    override fun isEnabled(position: Int): Boolean {
        return getItemViewType(position) != TYPE_SECTION_HEADER
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var positionVariable = position
        for ((sectionnum, section) in this.sections.keys.withIndex()) {
            val adapter = sections[section]
            if (adapter != null) {
                val size = adapter.count + 1

                // check if position inside this section
                if (positionVariable == 0) return headers.getView(sectionnum, convertView, parent)
                if (positionVariable < size) return adapter.getView(positionVariable - 1, convertView, parent)

                // otherwise jump into next section
                positionVariable -= size
            }
        }
        return null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    companion object {
        private const val TYPE_SECTION_HEADER = 0
    }
}