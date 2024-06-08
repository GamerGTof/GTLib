package gt.gtlib.shapes;

import gt.gtlib.utils.Vectors;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OrientedBox implements Shape<OrientedBox> {
    private final Vector center;
    private double yaw, pitch, xSize, ySize, zSize;

    public OrientedBox(Vector center, double xSize, double ySize, double zSize) {
        this.center = center;
        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
    }

    public OrientedBox(Vector center, double size) {
        this.center = center;
        xSize = size;
        ySize = size;
        zSize = size;
    }

    public List<Vector> getPoints(double step) {
        return getPoints(step, step, step);
    }

    @Override
    public List<Vector> getSurfacePoints(double step) {
        return null;
    }

    public double getXLength() {
        return xSize * 2;
    }

    public double getYLength() {
        return ySize * 2;
    }

    public double getZLength() {
        return zSize * 2;
    }

    private Vector rotate(Vector vector) {
        return Vectors.rotateAround(vector, yaw, pitch);
    }

    private List<Vector> getPoints(double xStep, double yStep, double zStep) {
        final var forward = Vectors.of(0, 0, 1).rotateAroundX(pitch).rotateAroundY(yaw);
        final var right = Vectors.of(1, 0, 0).rotateAroundX(pitch).rotateAroundY(yaw);
        final var up = Vectors.of(0, 1, 0).rotateAroundX(pitch).rotateAroundY(yaw);
        final var start = center.clone().subtract(
                Vectors.addAll(forward.clone().multiply(zSize), up.clone().multiply(ySize), right.clone().multiply(xSize))
        );
        final int width = (int) (getXLength() / xStep) + 1;
        final int height = (int) (getYLength() / yStep) + 1;
        final int depth = (int) (getZLength() / zStep) + 1;

        final var points = new ArrayList<Vector>(width * height * depth);
        final var rightCache = new Vector();
        final var upCache = new Vector();
        final var forwardCache = new Vector();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {
                    final var pointX = rightCache.copy(right).multiply(x * xStep);
                    final var pointY = upCache.copy(up).multiply(y * yStep);
                    final var pointZ = forwardCache.copy(forward).multiply(z * zStep);
                    final var res = start.clone().add(Vectors.addAll(pointX, pointY, pointZ));
                    points.add(res);
                }
            }
        }
        return points;
    }

    public List<Vector> getVertices() {
        return List.of(
                rotate(Vectors.add(center.clone(), xSize, -ySize, zSize)),
                rotate(Vectors.add(center.clone(), xSize, -ySize, -zSize)),
                rotate(Vectors.add(center.clone(), -xSize, -ySize, zSize)),
                rotate(Vectors.add(center.clone(), -xSize, -ySize, -zSize)),

                rotate(Vectors.add(center.clone(), xSize, ySize, zSize)),
                rotate(Vectors.add(center.clone(), xSize, ySize, -zSize)),
                rotate(Vectors.add(center.clone(), -xSize, ySize, zSize)),
                rotate(Vectors.add(center.clone(), -xSize, ySize, -zSize))
        );
    }

    public boolean contains(Vector point) {
        Vector p1 = Vectors.add(center.clone(), xSize, -ySize, -zSize),
                p2 = Vectors.add(center.clone(), xSize, -ySize, zSize),
                p4 = Vectors.add(center.clone(), -xSize, -ySize, -zSize),
                p5 = Vectors.add(center.clone(), xSize, ySize, -zSize);

        Vector p1MP2 = p1.clone().subtract(p2), p1Mp5 = p1.clone().subtract(p5),
                u = p1.clone().subtract(p4).multiply(p1Mp5.clone()),
                v = p1MP2.clone().multiply(p1Mp5.clone()),
                w = p1MP2.clone().multiply(p1.clone().subtract(p4));

        double uDot = u.clone().dot(point);
        if (uDot > u.clone().dot(p1) && uDot < u.clone().dot(p2)) {
            return true;
        }

        double vDot = v.clone().dot(point);
        if (vDot > v.clone().dot(p1) && vDot < v.clone().dot(p4)) {
            return true;
        }

        double wDot = w.clone().dot(point);
        return wDot > w.clone().dot(p1) && wDot < w.clone().dot(p5);
    }

    @Override
    public boolean contains(OrientedBox other) {
        if (other.equals(this)) {
            return true;
        }
        return getVertices().stream().allMatch(this::contains);
    }

    public void expand(double x, double y, double z) {
        xSize += x;
        ySize += y;
        zSize += z;
    }

    public void shrink(double x, double y, double z) {
        xSize = Math.max(0, xSize - x);
        ySize = Math.max(0, ySize - y);
        zSize = Math.max(0, zSize - z);
    }

    public Vector getCenter() {
        return center;
    }

    public double getYaw() {
        return yaw;
    }

    public double getPitch() {
        return pitch;
    }

    public void setYaw(double yaw) {
        this.yaw = yaw;
    }

    public void setPitch(double pitch) {
        this.pitch = pitch;
    }

    public double getxSize() {
        return xSize;
    }

    public double getySize() {
        return ySize;
    }

    public double getzSize() {
        return zSize;
    }
}
