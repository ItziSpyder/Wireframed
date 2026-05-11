package io.github.itzispyder.util;

import io.github.itzispyder.math.Vector;

public class Mth {

    public static final float PI = (float) Math.PI;
    public static final float TWO_PI = (float) Math.TAU;
    public static final float PI_OVER_TWO = (float) (0.5F * Math.PI);
    public static final float TO_RAD = PI / 180;
    public static final float TO_DEG = 180 / PI;

    private static final int SIN_TAB_LEN = 360 * 64;
    private static final float[] SIN_TAB = new float[SIN_TAB_LEN];
    private static final float SIN_TAB_CONV_WRITE = TWO_PI / SIN_TAB_LEN;
    private static final float SIN_TAB_CONV_READ = SIN_TAB_LEN / TWO_PI;

    static {
        for (int i = 0; i < SIN_TAB_LEN; i++) {
            SIN_TAB[i] = (float) Math.sin(i * SIN_TAB_CONV_WRITE);
        }
    }

    public static float sin(float a) {
        int index = (int) (a * SIN_TAB_CONV_READ) % SIN_TAB_LEN;
        return SIN_TAB[index >= 0 ? index : index + SIN_TAB_LEN];
    }

    public static float cos(float a) {
        return sin(PI_OVER_TWO - a);
    }

    public static float tan(float a) {
        return sin(a) / cos(a);
    }

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
