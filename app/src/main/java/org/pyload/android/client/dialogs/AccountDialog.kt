package org.pyload.android.client.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import org.apache.thrift.TException
import org.pyload.android.client.R
import org.pyload.android.client.module.GuiTask
import org.pyload.android.client.module.Utils
import org.pyload.android.client.pyLoadApp
import org.pyload.thrift.AccountInfo
import org.pyload.thrift.Pyload
import java.text.SimpleDateFormat
import java.util.*

class AccountDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val adapter = AccountAdapter(context!!)

        val app = activity?.application as pyLoadApp
        val task = GuiTask(Runnable {
            val client: Pyload.Client
            try {
                client = app.getClient()
                adapter.setData(client.getAccounts(false))
            } catch (e: TException) {
                throw RuntimeException(e)
            }
        })
        app.addTask(task)

        val lv = ListView(context)
        lv.adapter = adapter

        val builder = AlertDialog.Builder(context)
        builder.setCancelable(true)
                .setView(lv)
                .setTitle(R.string.accounts)
                .setPositiveButton(R.string.close, null)
        return builder.create()
    }
}

internal class AccountAdapter(context: Context) : BaseAdapter() {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private var data: List<AccountInfo> = ArrayList()

    private data class ViewHolder(val type: TextView,
                                  val name: TextView,
                                  val valid: TextView,
                                  val validUntil: TextView,
                                  val trafficLeft: TextView)

    override fun getCount(): Int {
        // this is a hack to show empty list item in getView()
        return if (data.isNotEmpty()) data.size else 1
    }

    fun setData(accounts: List<AccountInfo>) {
        data = accounts
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Any {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(pos: Int, convertView: View?, group: ViewGroup): View {
        val acc = (if (data.isEmpty()) null else data[pos])
                ?: return layoutInflater.inflate(R.layout.account_empty_list, group, false)
        // here comes the empty list view

        val view = convertView ?: let {
            val result = layoutInflater.inflate(R.layout.account_item, group, false)
            val holder = ViewHolder(result.findViewById(R.id.type),
                    result.findViewById(R.id.name),
                    result.findViewById(R.id.valid),
                    result.findViewById(R.id.validuntil),
                    result.findViewById(R.id.trafficleft))
            result.tag = holder
            result
        }

        val holder = view.tag as ViewHolder

        holder.type.text = acc.type
        holder.name.text = acc.login

        if (acc.valid) {
            holder.valid.setText(R.string.valid)
        } else {
            holder.valid.setText(R.string.invalid)
        }

        if (acc.trafficleft < 0) {
            holder.trafficLeft.setText(R.string.unlimited)
        } else {
            holder.trafficLeft.text = Utils.formatSize(acc.trafficleft)
        }

        if (acc.validuntil < 0) {
            holder.validUntil.setText(R.string.unlimited)
        } else {
            val date = Date()
            date.time = acc.validuntil * 1000
            val formatter = SimpleDateFormat("dd.MM.yyyy")
            holder.validUntil.text = formatter.format(date)
        }

        return view
    }
}