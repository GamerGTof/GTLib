package gt.gtlib.utils;

import com.google.common.reflect.Reflection;
import com.google.common.util.concurrent.Futures;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public final class Tasks {

    private Tasks() {
    }

    private static final BukkitScheduler scheduler = Bukkit.getScheduler();

    public static Task sync(Runnable runnable) {
        return sync(Reflections.getPluginCaller(), runnable);
    }

    public static Task sync(Plugin plugin, Runnable runnable) {
        return Task.of(scheduler.runTask(plugin, runnable));
    }

    public static Task sync(Runnable task, long delay) {
        return sync(Reflections.getPluginCaller(), task, delay);
    }

    public static Task sync(Plugin plugin, Runnable task, long delay) {
        return Task.of(scheduler.runTaskLater(plugin, task, delay));
    }

    public static Task async(Plugin plugin, Runnable task) {
        return Task.of(scheduler.runTaskAsynchronously(plugin, task));
    }

    public static Task asyncRepeating(Plugin plugin, Runnable task, long delay, long span) {
        return Task.of(scheduler.runTaskTimerAsynchronously(plugin, task, delay, span));
    }

    public static Task asyncRepeating(Plugin plugin, Runnable task, long delay) {
        return asyncRepeating(plugin, task, delay, 0);
    }

    public static Task asyncRepeating(Plugin plugin, Runnable task) {
        return asyncRepeating(plugin, task, 0);
    }

    public static void asyncRepeatingUntilTrue(BooleanSupplier check, Runnable onComplete) {
        asyncRepeatingUntilTrue(Reflections.getPluginCaller(), check, onComplete);
    }

    public static void asyncRepeatingUntilTrue(Plugin plugin, BooleanSupplier check, Runnable onComplete) {
        scheduler.runTaskTimerAsynchronously(plugin, (task) -> {
            if (check.getAsBoolean()) {
                onComplete.run();
                task.cancel();
            }
        }, 0, 0);
    }


    private interface Task {
        boolean isRunning();

        default boolean isDone() {
            return !isRunning();
        }

        boolean isSync();

        void cancel();

        static Task of(BukkitTask task) {
            return of(task::isCancelled, task::isSync, task::cancel);
        }

        static Task of(BooleanSupplier running, BooleanSupplier isSync, Runnable cancelF) {
            return new Task() {
                @Override
                public boolean isSync() {
                    return isSync.getAsBoolean();
                }

                @Override
                public boolean isRunning() {
                    return running.getAsBoolean();
                }

                @Override
                public void cancel() {
                    cancelF.run();
                }
            };
        }
    }

}
