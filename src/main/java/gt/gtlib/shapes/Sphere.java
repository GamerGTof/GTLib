package gt.gtlib.shapes;

import gt.gtlib.utils.Vectors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;
import org.bukkit.util.io.BukkitObjectInputStream;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class Sphere implements Shape<Sphere> {
    private final Vector center;
    private double radius;

    public Sphere(Vector center, double radius) {
        this.center = center;
        this.radius = radius;
    }

    public Vector getCenter() {
        return center.clone();
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    @Override
    public boolean contains(Vector point) {
        return point.isInSphere(center, radius);
    }

    @Override
    public boolean contains(Sphere other) {
        if (other.radius > this.radius) {
            return false;
        }
        return this.center.distance(other.center) + other.radius < this.radius;
    }

    @Override
    public List<Vector> getPoints() {
        final var points = new ArrayList<Vector>();
        for (int r = 0; r < radius; r++) {
            points.addAll(
                    getSurfacePoints0(r)
            );
        }
        return points;
    }

    @Override
    public List<Vector> getPoints(double step) {
        return getPoints(step, step, step);
    }

    public List<Vector> getPoints(double stepX, double stepY, double stepZ) {
        final var points = new ArrayList<Vector>();
        for (int r = 0; r < radius; r += stepZ) {
            final var rStepX = Math.toRadians(stepX);
            final var rStepY = Math.toRadians(stepY);
            points.addAll(
                    getSurfacePoints0(rStepX, rStepY, r)
            );
        }
        return points;
    }

    @Override
    public List<Vector> getSurfacePoints() {
        return getSurfacePoints0(radius);
    }

    @Override
    public List<Vector> getSurfacePoints(double step) {
        return getSurfacePoints(step, step);
    }

    public List<Vector> getSurfacePoints(double xStep, double yStep) {
        final double rx = Math.toRadians(xStep);
        final double ry = Math.toRadians(yStep);
        return getSurfacePoints0(rx, ry, radius);
    }

    private List<Vector> getSurfacePoints0(double r) {
        List<Vector> points = new ArrayList<>();
        for (Vector coord : bresenhemCircle(0, 0, 0, r)) {
            final double z = coord.getY();
            final double rNew = coord.getX();
            for (Vector point : bresenhemCircle(center.getX(), center.getY(), 0, rNew)) {
                final var res = Vectors.of(point.getX(), point.getY(), center.getZ() + z);
                points.add(res);
            }
        }
        return points;
    }

    private List<Vector> getSurfacePoints0(double radianStepX, double radianStepY, double r) {
        final var points = new ArrayList<Vector>();
        for (double pitch = 0; pitch < Math.PI; pitch += radianStepX) {
            for (double yaw = 0; yaw < Math.PI; yaw += radianStepY) {
                final var direction = Vectors.getDirection(yaw, pitch).multiply(r);
                points.add(center.clone().subtract(direction));
                points.add(center.clone().add(direction));
            }
        }
        return points;
    }

    private List<Vector> bresenhemCircle(double oX, double oY, double oZ, double radius) {
        final var points = new ArrayList<Vector>();
        final double eps = .5;
        final double eps2 = eps * eps;
        double x = 0.0;
        double y = radius;
        while (x < y) {
            y = Math.sqrt(y * y - 2 * eps * x - eps2);
            x += eps;
            points.addAll(List.of(
                    Vectors.add(Vectors.of(x, y, 0), oX, oY, oZ),
                    Vectors.add(Vectors.of(-x, y, 0), oX, oY, oZ),
                    Vectors.add(Vectors.of(x, -y, 0), oX, oY, oZ),
                    Vectors.add(Vectors.of(-x, -y, 0), oX, oY, oZ),
                    Vectors.add(Vectors.of(y, x, 0), oX, oY, oZ),
                    Vectors.add(Vectors.of(-y, x, 0), oX, oY, oZ),
                    Vectors.add(Vectors.of(y, -x, 0), oX, oY, oZ),
                    Vectors.add(Vectors.of(-y, -x, 0), oX, oY, oZ)
            ));
        }

        return points;
    }
}
