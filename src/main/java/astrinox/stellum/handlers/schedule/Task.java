package astrinox.stellum.handlers.schedule;

public class Task {
    private final Runnable runnable;
    private final TaskType type;
    private final int delay;
    private int currentDelay;

    public Task(Runnable runnable, TaskType type, int delay) {
        this.runnable = runnable;
        this.type = type;
        this.delay = delay;
        this.currentDelay = delay + 1;
    }

    public void run() {
        runnable.run();
    }

    public TaskType getType() {
        return type;
    }

    public int getDelay() {
        --currentDelay;
        if (currentDelay < 0 && type == TaskType.REPEATING)
            currentDelay = delay;
        return currentDelay;
    }
}
