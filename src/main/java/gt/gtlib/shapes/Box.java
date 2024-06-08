package gt.gtlib.shapes;

import gt.gtlib.utils.Vectors;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

public class Box implements Shape<Box> {
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    private double minZ;
    private double maxZ;

    public Box(double x1, double x2, double y1, double y2, double z1, double z2) {
        this.maxX = Math.max(x1,x2);
        this.maxY = Math.max(y1,y2);
        this.maxZ = Math.max(z1,z2);
        this.minX = Math.min(x1,x2);
        this.minY = Math.min(y1,y2);
        this.minZ = Math.min(z1,z2);
    }

    public Box(Vector v1, Vector v2) {
        this(v1.getX(), v2.getX(), v1.getY(), v2.getY(), v1.getZ(), v2.getZ());
    }

    public Box(Vector mid, double xSize, double ySize, double zSize) {
        this(
                Vectors.add(mid.clone(), xSize, ySize, zSize),
                Vectors.sub(mid.clone(), xSize, ySize, zSize)
        );
    }

    public Box(Vector mid, double size) {
        this(mid, size, size, size);
    }

    public Box expand(double xSize, double ySize, double zSize) {
        setMinPoint(Vectors.sub(getMinPoint(), xSize, ySize, zSize));
        setMaxPoint(Vectors.add(getMaxPoint(), xSize, ySize, zSize));
        return this;
    }

    @Override
    public List<Vector> getPoints(double step) {
        return getPoints(step, step, step);
    }

    @Override
    public List<Vector> getSurfacePoints(double step) {
        //TODO: implement this.
        throw new NotImplementedException();
    }

    public List<Vector> getPoints(double stepX, double stepY, double stepZ) {
        final int width = (int) (getXLength() / stepX) + 1;
        final int height = (int) (getYLength() / stepY) + 1;
        final int depth = (int) (getZLength() / stepZ) + 1;
        final var points = new Vector[width * height * depth];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < depth; z++) {
                    final var point = new Vector(minX + (x * stepX), minY + (y * stepY), minZ + (z * stepZ));
                    points[x + (y * width) + (z * (height * depth))] = point;
                }
            }
        }
        return Arrays.asList(points);
    }


    public boolean contains(Vector point) {
        final var x = point.getX();
        final var y = point.getY();
        final var z = point.getZ();
        return x >= this.minX && x < this.maxX
                && y >= this.minY && y < this.maxY
                && z >= this.minZ && z < this.maxZ;
    }

    @Override
    public boolean contains(Box other) {
        return other.maxX < maxX && other.minX > minX
                && other.maxY < maxY && other.minY > minY
                && other.maxZ < maxZ && other.minZ > minZ;
    }

    public List<Line> getLines() {
        final var vertices = getVertices();
        return List.of(
                new Line(vertices.get(0), vertices.get(1)),
                new Line(vertices.get(0), vertices.get(2)),
                new Line(vertices.get(0), vertices.get(3)),
                new Line(vertices.get(7), vertices.get(6)),
                new Line(vertices.get(7), vertices.get(5)),
                new Line(vertices.get(7), vertices.get(4)),

                new Line(vertices.get(1), vertices.get(0)),
                new Line(vertices.get(3), vertices.get(4)),

                new Line(vertices.get(1), vertices.get(6)),
                new Line(vertices.get(1), vertices.get(5)),
                new Line(vertices.get(2), vertices.get(4)),
                new Line(vertices.get(2), vertices.get(6)),
                new Line(vertices.get(3), vertices.get(5)),
                new Line(vertices.get(3), vertices.get(4))
        );
    }

    public List<Vector> getVertices() {
        return List.of(
                getMinPoint(),
                Vectors.add(getMinPoint(), getXLength(), 0, 0),
                Vectors.add(getMinPoint(), 0, getYLength(), 0),
                Vectors.add(getMinPoint(), 0, 0, getZLength()),
                Vectors.add(getMinPoint(), 0, getYLength(), getZLength()),
                Vectors.add(getMinPoint(), getXLength(), 0, getZLength()),
                Vectors.add(getMinPoint(), getXLength(), getYLength(), 0),
                Vectors.add(getMinPoint(), getXLength(), getYLength(), getZLength())
        );
    }

    public Box setMaxPoint(Vector max) {
        maxX = Math.max(max.getX(), minX);
        maxY = Math.max(max.getY(), minY);
        maxZ = Math.max(max.getZ(), minZ);
        return this;
    }

    public Box setMinPoint(Vector min) {
        minX = Math.min(min.getX(), maxX);
        minY = Math.min(min.getY(), maxY);
        minZ = Math.min(min.getZ(), maxZ);
        return this;
    }

    public Vector getCenterPoint() {
        return new Vector(minX + (getXLength() / 2), minY + (getYLength() / 2), minZ + (getZLength() / 2));
    }

    public Vector getMaxPoint() {
        return new Vector(maxX, maxY, maxZ);
    }

    public Vector getMinPoint() {
        return new Vector(minX, minY, minZ);
    }

    public double getXLength() {
        return maxX - minX;
    }

    public double getYLength() {
        return maxY - minY;
    }

    public double getZLength() {
        return maxZ - minZ;
    }

    public double getMinX() {
        return minX;
    }

    public void setMinX(double minX) {
        this.minX = minX;
    }

    public double getMaxX() {
        return maxX;
    }

    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    public double getMinY() {
        return minY;
    }

    public void setMinY(double minY) {
        this.minY = minY;
    }

    public double getMaxY() {
        return maxY;
    }

    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }

    public double getMinZ() {
        return minZ;
    }

    public void setMinZ(double minZ) {
        this.minZ = minZ;
    }

    public double getMaxZ() {
        return maxZ;
    }

    public void setMaxZ(double maxZ) {
        this.maxZ = maxZ;
    }


    public BoundingBox asBoundingBox() {
        return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    public String toString() {
        return "Box{" +
                "max=" + getMaxPoint().toString() +
                "min=" + getMinPoint().toString() +
                '}';
    }
}
