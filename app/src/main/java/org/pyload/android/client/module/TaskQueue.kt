package org.pyload.android.client.module

import android.os.Handler
import android.util.Log
import org.pyload.android.client.pyLoadApp
import java.util.*

/**
 * Task Queue to ensure single threadedness but provide async taks, to avoid
 * blocking the GUI Thread Will catch all exceptions and post them to the
 * handler if found in exception map.
 *
 * @author RaNaN
 */

class TaskQueue(private val app: pyLoadApp, private val mHandler: Handler, private val exceptionMap: HashMap<Throwable, Runnable>) {
    private val tasks = LinkedList<GuiTask>()
    private val lock = Object()

    private var running: Boolean = false
    private val internalRunnable: Runnable

    private val nextTask: GuiTask
        get() = synchronized(lock) {
            while (tasks.isEmpty()) {
                try {
                    lock.wait()
                } catch (e: InterruptedException) {
                    Log.e("pyLoad", "Task interrupted", e)
                    stop()
                }

            }
            return tasks.removeLast()
        }

    init {

        internalRunnable = InternalRunnable()
    }

    fun start() {
        if (!running) {
            val thread = Thread(internalRunnable)
            thread.isDaemon = true
            running = true
            thread.start()
        }
    }

    fun stop() {
        running = false
    }

    fun clear() {
        tasks.clear()
    }

    fun addTask(task: GuiTask) {
        synchronized(lock) {
            tasks.addFirst(task)
            lock.notify() // notify any waiting threads
        }
    }

    private fun internalRun() {
        while (running) {
            val task = nextTask

            // TODO: unusable atm
            if (task.tries <= 0) {
                Log.d("pyLoad", "$task has reached retry limit")
                continue
            }

            try {
                task.task.run()
                mHandler.post(task.success)

            } catch (t: Throwable) {
                Log.e("pyLoad", "Task threw an exception", t)
                app.setLastException(t)

                if (task.hasCritical()) {
                    task.critical?.let {
                        mHandler.post(it)
                    }
                }

                for ((key, value) in exceptionMap) {
                    if (t.javaClass == key.javaClass) {
                        mHandler.post(value)
                    }
                }
            }
        }
    }

    private inner class InternalRunnable : Runnable {
        override fun run() {
            internalRun()
        }
    }
}