package gt.gtlib.event;

import gt.gtlib.utils.Events;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

public class DynamicEventHandler<E extends Event> implements EventHandler<E> {
    private final EventHandler<E> eventHandler;
    private final Class<E> eventClass;
    private final Class<Plugin> pluginClass;

    public DynamicEventHandler(Class<E> eventClass, Class<Plugin> pluginClass, EventHandler<E> eventHandler) {
        this.eventHandler = eventHandler;
        this.eventClass = eventClass;
        this.pluginClass = pluginClass;
    }

    public DynamicEventHandler(Class<E> eventClass, EventHandler<E> eventHandler) {
        this(eventClass, null, eventHandler);
    }

    public Class<Plugin> getPluginClass() {
        return pluginClass;
    }

    public Class<E> getEventClass() {
        return this.eventClass;
    }

    public boolean register() {
        return Events.registerDynamic(this);
    }

    public boolean unregister(Class<E> type) {
        return Events.removeDynamic(this);
    }

    @Override
    public void on(E event) {
        eventHandler.on(event);
    }
}
