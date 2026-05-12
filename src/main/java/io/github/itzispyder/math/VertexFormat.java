package io.github.itzispyder.math;

public enum VertexFormat implements VertexBufferEntry {

    LINES(2),
    QUADS(4);

    public final int count;

    VertexFormat(int count) {
        this.count = count;
    }
}
