package org.pyload.android.client.fragments

import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnDismissListener
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.view.ContextMenu.ContextMenuInfo
import android.widget.AdapterView.AdapterContextMenuInfo
import android.widget.BaseAdapter
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.ListFragment
import org.apache.thrift.TException
import org.pyload.android.client.R
import org.pyload.android.client.components.TabHandler
import org.pyload.android.client.dialogs.CaptchaDialog
import org.pyload.android.client.module.GuiTask
import org.pyload.android.client.module.Utils
import org.pyload.android.client.pyLoadApp
import org.pyload.thrift.CaptchaTask
import org.pyload.thrift.DownloadInfo
import org.pyload.thrift.DownloadStatus
import org.pyload.thrift.Pyload.Client
import org.pyload.thrift.ServerStatus

class OverviewFragment : ListFragment(), OnDismissListener, TabHandler {

    private val mHandler = Handler()
    private lateinit var app: pyLoadApp
    private lateinit var adp: OverviewAdapter
    private var downloads: List<DownloadInfo> = emptyList()
    private lateinit var status: ServerStatus
    private var captcha: CaptchaTask? = null
    private var lastCaptcha = -1
    private var interval = 5
    private var update = false
    private val mUpdateTimeTask = object : Runnable {
        override fun run() {
            refresh()
            if (update) {
                mHandler.postDelayed(this, (interval * 1000).toLong())
            }
        }
    }
    private val cancelUpdate = Runnable { stopUpdate() }
    private var dialogOpen = false
    // tab position
    private var pos = -1
    /**
     * GUI Elements
     */
    private lateinit var statusServer: TextView
    private lateinit var reconnect: TextView
    private lateinit var speed: TextView
    private lateinit var active: TextView
    private val mUpdateResults = Runnable { this.onDataReceived() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        app = activity!!.applicationContext as pyLoadApp

        downloads = ArrayList()
        adp = OverviewAdapter(app, R.layout.overview_item, downloads)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val v = inflater.inflate(R.layout.overview, container, false)

        statusServer = v.findViewById(R.id.status_server)
        reconnect = v.findViewById(R.id.reconnect)
        speed = v.findViewById(R.id.speed)
        active = v.findViewById(R.id.active)

        // toggle pause on click
        statusServer.setOnClickListener {
            app.addTask(GuiTask(Runnable {
                val client: Client
                try {
                    client = app.getClient()
                    client.togglePause()
                } catch (e: TException) {
                    throw RuntimeException(e)
                }
            }, app.handleSuccess))
        }

        // toggle reconnect on click
        reconnect.setOnClickListener {
            app.addTask(GuiTask(Runnable {
                val client: Client
                try {
                    client = app.getClient()
                    client.toggleReconnect()
                } catch (e: TException) {
                    throw RuntimeException(e)
                }
            }, app.handleSuccess))
        }

        if (::status.isInitialized) {
            onDataReceived()
        }

        registerForContextMenu(v.findViewById(android.R.id.list))

        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listAdapter = adp
    }

    override fun onStart() {
        super.onStart()
        onSelected()
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View,
                                     menuInfo: ContextMenuInfo?) {
        val activity = activity
        if (activity != null) {
            val inflater = activity.menuInflater
            inflater.inflate(R.menu.overview_context_menu, menu)
            menu.setHeaderTitle(R.string.choose_action)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {

        if (!app.isCurrentTab(pos)) {
            return false
        }

        val menuInfo = item
                .menuInfo as AdapterContextMenuInfo
        val id = menuInfo.position
        val info = downloads[id]
        when (item.itemId) {
            R.id.abort -> {

                app.addTask(GuiTask(Runnable {
                    try {
                        val client = app.getClient()
                        val fids = ArrayList<Int>()
                        fids.add(info.fid)
                        client.stopDownloads(fids)
                    } catch (e: TException) {
                        throw RuntimeException(e)
                    }
                }, Runnable { this.refresh() }))
                return true
            }

            else -> return super.onContextItemSelected(item)
        }

    }

    override fun onSelected() {
        startUpdate()
    }

    override fun onDeselected() {
        stopUpdate()
    }

    private fun startUpdate() {
        // already update running
        if (update)
            return
        interval = try {
            Integer.parseInt(app.prefs.getString("refresh_rate", "5")!!)
        } catch (e: NumberFormatException) {
            // somehow contains illegal value
            5
        }

        update = true
        mHandler.post(mUpdateTimeTask)
    }

    private fun stopUpdate() {
        update = false
        mHandler.removeCallbacks(mUpdateTimeTask)
    }

    /**
     * Called when Status data received
     */
    private fun onDataReceived() {
        val adapter = listAdapter
        if (adapter is OverviewAdapter) {
            adapter.setDownloads(downloads)

            statusServer.text = app.verboseBool(status.download)
            reconnect.text = app.verboseBool(status.reconnect)
            speed.text = "${Utils.formatSize(status.speed)}/s"
            active.text = String.format("%d / %d", status.active, status.total)

            val captchaTask = captcha
            if (captchaTask != null && app.prefs.getBoolean("pull_captcha", true)
                    && captchaTask.resultType == "textual"
                    && lastCaptcha != captchaTask.tid.toInt()) {
                showDialog()
            }
        }
    }

    fun refresh() {
        if (!app.hasConnection())
            return

        val task = GuiTask(Runnable {
            try {
                val client = app.getClient()
                downloads = client.statusDownloads()
                status = client.statusServer()
                if (client.isCaptchaWaiting) {
                    Log.d("pyLoad", "Captcha available")
                    captcha = client.getCaptchaTask(false)
                    Log.d("pyload", captcha!!.resultType)
                }
            } catch (e: TException) {
                throw RuntimeException(e)
            }
        }, mUpdateResults)
        task.critical = cancelUpdate

        app.addTask(task)
    }

    private fun showDialog() {

        if (dialogOpen || captcha == null)
            return

        val dialog = CaptchaDialog.newInstance(captcha!!)
        lastCaptcha = captcha!!.tid.toInt()

        Log.d("pyLoad", "Got Captcha Task")

        dialog.setOnDismissListener(this)

        dialogOpen = true
        try {
            val fragmentManager = fragmentManager
            if (fragmentManager != null && !fragmentManager.isStateSaved) {
                dialog.show(fragmentManager, CaptchaDialog::class.java.name)
            }
        } catch (e: IllegalStateException) {
            dialogOpen = false
            // seems to appear when overview is already closed
            Log.e("pyLoad", "Dialog state error", e)
        } catch (e: NullPointerException) {
            dialogOpen = false
            // something is null, but why?
            Log.e("pyLoad", "Dialog null pointer error", e)
        }

    }

    override fun onDismiss(arg0: DialogInterface?) {
        captcha = null
        dialogOpen = false
    }

    override fun setPosition(pos: Int) {
        this.pos = pos
    }
}

/**
 * Renders the single ListView items
 *
 * @author RaNaN
 */
internal class OverviewAdapter(context: Context, private val rowResID: Int,
                               private var downloads: List<DownloadInfo>) : BaseAdapter() {
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    fun setDownloads(downloads: List<DownloadInfo>) {
        this.downloads = downloads
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return downloads.size
    }

    override fun getItem(id: Int): Any? {
        return downloads[id]
    }

    override fun getItemId(pos: Int): Long {
        return pos.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val resultView = convertView ?: let {
            val view = layoutInflater.inflate(rowResID, null)
            val holder = ViewHolder(
                    view.findViewById(R.id.name),
                    view.findViewById(R.id.progress),
                    view.findViewById(R.id.size),
                    view.findViewById(R.id.speed),
                    view.findViewById(R.id.size_done),
                    view.findViewById(R.id.eta),
                    view.findViewById(R.id.percent))
            view.tag = holder
            view
        }
        val info = downloads[position]

        val holder = resultView.tag as ViewHolder

        // name is null sometimes somehow
        if (info.name != null && info.name?.contentEquals(holder.name.text) == false) {
            holder.name.text = info.name
        }

        holder.progress.progress = info.percent.toInt()

        when {
            info.status == DownloadStatus.Downloading -> {
                holder.size.text = Utils.formatSize(info.size)
                holder.percent.text = "${info.percent}%"
                holder.size_done.text = Utils.formatSize(info.size - info.bleft)

                holder.speed.text = "${Utils.formatSize(info.speed)}/s"
                holder.eta.text = info.format_eta

            }
            info.status == DownloadStatus.Waiting -> {
                holder.size.setText(R.string.lambda)
                holder.percent.setText(R.string.lambda)
                holder.size_done.setText(R.string.lambda)
                holder.speed.text = info.statusmsg
                holder.eta.text = info.format_wait
            }
            else -> {
                holder.size.setText(R.string.lambda)
                holder.percent.setText(R.string.lambda)
                holder.size_done.setText(R.string.lambda)
                holder.speed.text = info.statusmsg
                holder.eta.setText(R.string.lambda)
            }
        }

        return resultView
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    private class ViewHolder(
            val name: TextView,
            val progress: ProgressBar,
            val size: TextView,
            val percent: TextView,
            val size_done: TextView,
            val speed: TextView,
            val eta: TextView)
}