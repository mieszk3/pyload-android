package org.pyload.android.client

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import org.apache.thrift.TException
import org.pyload.android.client.components.FragmentTabsPager
import org.pyload.android.client.dialogs.AccountDialog
import org.pyload.android.client.fragments.CollectorFragment
import org.pyload.android.client.fragments.OverviewFragment
import org.pyload.android.client.fragments.QueueFragment
import org.pyload.android.client.module.Eula
import org.pyload.android.client.module.GuiTask
import org.pyload.thrift.Destination
import org.pyload.thrift.PackageDoesNotExists
import org.pyload.thrift.Pyload.Client
import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.util.*

class pyLoad : FragmentTabsPager() {

    private lateinit var app: pyLoadApp

    // keep reference to set indeterminateProgress
    var refreshItem: MenuItem? = null
        private set

    /**
     * Called when the activity is first created.
     */

    public override fun onCreate(savedInstanceState: Bundle?) {

        Log.d("pyLoad", "Starting pyLoad App")

        app = applicationContext as pyLoadApp
        app.prefs = PreferenceManager.getDefaultSharedPreferences(this)
        initLocale()

        super.onCreate(savedInstanceState)
        Eula.show(this)

        app.cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        app.init(this)

        mTabsAdapter.addTab(getString(R.string.overview), OverviewFragment::class.java)

        mTabsAdapter.addTab(getString(R.string.queue), QueueFragment::class.java)

        mTabsAdapter.addTab(getString(R.string.collector), CollectorFragment::class.java)
    }

    override fun onStart() {
        super.onStart()
        val intent = intent
        val action = intent.action
        val data = intent.data

        // we got a SHARE intent
        if (Intent.ACTION_SEND == action) {
            val addURL = Intent(app, AddLinksActivity::class.java)
            addURL.putExtra("url", intent.getStringExtra(Intent.EXTRA_TEXT))
            addURL.putExtra("name", intent.getStringExtra(Intent.EXTRA_SUBJECT))
            startActivityForResult(addURL, AddLinksActivity.NEW_PACKAGE)
            intent.action = Intent.ACTION_MAIN

            // we got a VIEW intent
        } else if (Intent.ACTION_VIEW == action && data != null) {
            val intentScheme = intent.scheme
            if (intentScheme != null) {
                if (intentScheme.startsWith("http") || intentScheme.contains("ftp")) {
                    val addURL = Intent(app, AddLinksActivity::class.java)
                    addURL.putExtra("url", data.toString())
                    startActivityForResult(addURL, AddLinksActivity.NEW_PACKAGE)
                } else if (intentScheme == "file") {
                    val addURL = Intent(app, AddLinksActivity::class.java)
                    addURL.putExtra("dlcpath", data.path)
                    startActivityForResult(addURL, AddLinksActivity.NEW_PACKAGE)
                }
            }
            intent.data = null
        }
    }

    override fun onResume() {
        super.onResume()
        app.refreshTab()
    }

    override fun onPause() {
        super.onPause()
        app.clearTasks()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        refreshItem = menu.findItem(R.id.refresh)?.also {
            it.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        }
        menu.findItem(R.id.add_links).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_links -> {
                startActivityForResult(Intent(app, AddLinksActivity::class.java),
                        AddLinksActivity.NEW_PACKAGE)

                return true
            }

            R.id.refresh -> {
                app.resetClient()
                app.refreshTab()

                return true
            }

            R.id.settings -> {
                val settingsActivity = Intent(app, Preferences::class.java)
                startActivity(settingsActivity)

                return true
            }

            R.id.show_accounts -> {
                val accountsList = AccountDialog()
                accountsList.show(supportFragmentManager, "accountsDialog")

                return true
            }

            R.id.remote_settings -> {
                val serverConfigActivity = Intent(app, RemoteSettings::class.java)
                startActivity(serverConfigActivity)

                return true
            }

            R.id.restart_failed -> {
                app.addTask(GuiTask(Runnable {
                    val client: Client
                    try {
                        client = app.getClient()
                        client.restartFailed()
                    } catch (e: TException) {
                        throw RuntimeException(e)
                    }
                }, app.handleSuccess))

                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when (requestCode) {
            AddLinksActivity.NEW_PACKAGE -> when (resultCode) {
                Activity.RESULT_OK -> if (data != null) {
                    val name = data.getStringExtra("name")
                    val linkArray = (data.getStringExtra("links") ?: "").trim { it <= ' ' }
                            .split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val dest: Destination = if (data.getIntExtra("dest", 0) == 0) {
                        Destination.Queue
                    } else {
                        Destination.Collector
                    }
                    val filepath = data.getStringExtra("filepath")
                    val filename = data.getStringExtra("filename")

                    val links = ArrayList<String>()
                    for (linkRow in linkArray)
                        for (link in linkRow.trim { it <= ' ' }.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
                            if (link != "")
                                links.add(link)

                    val password = data.getStringExtra("password")

                    app.addTask(GuiTask(Runnable {
                        val client: Client
                        try {
                            client = app.getClient()

                            if (links.size > 0) {
                                val pid = client.addPackage(name, links, dest)

                                if (password != null && password != "") {

                                    val opts = HashMap<String, String>()
                                    opts["password"] = password

                                    try {
                                        client.setPackageData(pid, opts)
                                    } catch (e: PackageDoesNotExists) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace()
                                    }

                                }
                            }
                        } catch (e: TException) {
                            throw RuntimeException(e)
                        }

                        if (filename != null && !filepath.isNullOrEmpty()) {

                            val file = File(filepath)
                            try {
                                if (file.length() > 1 shl 20)
                                    throw Exception("File size to large")
                                val inputStream = FileInputStream(file)
                                val buffer = ByteBuffer
                                        .allocate(file.length().toInt())

                                while (inputStream.channel.read(buffer) > 0);

                                buffer.rewind()
                                inputStream.close()
                                client.uploadContainer(filename, buffer)

                            } catch (e: Throwable) {
                                Log.e("pyLoad", "Error when uploading file", e)
                            }

                        }

                    }, app.handleSuccess))
                }
                else -> {
                    // do nothing
                }
            }

            else -> super.onActivityResult(requestCode, resultCode, data)
        }

    }

    override fun onNewIntent(intent: Intent?) {
        Log.d("pyLoad", "got Intent")
        super.onNewIntent(intent)
    }

    /**
     * Sets the locale defined in config.
     */
    private fun initLocale() {

        val language = app.prefs.getString("language", "")
        val locale = if (language.isNullOrEmpty()) {
            Locale.getDefault()
        } else {
            Locale(language)
        }

        Log.d("pyLoad", "Change locale to: $locale")
        val config = Configuration(resources.configuration)
        config.setLocale(locale)

        resources.updateConfiguration(config, resources.displayMetrics)
    }

    fun setCaptchaResult(tid: Short, result: String) {
        app.addTask(GuiTask(Runnable {
            val client: Client
            try {
                client = app.getClient()
                Log.d("pyLoad", "Send Captcha result: $tid $result")
                client.setCaptchaResult(tid.toInt(), result)
            } catch (e: TException) {
                throw RuntimeException(e)
            }
        }))
    }
}
