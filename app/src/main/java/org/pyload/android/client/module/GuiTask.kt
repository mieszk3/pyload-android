package org.pyload.android.client.module

class GuiTask {
    val task: Runnable
    val success: Runnable
    //how often the task can be called
    var tries = 2
    // called when anything goes wrong (optional)
    var critical: Runnable? = null

    constructor(task: Runnable) {
        this.task = task
        // Nop
        this.success = Runnable { }
    }

    constructor(task: Runnable, success: Runnable) {
        this.task = task
        this.success = success
    }

    fun hasCritical(): Boolean {
        return critical != null
    }
}