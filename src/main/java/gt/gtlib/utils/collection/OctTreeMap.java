package gt.gtlib.utils.collection;

import gt.gtlib.shapes.Box;
import gt.gtlib.shapes.Line;
import gt.gtlib.utils.Vectors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class OctTreeMap<V> extends AbstractMap<Vector, V> implements Map<Vector, V> {
    private final Box bounds;
    private final int maxCapacity;
    private List<Map.Entry<Vector, V>> elements;
    @SuppressWarnings("unchecked")
    private final OctTreeMap<V>[] childs = new OctTreeMap[8];

    public OctTreeMap(Box bounds, int maxCapacity) {
        this.elements = new ArrayList<>(maxCapacity);
        this.maxCapacity = maxCapacity;
        this.bounds = bounds;
    }

    protected boolean isSubdivided() {
        return childs[0] != null;
    }

    @Override
    public V get(Object key) {
        final var vec = (Vector) key;
        if (!bounds.contains(vec)) {
            return null;
        }
        if (!isSubdivided()) {
            return elements.stream().filter(entry -> entry.getKey().equals(vec)).findAny().map(Entry::getValue).orElse(null);
        }
        for (var child : childs) {
            var found = child.get(key);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    @Override
    public int size() {
        if (!isSubdivided()) {
            return elements.size();
        }
        return Arrays.stream(childs).map(OctTreeMap::size).reduce(0, Integer::sum);
    }

    @Override
    public boolean isEmpty() {
        if (!isSubdivided()) {
            return elements.isEmpty();
        }
        return Arrays.stream(childs).allMatch(OctTreeMap::isEmpty);
    }

    @Override
    public void clear() {
        elements.clear();
        if (isSubdivided()) {
            Arrays.fill(childs, null);
        }
    }

    @Override
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    @Override
    public V put(Vector key, V value) {
        if (!bounds.contains(key)) {
            throw new IllegalArgumentException("Key not in abounds for " + bounds + ".");
        }
        return put0(key, value);
    }

    private V put0(Vector key, V value) {
        if (!bounds.contains(key)) {
            return null;
        }
        if (!isSubdivided()) {
            if (elements.size() < maxCapacity) {
                final var foundIdx = elements.indexOf(Map.entry(key, value));
                final var toAdd = Map.entry(key, value);
                if (foundIdx == -1) {
                    elements.add(toAdd);
                    return null;
                }
                return elements.set(foundIdx, toAdd).getValue();
            }
            subdivide();
        }
        for (var child : childs) {
            final var added = child.put0(key, value);
            if (added != null) {
                return added;
            }
        }
        return null;
    }

    private void subdivide() {
        final var mid = bounds.getCenterPoint();
        final var dXLen = bounds.getXLength() / 2;
        final var dYLen = bounds.getYLength() / 2;
        final var dZLen = bounds.getZLength() / 2;
        final var min = bounds.getMinPoint();

        final var b1 = new Box(min.clone(), mid.clone());
        final var b2 = new Box(Vectors.add(min.clone(), dXLen, 0, 0), Vectors.add(mid.clone(), dXLen, 0, 0));
        final var b3 = new Box(Vectors.add(min.clone(), 0, 0, dZLen), Vectors.add(mid.clone(), 0, 0, dZLen));
        final var b4 = new Box(Vectors.add(min.clone(), dXLen, 0, dZLen), Vectors.add(mid.clone(), dXLen, 0, dZLen));

        final var t1 = new Box(Vectors.add(min.clone(), 0, dYLen, 0), Vectors.add(mid.clone(), 0, dYLen, 0));
        final var t2 = new Box(Vectors.add(min.clone(), dXLen, dYLen, 0), Vectors.add(mid.clone(), dXLen, dYLen, 0));
        final var t3 = new Box(Vectors.add(min.clone(), 0, dYLen, dZLen), Vectors.add(mid.clone(), 0, dYLen, dZLen));
        final var t4 = new Box(Vectors.add(min.clone(), dXLen, dYLen, dZLen), Vectors.add(mid.clone(), dXLen, dYLen, dZLen));

        childs[0] = new OctTreeMap<>(b1, maxCapacity);
        childs[1] = new OctTreeMap<>(b2, maxCapacity);
        childs[2] = new OctTreeMap<>(b3, maxCapacity);
        childs[3] = new OctTreeMap<>(b4, maxCapacity);
        childs[4] = new OctTreeMap<>(t1, maxCapacity);
        childs[5] = new OctTreeMap<>(t2, maxCapacity);
        childs[6] = new OctTreeMap<>(t3, maxCapacity);
        childs[7] = new OctTreeMap<>(t4, maxCapacity);

        for (Entry<Vector, V> entry : elements) {
            for (OctTreeMap<V> child : childs) {
                child.put0(entry.getKey(), entry.getValue());
            }
        }
        elements.clear();
        elements = null;
    }

    @Override
    public boolean remove(Object key, Object value) {
        if (Objects.equals(get(key), value)) {
            return remove(key) != null;
        }
        return false;
    }

    @Override
    public V remove(Object key) {
        if (!bounds.contains((Vector) key)) {
            throw new IllegalArgumentException("Key not in abounds for " + bounds + ".");
        }
        return remove0(key);
    }

    private V remove0(Object key) {
        final var vec = (Vector) key;
        if (!bounds.contains(vec)) {
            return null;
        }
        if (!isSubdivided()) {
            for (int i = 0; i < elements.size(); i++) {
                final var found = elements.get(i);
                if (found.getKey().equals(vec)) {
                    return elements.remove(i).getValue();
                }
            }
        }
        for (var child : childs) {
            final var removed = child.remove0(key);
            if (removed != null) {
                if(Arrays.stream(childs).allMatch(OctTreeMap::isEmpty)){ // If all childs are null, free them.
                    Arrays.fill(childs,null);
                }
                return removed;
            }
        }
        return null;
    }

    private Stream<Entry<Vector, V>> entryStream() {
        if (!isSubdivided()) {
            return elements.stream();
        }
        return Arrays.stream(childs).flatMap(OctTreeMap::entryStream);
    }

    @NotNull
    @Override
    public Set<Entry<Vector, V>> entrySet() {
        return entryStream().collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public String toString() {
        return "OctTreeMap{" +
                "bounds=" + bounds +
                ", maxCapacity=" + maxCapacity +
                ", elements=" + elements +
                (!isSubdivided() ? "" : ", childs=" + Arrays.stream(childs).filter(child -> !child.isEmpty()).toList()) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        OctTreeMap<?> that = (OctTreeMap<?>) o;
        return maxCapacity == that.maxCapacity && bounds.equals(that.bounds) && elements.equals(that.elements) && Arrays.equals(childs, that.childs);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(super.hashCode(), bounds, maxCapacity, elements);
        result = 31 * result + Arrays.hashCode(childs);
        return result;
    }

    // TODO: Delete these debugging functions:
    public Stream<Vector> getPoints() {
        if (!isSubdivided()) {
            return bounds.getLines().stream().flatMap(line -> line.getPoints().stream());
        }
        return Arrays.stream(childs).flatMap(OctTreeMap::getPoints);
    }

    public void print(Player player) {
        sendBox(bounds, player, Material.DIAMOND_BLOCK);
        if (isSubdivided()) {
            Arrays.stream(childs).forEach(childs -> childs.print(player));
        }
    }

    private void sendBox(Box box, Player player, Material material) {
        box.getLines().forEach(line ->
                line.getPoints().forEach(point ->
                        player.sendBlockChange(point.toLocation(player.getWorld()), material.createBlockData()))
        );
    }
}
