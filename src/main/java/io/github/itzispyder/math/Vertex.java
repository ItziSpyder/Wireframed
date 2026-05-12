package io.github.itzispyder.math;

public non-sealed class Vertex extends Vector implements VertexBufferEntry {

    public final int color;

    public Vertex(float x, float y, float z) {
        this(x, y, z, 0xFFFFFFFF);
    }

    public Vertex(float x, float y, float z, int color) {
        super(x, y, z);
        this.color = color;
    }
}
