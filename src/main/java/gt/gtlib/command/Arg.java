package gt.gtlib.command;

import java.net.Socket;

public class Arg<T> {
    protected final Class<T> type;
    protected final T defaultValue;
    protected final boolean nullable;

    protected Arg(Class<T> type, boolean nullable, T defaultValue) {
        this.type = type;
        this.nullable = nullable;
        this.defaultValue = defaultValue;
    }

    public boolean isNullable() {
        return nullable;
    }

    public boolean isNotNullable() {
        return !nullable;
    }

    public Class<T> getType() {
        return type;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public static <V> Arg<V> of(Class<V> type) {
        return new Arg<>(type, false, null);
    }

    public static <V> Arg<V> ofNullable(Class<V> type) {
        return new Arg<>(type, true, null);
    }

    public static <V> Arg<V> ofDefault(Class<V> type, V def) {
        return new Arg<>(type, true, def);
    }


    @SuppressWarnings("unchecked")
    public static <V> Arg<V> ofDefault(V def) {
        return ofDefault((Class<V>) def.getClass(), def);
    }

    @Override
    public String toString() {
        return "Arg{" +
                "type=" + type +
                ", defaultValue=" + defaultValue +
                ", nullable=" + nullable +
                '}';
    }
}
