package org.pyload.android.client

import android.app.Application
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.Toast
import org.apache.http.conn.ssl.BrowserCompatHostnameVerifier
import org.apache.thrift.TException
import org.apache.thrift.protocol.TBinaryProtocol
import org.apache.thrift.transport.TSocket
import org.apache.thrift.transport.TTransportException
import org.pyload.android.client.components.TabHandler
import org.pyload.android.client.exceptions.WrongLogin
import org.pyload.android.client.exceptions.WrongServer
import org.pyload.android.client.module.AllTrustManager
import org.pyload.android.client.module.GuiTask
import org.pyload.android.client.module.TaskQueue
import org.pyload.thrift.Pyload.Client
import java.io.IOException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.security.KeyManagementException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import javax.net.ssl.*

class pyLoadApp : Application() {
    lateinit var prefs: SharedPreferences
    lateinit var cm: ConnectivityManager
    private var client: Client? = null
    // setted by main activity
    private lateinit var taskQueue: TaskQueue
    private var lastException: Throwable? = null
    private lateinit var main: pyLoad
    private val handleException = Runnable { this.onException() }
    val handleSuccess = Runnable { this.onSuccess() }

    fun init(main: pyLoad) {
        this.main = main

        val map = HashMap<Throwable, Runnable>()
        map[TException()] = handleException
        map[WrongLogin()] = handleException
        map[TTransportException()] = handleException
        map[WrongServer()] = handleException

        taskQueue = TaskQueue(this, Handler(), map)
        startTaskQueue()
    }

    fun verboseBool(state: Boolean): String {
        return if (state)
            getString(R.string.on)
        else
            getString(R.string.off)
    }

    @Throws(TException::class)
    private fun login(): Boolean {
        // replace protocol, some user also enter it
        val defHost = prefs.getString("host", "10.0.2.2") ?: "10.0.2.2"
        val host = defHost.replaceFirst("^[a-zA-z]+://".toRegex(), "")
        val port = Integer.parseInt(prefs.getString("port", "7227") ?: "7227")
        val username = prefs.getString("username", "User")
        val password = prefs.getString("password", "pwhere")

        // TODO: better exception handling
        val trans: TSocket
        try {
            if (prefs.getBoolean("ssl", false)) {
                val ctx: SSLContext
                val trustManagers: Array<TrustManager?>
                try {
                    if (prefs.getBoolean("ssl_validate", true)) {
                        val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
                        tmf.init(null as KeyStore?)
                        trustManagers = tmf.trustManagers
                    } else {
                        trustManagers = arrayOfNulls(1)
                        trustManagers[0] = AllTrustManager()
                    }
                    ctx = SSLContext.getInstance("TLS")
                    ctx.init(null, trustManagers, null)
                    Log.d("pyLoad", "SSL Context created")
                } catch (e: NoSuchAlgorithmException) {
                    throw TException(e)
                } catch (e: KeyStoreException) {
                    throw TException(e)
                } catch (e: KeyManagementException) {
                    throw TException(e)
                }

                // timeout 8000ms
                try {
                    val socket = ctx.socketFactory.createSocket(host, port) as SSLSocket
                    socket.soTimeout = 8000
                    trans = TSocket(socket)
                } catch (var5: Exception) {
                    throw TTransportException("Could not connect to $host on port $port", var5)
                }

                if (prefs.getBoolean("ssl_validate", true)) {
                    val verifier = BrowserCompatHostnameVerifier()
                    try {
                        verifier.verify(host, trans.socket as SSLSocket)
                    } catch (e: IOException) {
                        throw TException(e)
                    }

                    // TODO: check OCSP/CRL
                }
            } else {
                trans = TSocket(host, port, 8000)
                trans.open()
            }
        } catch (e: TTransportException) {
            throw TException(e)
        }

        val iprot = TBinaryProtocol(trans)

        val clientInstance = Client(iprot)
        client = clientInstance
        return clientInstance.login(username, password)
    }

    @Throws(TException::class)
    fun getClient(): Client {
        return client ?: let {
            Log.d("pyLoad", "Creating new Client")
            val loggedin = login()
            val clientInstance = client
            if (!loggedin || clientInstance == null) {
                client = null
                throw WrongLogin()
            } else {

                val server = clientInstance.serverVersion
                var match = false

                for (version in clientVersion) {
                    if (server == version) {
                        match = true
                    }
                }

                if (!match) {
                    throw WrongServer()
                }
                clientInstance
            }
        }
    }

    fun addTask(task: GuiTask) {
        taskQueue.addTask(task)
    }

    private fun startTaskQueue() {
        taskQueue.start()
    }

    private fun onException() {
        client = null
        // The task queue will log an error with exception

        val exception = lastException
        when (exception) {
            is TTransportException -> {
                val t = Toast.makeText(this, R.string.lost_connection,
                        Toast.LENGTH_SHORT)
                t.show()
            }
            is WrongLogin -> {
                val t = Toast.makeText(this, R.string.bad_login,
                        Toast.LENGTH_SHORT)
                t.show()
            }
            is TException -> {
                val tr = findException(exception)

                val t: Toast
                t = when (tr) {
                    is SSLHandshakeException -> Toast.makeText(this, R.string.certificate_error, Toast.LENGTH_SHORT)
                    is SocketTimeoutException -> Toast.makeText(this, R.string.connect_timeout, Toast.LENGTH_SHORT)
                    is ConnectException -> Toast.makeText(this, R.string.connect_error, Toast.LENGTH_SHORT)
                    is SocketException -> Toast.makeText(this, R.string.socket_error, Toast.LENGTH_SHORT)
                    else -> Toast.makeText(this, getString(R.string.no_connection) + " " + tr.message, Toast.LENGTH_SHORT)
                }

                t.show()
            }
            is WrongServer -> {
                val t = Toast.makeText(this, String.format(
                        getString(R.string.old_server), clientVersion[clientVersion.size - 1]),
                        Toast.LENGTH_SHORT)
                t.show()
            }
        }

        setProgress(false)
    }

    /**
     * Retrieves first root exception on stack of several TExceptions.
     *
     * @return the first exception not a TException or the last TException
     */
    private fun findException(e: Throwable): Throwable {
        var throwable = e
        // will not terminate when cycles occur, hopefully nobody cycle exception causes
        while (throwable is TException) {
            val cause = throwable.cause ?: break
            if (cause === throwable) break // just to avoid loop
            throwable = cause
        }

        return throwable
    }

    private fun onSuccess() {
        val t = Toast.makeText(this, R.string.success, Toast.LENGTH_SHORT)
        t.show()

        refreshTab()
    }

    fun refreshTab() {
        val frag = main.currentFragment

        Log.d("pyLoad", "Refreshing Tab: $frag")

        if (frag is TabHandler)
            frag.onSelected()
    }

    fun isCurrentTab(pos: Int): Boolean {
        return main.currentTab == pos
    }

    fun hasConnection(): Boolean {
        // TODO investigate network states, info etc
        return cm.activeNetworkInfo != null
    }

    fun clearTasks() {
        taskQueue.clear()
    }

    fun setLastException(t: Throwable) {
        lastException = t
    }

    fun resetClient() {
        Log.d("pyLoad", "Client reset")
        client = null
    }

    /**
     * Enables and disables the progress indicator.
     *
     *
     * The indicator depends on the user's Android version.
     * pre-actionBar devices: Window.FEATURE_INDETERMINATE_PROGRESS
     * actionBar devices: set refreshAction's view to a progress wheel (Gmail like)
     *
     * @param state
     */
    fun setProgress(state: Boolean) {
        setIndeterminateProgress(main.refreshItem, state)
    }

    private fun setIndeterminateProgress(item: MenuItem?, state: Boolean) {
        if (item == null) {
            return
        }

        if (state) {
            val inflater = LayoutInflater.from(this)
            val progress = inflater.inflate(R.layout.progress_wheel, null)

            main.refreshItem?.actionView = progress

        } else {
            item.actionView = null
        }
    }

    companion object {
        private val clientVersion = arrayOf("0.4.8", "0.4.9")
    }
}
