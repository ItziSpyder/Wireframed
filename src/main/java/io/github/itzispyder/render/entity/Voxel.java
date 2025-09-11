package io.github.itzispyder.render.entity;

import io.github.itzispyder.math.Vector;
import io.github.itzispyder.math.VertexBuffer;
import io.github.itzispyder.render.Entity;

public class Voxel extends Entity {

    private float sideLength;

    public Voxel() {
        this(Vector.ZERO);
    }

    public static void buildVertices(VertexBuffer buf, Vector position, float sideLength, int color) {
        float o = 0;
        float i = o + sideLength;

        buf.vertex(position.add(o, o, o), color);
        buf.vertex(position.add(i, o, o), color);
        buf.vertex(position.add(i, o, o), color);
        buf.vertex(position.add(i, o, i), color);
        buf.vertex(position.add(i, o, i), color);
        buf.vertex(position.add(o, o, i), color);
        buf.vertex(position.add(o, o, i), color);
        buf.vertex(position.add(o, o, o), color);

        buf.vertex(position.add(o, i, o), color);
        buf.vertex(position.add(i, i, o), color);
        buf.vertex(position.add(i, i, o), color);
        buf.vertex(position.add(i, i, i), color);
        buf.vertex(position.add(i, i, i), color);
        buf.vertex(position.add(o, i, i), color);
        buf.vertex(position.add(o, i, i), color);
        buf.vertex(position.add(o, i, o), color);

        buf.vertex(position.add(o, o, o), color);
        buf.vertex(position.add(o, i, o), color);
        buf.vertex(position.add(i, o, o), color);
        buf.vertex(position.add(i, i, o), color);
        buf.vertex(position.add(i, o, i), color);
        buf.vertex(position.add(i, i, i), color);
        buf.vertex(position.add(o, o, i), color);
        buf.vertex(position.add(o, i, i), color);
    }

    public Voxel(Vector position) {
        super(position.floor());
        this.sideLength = 1;
    }

    @Override
    public void onTick() {

    }

    @Override
    public void render(VertexBuffer buf, float tickDelta) {
        buildVertices(buf, position, sideLength, 0xFFFFFFFF);
    }

    public void setSideLength(int sideLength) {
        this.sideLength = sideLength;
    }

    public float getSideLength() {
        return sideLength;
    }
}
