package astrinox.stellum.handlers.schedule;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.Identifier;

public class ScheduleHandler {
    public static Map<Identifier, Task> tasks = new HashMap<>();

    public static void update() {
        for (Map.Entry<Identifier, Task> task : tasks.entrySet()) {
            if (task.getValue().getDelay() <= 0) {
                task.getValue().run();
                if (task.getValue().getType() == TaskType.ONCE)
                    tasks.remove(task.getKey());
            }
        }
    }

    public static void addTask(Identifier id, Task task) {
        tasks.put(id, task);
    }

    public static void removeTask(Identifier id) {
        tasks.remove(id);
    }
}
