package io.github.itzispyder.math;

public sealed interface VertexBufferEntry permits Vertex, VertexFormat {

    default boolean isModeFlag() {
        return this instanceof VertexFormat;
    }

    default boolean isRenderVertex() {
        return this instanceof Vertex;
    }
}
