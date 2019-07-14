package org.pyload.android.client.module

import android.app.Activity
import android.app.ListActivity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import org.pyload.android.client.R
import java.io.File

class FileChooser : ListActivity() {
    private lateinit var currentDir: File
    private lateinit var adapter: FileArrayAdapter

    @Suppress("DEPRECATION")
    private val sdCard: String by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.path ?: ""
        } else {
            Environment.getExternalStorageDirectory().path
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentDir = File(sdCard)
        fill(currentDir)
    }

    private fun fill(f: File) {
        val dirs = f.listFiles() ?: emptyArray()
        this.title = getString(R.string.current_dir) + f.name
        val dir = ArrayList<Option>()
        val fls = ArrayList<Option>()
        try {
            for (ff in dirs) {
                if (ff.isDirectory) {
                    dir.add(Option(ff.name, getString(R.string.folder), ff
                            .absolutePath))
                } else {
                    fls.add(Option(ff.name, getString(R.string.file_size) + Utils.formatSize(ff.length()), ff.absolutePath))
                }
            }
        } catch (e: Exception) {
            // ignore
        }

        dir.sort()
        fls.sort()
        dir.addAll(fls)
        if (!f.name.equals("sdcard", ignoreCase = true)) {
            dir.add(0, Option("..", getString(R.string.parent_dir), f.parent ?: ""))
        }
        adapter = FileArrayAdapter(this@FileChooser, R.layout.file_view,
                dir)
        this.listAdapter = adapter
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id)
        val o = adapter.getItem(position)
        if (o != null) {
            if (o.data.equals(getString(R.string.folder), ignoreCase = true) || o.data.equals(getString(R.string.parent_dir), ignoreCase = true)) {
                currentDir = File(o.path)
                fill(currentDir)
            } else {
                onFileClick(o)
            }
        }
    }

    private fun onFileClick(o: Option) {
        val intent = Intent()
        intent.putExtra("filepath", o.path)
        intent.putExtra("filename", o.name)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    companion object {
        const val CHOOSE_FILE = 0
    }
}

internal class Option(val name: String, val data: String, val path: String) : Comparable<Option> {

    override fun compareTo(other: Option): Int {
        return name.toLowerCase().compareTo(other.name.toLowerCase())
    }
}

internal class FileArrayAdapter(private val c: Context, private val id: Int,
                                private val items: List<Option?>) : ArrayAdapter<Option>(c, id, items) {

    override fun getItem(i: Int): Option? {
        return items[i]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v = convertView ?: LayoutInflater.from(c).inflate(id, null)

        val o = items[position]
        if (o != null) {
            val t1 = v.findViewById<TextView>(R.id.TextView01)
            val t2 = v.findViewById<TextView>(R.id.TextView02)

            if (t1 != null) {
                t1.text = o.name
            }
            if (t2 != null) {
                t2.text = o.data
            }
        }
        return v
    }
}