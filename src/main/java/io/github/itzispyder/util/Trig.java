package io.github.itzispyder.util;

public class Trig {

    private static final int tableSize = 360 * 4;
    private static final float[] sines = new float[360 * 4];

    static {
        for (int i = 0; i < 360 * 4; i++)
            sines[i] = (float) Math.sin(Math.toRadians(i * 0.25));
    }

    public static float sin(float angDeg) {
        int ang = (int)(angDeg * 4) % (360 * 4);
        return sines[ang >= 0 ? ang : ang + (360 * 4)];
    }
    
    public static float cos(float angDeg) {
        return sin(90 - angDeg);
    }
    
    public static float tan(float angDeg) {
        return sin(angDeg) / cos(angDeg);
    }
}
