package gt.gtlib.shapes;

import org.bukkit.util.Vector;

import java.util.List;

public interface Shape<S> {

    boolean contains(Vector point);

    boolean contains(S other);

    List<Vector> getPoints(double step);

    List<Vector> getSurfacePoints(double step);

    /**
     * Equivalent to {@link #getPoints(double)} with 1 as argument. In some implementations, its more optimised or precise to call this method.
     *
     * @return List of points in no particular order.
     */
    default List<Vector> getPoints() {
        return this.getPoints(1);
    }

    /**
     * Equivalent to {@link #getSurfacePoints(double)} with 1 as argument. In some implementations, its more optimised or precise to call this method.
     *
     * @return List of points in no particular order.
     */
    default List<Vector> getSurfacePoints() {
        return this.getSurfacePoints(1);
    }
}
