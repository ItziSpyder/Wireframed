package io.github.itzispyder.util;

import io.github.itzispyder.Main;
import io.github.itzispyder.math.Vector;

public class MathUtil {

    public static double lerp(double a, double b, double delta) {
        return a + (b - a) * delta;
    }

    public static double lerp(double a, double b) {
        return lerp(a, b, Main.tickDelta());
    }

    public static Vector lerp(Vector a, Vector b, double delta) {
        return a.add(b.sub(a).mul(delta));
    }

    public static Vector lerp(Vector a, Vector b) {
        return lerp(a, b, Main.tickDelta());
    }

    public static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(val, max));
    }
}
