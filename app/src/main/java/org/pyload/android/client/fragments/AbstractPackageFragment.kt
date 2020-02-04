package org.pyload.android.client.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.ContextMenu.ContextMenuInfo
import android.widget.*
import android.widget.ExpandableListView.ExpandableListContextMenuInfo
import org.apache.thrift.TException
import org.pyload.android.client.R
import org.pyload.android.client.components.ExpandableListFragment
import org.pyload.android.client.components.TabHandler
import org.pyload.android.client.dialogs.FileInfoDialog
import org.pyload.android.client.module.GuiTask
import org.pyload.android.client.module.Utils
import org.pyload.android.client.pyLoadApp
import org.pyload.thrift.Destination
import org.pyload.thrift.DownloadStatus
import org.pyload.thrift.FileData
import org.pyload.thrift.PackageData

abstract class AbstractPackageFragment : ExpandableListFragment(), TabHandler {

    private val mOrderComparator = Comparator { a: Any?, b: Any? ->
        if (a == null && b == null) {
            0
        } else if (a == null) {
            1
        } else if (b == null) {
            -1
        } else if (a is PackageData && b is PackageData) {
            a.order.compareTo(b.order)
        } else if (a is FileData && b is FileData) {
            a.order.compareTo(b.order)
        } else {
            0
        }
    }
    protected var dest: Int = 0
    private lateinit var data: List<PackageData>
    private lateinit var app: pyLoadApp
    private val mUpdateResults = Runnable { this.onDataReceived() }
    // tab position
    private var pos = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activity = activity
        if (activity != null) {
            app = activity.applicationContext as pyLoadApp
        }
        data = ArrayList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        registerForContextMenu(view.findViewById(android.R.id.list))
        val adp = PackageListAdapter(requireContext(), data,
                R.layout.package_item, R.layout.package_child_item)
        setListAdapter(adp)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {

        Log.d("pyLoad", "$dest onContextItemSelected $item")

        // filter event und allow to proceed
        if (!app.isCurrentTab(pos))
            return false

        val info = item
                .menuInfo as ExpandableListContextMenuInfo

        val type = ExpandableListView
                .getPackedPositionType(info.packedPosition)
        if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            val groupPos = ExpandableListView
                    .getPackedPositionGroup(info.packedPosition)
            val childPos = ExpandableListView
                    .getPackedPositionChild(info.packedPosition)

            val file: FileData
            try {
                file = data[groupPos].links[childPos]
            } catch (e: IndexOutOfBoundsException) {
                return false
            }

            when (item.itemId) {
                R.id.restart ->

                    app.addTask(GuiTask(Runnable {
                        try {
                            val client = app.getClient()
                            client.restartFile(file.fid)
                        } catch (e: TException) {
                            throw RuntimeException(e)
                        }
                    }, app.handleSuccess))
                R.id.delete ->

                    app.addTask(GuiTask(Runnable {
                        try {
                            val client = app.getClient()
                            val fids = ArrayList<Int>()
                            fids.add(file.fid)

                            client.deleteFiles(fids)
                        } catch (e: TException) {
                            throw RuntimeException(e)
                        }
                    }, app.handleSuccess))

                R.id.move -> Toast.makeText(activity, R.string.cant_move_files,
                        Toast.LENGTH_SHORT).show()

                else -> {
                    // do nothing
                }
            }

            return true
        } else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
            val groupPos = ExpandableListView
                    .getPackedPositionGroup(info.packedPosition)

            val pack: PackageData
            try {
                pack = data[groupPos]
            } catch (e: IndexOutOfBoundsException) {
                return false // pack does not exists anymore
            }

            when (item.itemId) {
                R.id.restart ->

                    app.addTask(GuiTask(Runnable {
                        try {
                            val client = app.getClient()
                            client.restartPackage(pack.pid)
                        } catch (e: TException) {
                            throw RuntimeException(e)
                        }
                    }, app.handleSuccess))
                R.id.delete ->

                    app.addTask(GuiTask(Runnable {
                        try {
                            val client = app.getClient()
                            val pids = ArrayList<Int>()
                            pids.add(pack.pid)
                            client.deletePackages(pids)
                        } catch (e: TException) {
                            throw RuntimeException(e)
                        }
                    }, app.handleSuccess))

                R.id.move ->

                    app.addTask(GuiTask(Runnable {
                        try {
                            val client = app.getClient()
                            val newDest: Destination = if (dest == 0) {
                                Destination.Collector
                            } else {
                                Destination.Queue
                            }

                            client.movePackage(newDest, pack.pid)
                        } catch (e: TException) {
                            throw RuntimeException(e)
                        }
                    }, app.handleSuccess))

                else -> {
                    // do nothing
                }
            }

            return true
        }

        return false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.package_list, container, false)
    }

    override fun onChildClick(parent: ExpandableListView, v: View, groupPosition: Int,
                              childPosition: Int, id: Long): Boolean {
        val pack: PackageData
        val file: FileData
        try {
            pack = data[groupPosition]
            file = pack.links[childPosition]
        } catch (e: Exception) {
            return true
        }

        val dialog = FileInfoDialog.newInstance(pack, file)
        val fragmentManager = fragmentManager
        if (fragmentManager != null && !fragmentManager.isStateSaved) {
            dialog.show(fragmentManager, FileInfoDialog::class.java.name)
        }
        return true
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View,
                                     menuInfo: ContextMenuInfo?) {
        val activity = activity
        if (activity != null) {
            val inflater = activity.menuInflater
            inflater.inflate(R.menu.package_context_menu, menu)
            menu.setHeaderTitle(R.string.choose_action)
        }
    }

    override fun onSelected() {
        val activity = activity
        if (activity != null) {
            app = activity.applicationContext as pyLoadApp
            refresh()
        }
    }

    override fun onDeselected() {}

    override fun setPosition(pos: Int) {
        this.pos = pos
    }

    fun refresh() {
        if (!app.hasConnection())
            return

        app.setProgress(true)

        val task = GuiTask(Runnable {
            data = try {
                val client = app.getClient()
                if (dest == 0) {
                    client.queueData
                } else {
                    client.collectorData
                }
            } catch (e: TException) {
                throw RuntimeException(e)
            }
        }, mUpdateResults)

        app.addTask(task)
    }

    private fun onDataReceived() {
        app.setProgress(false)
        data.sortedWith(mOrderComparator)
        for (pak in data) {
            pak.links?.sortWith(mOrderComparator)
        }

        val adapter = expandableListAdapter as PackageListAdapter?
        adapter?.setData(data)
    }
}

internal class PackageListAdapter(context: Context, private var data: List<PackageData>,
                                  private val groupRes: Int, private val childRes: Int) : BaseExpandableListAdapter() {
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    fun setData(data: List<PackageData>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getGroupCount(): Int {
        return data.size
    }

    override fun getChildrenCount(group: Int): Int {
        return data[group].links.size
    }

    override fun getGroup(group: Int): Any? {
        return data[group]
    }

    override fun getChild(group: Int, child: Int): Any? {
        return data[group].links[child]
    }

    override fun getGroupId(group: Int): Long {
        return group.toLong()
    }

    override fun getChildId(group: Int, child: Int): Long {
        return child.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(group: Int, isExpanded: Boolean, convertView: View?,
                              parent: ViewGroup): View {
        val resultView = convertView ?: let {
            val view = layoutInflater.inflate(groupRes, null)
            val holder = GroupViewHolder(
                    name = view.findViewById(R.id.name),
                    progress = view.findViewById(R.id.package_progress),
                    size = view.findViewById(R.id.size_stats),
                    links = view.findViewById(R.id.link_stats))
            view.tag = holder
            view
        }

        val pack = data[group]

        val holder = resultView.tag as GroupViewHolder
        holder.name.text = pack.name

        if (pack.linkstotal.toInt() == 0) {
            pack.linkstotal = 1
        }

        holder.progress.progress = pack.linksdone * 100 / pack.links.size
        holder.size.text = (Utils.formatSize(pack.sizedone) + " / "
                + Utils.formatSize(pack.sizetotal))
        holder.links.text = "${pack.linksdone} / ${pack.links.size}"

        return resultView
    }

    override fun getChildView(group: Int, child: Int, isLastChild: Boolean,
                              convertView: View?, parent: ViewGroup): View? {
        val resultView = convertView ?: let {
            val view = layoutInflater.inflate(childRes, null)
            val holder = ChildViewHolder(
                    name = view.findViewById(R.id.name),
                    status = view.findViewById(R.id.status),
                    size = view.findViewById(R.id.size),
                    plugin = view.findViewById(R.id.plugin),
                    status_icon = view.findViewById(R.id.status_icon))
            view.tag = holder
            view
        }

        val file = data[group].links[child] ?: return null

        val holder = resultView.tag as ChildViewHolder

        // seems to occure according to bug report
        // no idea why, and what about other data, so returning the view instantly
        if (file.name == null) {
            holder.name.setText(R.string.lambda)
            return convertView
        }

        if (file.name?.contentEquals(holder.name.text) == false)
            holder.name.text = file.name

        holder.status.text = file.statusmsg
        holder.size.text = Utils.formatSize(file.size)
        holder.plugin.text = file.plugin

        if (file.status == DownloadStatus.Failed
                || file.status == DownloadStatus.Aborted
                || file.status == DownloadStatus.Offline) {
            holder.status_icon.setImageResource(R.drawable.stop)
        } else if (file.status == DownloadStatus.Finished) {
            holder.status_icon.setImageResource(R.drawable.tick)
        } else if (file.status == DownloadStatus.Waiting) {
            holder.status_icon.setImageResource(R.drawable.menu_clock)
        } else if (file.status == DownloadStatus.Skipped) {
            holder.status_icon.setImageResource(R.drawable.tag)
        } else {
            holder.status_icon.setImageResource(0)
        }
        return resultView
    }

    override fun isChildSelectable(group: Int, child: Int): Boolean {
        return true
    }

    private class GroupViewHolder(
            val name: TextView,
            val progress: ProgressBar,
            val size: TextView,
            val links: TextView)


    private class ChildViewHolder(
            val name: TextView,
            val status: TextView,
            val size: TextView,
            val plugin: TextView,
            val status_icon: ImageView)
}
