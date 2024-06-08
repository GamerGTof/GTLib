package gt.gtlib.utils.collection;

import gt.gtlib.shapes.Box;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

public class OctreeSet implements Set<Vector> {
    private static final Object DEFAULT_VALUE = new Object();
    private final OctTreeMap<Object> handle;

    public OctreeSet(Box bounds, int maxCapacity) {
        handle = new OctTreeMap<>(bounds, maxCapacity);
    }

    @Override
    public int size() {
        return handle.size();
    }

    @Override
    public boolean isEmpty() {
        return handle.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return handle.get((Vector) o) == DEFAULT_VALUE;
    }

    @NotNull
    @Override
    public Iterator<Vector> iterator() {
        return handle.keySet().iterator();
    }

    @NotNull
    @Override
    public Object @NotNull [] toArray() {
        return handle.keySet().toArray();
    }

    @NotNull
    @Override
    public <T> T @NotNull [] toArray(@NotNull T[] a) {
        return handle.values().toArray(a);
    }

    @Override
    public boolean add(Vector vector) {
        return handle.put(vector, DEFAULT_VALUE) != null;
    }

    @Override
    public boolean remove(Object o) {
        return handle.remove(o) != null;
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return c.stream().allMatch(handle::containsKey);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends Vector> c) {
        handle.putAll(
                c.stream().collect(Collectors.toMap(k -> k, k -> DEFAULT_VALUE))
        );
        return true;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        handle.keySet().retainAll(c);
        return false;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        c.forEach(handle::remove);
        return true;
    }

    @Override
    public void clear() {
        handle.clear();
    }
}
