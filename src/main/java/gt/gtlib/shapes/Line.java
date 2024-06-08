package gt.gtlib.shapes;

import com.google.common.collect.ImmutableCollection;
import gt.gtlib.utils.Vectors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Line implements Shape<Line> {
    private final Vector start;
    private final Vector end;

    public Line(Vector start, Vector end) {
        this.start = start;
        this.end = end;
    }

    public Vector getStart() {
        return start;
    }

    public Vector getEnd() {
        return end;
    }

    @Override
    public boolean contains(Vector point) {
        final var v1 = point.clone().subtract(start);
        final var v2 = end.clone().subtract(start);
        final var cross = v1.crossProduct(v2);
        return Math.abs(cross.length()) < 1e-9 && Math.abs(v1.dot(v2)) <= v2.lengthSquared();
    }

    @Override
    public boolean contains(Line other) {
        return this.contains(other.start) && this.contains(other.end);
    }

    @Override
    public List<Vector> getSurfacePoints(double step) {
        return getPoints(step);
    }

    @Override
    public List<Vector> getPoints(double step) {
        final var start2end = end.clone().subtract(start);
        final int iterations = (int) (start2end.length() / step) + 1;
        final var direction = start2end.normalize();
        final var points = new Vector[iterations];
        for (int i = 0; i < iterations; i++) {
            points[i] = direction.clone().multiply(i * step).add(start);
        }
        return Arrays.asList(points);
    }
}
