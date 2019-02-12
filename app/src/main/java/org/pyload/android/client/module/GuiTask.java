package org.pyload.android.client.module;

public class GuiTask {

    private final Runnable task;
    private final Runnable success;
    //how often the task can be called
    int tries = 2;
    // called when anything goes wrong (optional)
    private Runnable critical;

    public GuiTask(Runnable task) {
        this.task = task;
        // Nop
        this.success = () -> {
        };
    }

    public GuiTask(Runnable task, Runnable success) {
        this.task = task;
        this.success = success;
    }

    Runnable getTask() {
        return task;
    }

    public Runnable getSuccess() {
        return success;
    }

    boolean hasCritical() {
        return (critical != null);
    }

    Runnable getCritical() {
        return critical;
    }

    public void setCritical(Runnable critical) {
        this.critical = critical;
    }

}
