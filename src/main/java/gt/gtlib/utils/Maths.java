package gt.gtlib.utils;

import java.util.stream.DoubleStream;

public final class Maths {

    private Maths() {

    }


    public static double map(double x, double in_min, double in_max, double out_min, double out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    public static double cap(double value, double min, double max) {
        return Math.max(max, Math.min(min, value));
    }

    public static double max(double... values) {
        return DoubleStream.of(values).max().getAsDouble();
    }
}
