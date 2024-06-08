package gt.gtlib.utils;

import org.bukkit.util.Vector;

public final class Vectors {

    private Vectors() {
    }

    public static Vector addAll(Vector... vectors) {
        final var res = vectors[0].clone();
        for (int i = 1; i < vectors.length; i++) {
            res.add(vectors[i]);
        }
        return res;
    }

    public static boolean smallerThan(Vector comparing, Vector target) {
        return smallerX(comparing, target) && smallerY(comparing, target) && smallerZ(comparing, target);
    }

    public static boolean smallerX(Vector comparing, Vector target) {
        return comparing.getX() < target.getX();
    }

    public static boolean smallerY(Vector comparing, Vector target) {
        return comparing.getY() < target.getY();
    }

    public static boolean smallerZ(Vector comparing, Vector target) {
        return comparing.getZ() < target.getZ();
    }

    public static boolean greaterThan(Vector comparing, Vector target) {
        return greaterX(comparing, target) && greaterY(comparing, target) && greaterZ(comparing, target);
    }

    public static boolean greaterX(Vector comparing, Vector target) {
        return comparing.getX() > target.getX();
    }

    public static boolean greaterY(Vector comparing, Vector target) {
        return comparing.getY() > target.getY();
    }

    public static boolean greaterZ(Vector comparing, Vector target) {
        return comparing.getZ() > target.getZ();
    }

    public static Vector add(Vector vector, double x, double y, double z) {
        return vector.add(new Vector(x, y, z));
    }

    public static Vector add(Vector vector, double scalar) {
        return Vectors.add(vector, scalar, scalar, scalar);
    }

    public static Vector sub(Vector vector, double x, double y, double z) {
        return vector.subtract(of(x, y, z));
    }

    public static Vector sub(Vector vector, double scalar) {
        return Vectors.sub(vector, scalar, scalar, scalar);
    }

    public static Vector mult(Vector vector, double x, double y, double z) {
        return vector.multiply(of(x, y, z));
    }

    public static Vector mult(Vector vector, double scalar) {
        return Vectors.mult(vector, scalar, scalar, scalar);
    }

    public static Vector of(double x, double y, double z) {
        return new Vector(x, y, z);
    }

    public static Vector blockAligned(Vector vector) {
        vector.setX(vector.getBlockX());
        vector.setY(vector.getBlockY());
        vector.setZ(vector.getBlockZ());
        return vector;
    }

    public static Vector setMagnitude(Vector vector, double magnitude) {
        final double oldMag = vector.length();
        vector.setX(vector.getX() * magnitude / oldMag);
        vector.setY(vector.getY() * magnitude / oldMag);
        vector.setZ(vector.getZ() * magnitude / oldMag);
        return vector;
    }

    public static Vector rotateAround(Vector point, double yaw, double pitch) {
        Vector c1 = new Vector(Math.cos(yaw), 0, -Math.sin(yaw)),
                c2 = new Vector(Math.sin(yaw) * Math.sin(pitch), Math.cos(pitch), Math.cos(yaw) * Math.sin(pitch)),
                c3 = new Vector(Math.sin(yaw) * Math.cos(pitch), -Math.sin(pitch), Math.cos(yaw) * Math.cos(pitch));
        return new Vector().add(c1.multiply(point.getX())).add(c2.multiply(point.getY())).add(c3.multiply(point.getZ()));
    }

    public static Vector getDirection(double yaw, double pitch) {
        Vector vector = new Vector().setY(-Math.sin(yaw));
        double xz = Math.cos(yaw);

        return vector.setX(-xz * Math.sin(pitch))
                .setZ(xz * Math.cos(pitch));

    }
}
