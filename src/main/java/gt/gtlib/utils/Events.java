package gt.gtlib.utils;

import gt.gtlib.GTLib;
import gt.gtlib.event.DummyListener;
import gt.gtlib.event.DynamicEventHandler;
import gt.gtlib.event.EventHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class Events {
    private static final Map<Class<Event>, Set<DynamicEventHandler<Event>>> events = new HashMap<>();

    static {
        final var registeredListener = new RegisteredListener(new DummyListener(),
                (listener, event) -> dispatchEvent(event),
                EventPriority.HIGHEST, GTLib.getInstance(), false);
        HandlerList.getHandlerLists().forEach(handler -> handler.register(registeredListener));
    }

    private Events() {
    }

    public static void trigger(Event event) {
        Bukkit.getPluginManager().callEvent(event);
    }

    public static <T extends Event> void register(Class<T> eventClass, EventHandler<T> eventHandler) {
        register(eventClass, eventHandler, Reflections.getPluginCaller());
    }


    @SuppressWarnings("unchecked")
    public static <T extends Event> void register(Class<T> eventClass, EventHandler<T> eventHandler, Plugin plugin) {
        Bukkit.getPluginManager().registerEvent(eventClass, new DummyListener(), EventPriority.HIGH, (listener, event) -> eventHandler.on((T) event), plugin);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Event> boolean registerDynamic(DynamicEventHandler<T> eventHandler) {
        return events.computeIfAbsent((Class<Event>) eventHandler.getEventClass(), k -> new HashSet<>()).add((DynamicEventHandler<Event>) eventHandler);
    }

    public static <E extends Event> boolean removeDynamic(DynamicEventHandler<E> dynamicHandler) {
        return events.get(dynamicHandler.getEventClass()).remove(dynamicHandler);
    }

    private static void dispatchEvent(Event event) {
        final var handlers = events.get(event.getClass());
        if (handlers != null) {
            for (var handler : handlers) {
                try {
                    handler.on(event);
                } catch (Exception e) {
                    new RuntimeException(
                            "Event from plugin " + (handler.getPluginClass() == null
                                    ? Reflections.findPluginCallerIgnoring(GTLib.class).getName()
                                    : handler.getPluginClass().getName())
                                    + " have thrown an error.", e
                    ).printStackTrace();
                }
            }
        }
    }
}
