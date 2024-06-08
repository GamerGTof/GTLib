package gt.gtlib.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.PluginClassLoader;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public final class Reflections {
    private Reflections() {
    }

    public static Plugin getPluginCaller() {
        final var found = findPluginCaller();
        return Arrays.stream(Bukkit.getPluginManager().getPlugins())
                .filter(p -> p.getClass().equals(found)).findAny()
                .orElse(null);
    }

    public static <T extends Plugin> Class<T> findPluginCaller() {
        return findPluginCallerIgnoring(null);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Plugin, E extends Plugin> Class<T> findPluginCallerIgnoring(Class<E> pluginClass) {
        final var foundPlugin = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .walk(stream -> stream
                        .map(StackWalker.StackFrame::getDeclaringClass)
                        .filter(c -> c.getClassLoader() instanceof PluginClassLoader)
                        .filter(c -> !c.equals(pluginClass))
                ).findAny();
        return (Class<T>) foundPlugin.orElse(null);
    }

    public static <V> V getFieldValue(Field field, Object target, Class<V> type) {
        return type.cast(getFieldValue(field, target));
    }

    public static Object getMethodHandleValue(MethodHandle v, Object o) {
        try {
            return v.invoke(o);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getFieldValue(VarHandle field, Object target) {
        return field.get(target);
    }

    public static Object getFieldValue(Field field, Object target) {
        final var lookup = MethodHandles.lookup();
        try {
            final var vh = MethodHandles.privateLookupIn(target.getClass(), lookup).unreflectVarHandle(field);
            return vh.get(target);
        } catch (Throwable e) {
            throw new RuntimeException("Could not get value from field.", e);
        }
    }

    /**
     * Get the parameterized type at a certain index. Start counting from 0.
     * Check {@link #getFieldParameterizedTypes(Field)} for a wider description.
     *
     * @param field The field it'll look for the type.
     * @param index The index of the type you want to get.
     * @return The type at specified index.
     */
    public static Type getFieldParameterizedType(Field field, int index) {
        return getFieldParameterizedTypes(field).get(index);
    }

    /**
     * Will return field parameterized types.
     * As in {@code List<Integer>} it would return an array containing a {@link Integer} type.
     * Keep in mind the returned types for generics is always Object due to type erasure.
     *
     * @param field The field where it'll look for the type.
     * @return List<Type> Containing all types found.
     */
    public static List<Type> getFieldParameterizedTypes(Field field) {
        final var fieldType = (ParameterizedType) field.getGenericType();
        return List.of(fieldType.getActualTypeArguments());
    }

    /**
     * Get parameterized type at certain index. Backed by {@link #getClassParameterizedTypes(Class)}.
     *
     * @param c   The class it'll look for the type.
     * @param idx Index to get.
     * @return Type at the index passed in.
     */
    public static Type getClassParameterizedType(Class<?> c, int idx) {
        return getClassParameterizedTypes(c).get(idx);
    }

    /**
     * Get the parameterized types form a class of an object.
     *
     * @param c The class it'll look for the type.
     * @return List<Type> of types of the class passed.
     */
    public static List<Type> getClassParameterizedTypes(Class<?> c) {
        return List.of(((ParameterizedType) c.getGenericSuperclass())
                .getActualTypeArguments()
        );
    }

    /**
     * Calls a non-arg constructor in the type {@link T};
     *
     * @param type Type in which the non-arg constructor must be called.
     * @return The new instance of the type.
     */
    public static <T> T newInstanceOf(Class<T> type) {
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Instance could not be created.", e);
        }
    }

    /**
     *
     * @param type Class to look into.
     * @param target Type instance.
     * @return Field of specified type or null.
     */
    public static Field getFieldOfType(Class<?> type, Object target) {
        return getNthFieldOfType(type, target, 0);
    }

    /**
     *
     * @param type Class to look into.
     * @param target Type instance.
     * @param nth Field index.
     * @return Nth-field of specified type or null.
     */
    public static Field getNthFieldOfType(Class<?> type, Object target, int nth) {
        final var fields = getFieldsOfType(type, target);
        return fields.isEmpty() || fields.size() < nth || nth < 0
                ? null
                : fields.get(nth);
    }

    /**
     *
     * @param type Class to look into.
     * @param target type instance.
     * @return List of Fields of the specified type.
     */
    public static List<Field> getFieldsOfType(Class<?> type, Object target) {
        return getFields(target).stream()
                .filter(field -> type.isAssignableFrom(field.getType()))
                .toList();
    }

    public static List<Field> getFields(Object target) {
        return getFields(target.getClass());
    }

    public static List<Field> getFields(Class<?> type) {
        return List.of(type.getDeclaredFields());
    }
}
