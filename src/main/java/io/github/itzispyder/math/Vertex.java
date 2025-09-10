package io.github.itzispyder.math;

public class Vertex extends Vector {

    public final int color;

    public Vertex(double x, double y, double z) {
        this(x, y, z, 0xFFFFFFFF);
    }

    public Vertex(double x, double y, double z, int color) {
        super(x, y, z);
        this.color = color;
    }
}
