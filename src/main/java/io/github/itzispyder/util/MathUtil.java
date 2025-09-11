package io.github.itzispyder.util;

import io.github.itzispyder.Main;
import io.github.itzispyder.math.Vector;

public class MathUtil {

    public static float lerp(float a, float b, float delta) {
        return a + (b - a) * delta;
    }

    public static Vector lerp(Vector a, Vector b, float delta) {
        return a.add(b.sub(a).mul(delta));
    }

    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(val, max));
    }
}
