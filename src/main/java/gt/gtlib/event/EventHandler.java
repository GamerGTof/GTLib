package gt.gtlib.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import java.io.Closeable;
import java.util.function.Consumer;

@FunctionalInterface
public interface EventHandler<E extends Event> {

    /**
     * Method called when a {@link E} happens.
     *
     * @param event The event triggered.
     */
    void on(E event);

    default boolean ignoreCancellable() {
        return false;
    }

    static <T extends Event & Cancellable> EventHandler<T> notCancellable(Consumer<T> consumer) {
        return new EventHandler<T>() {
            @Override
            public void on(T event) {
                consumer.accept(event);
            }

            @Override
            public boolean ignoreCancellable() {
                return true;
            }
        };
    }
}
