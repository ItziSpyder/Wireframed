package io.github.itzispyder.render;

import io.github.itzispyder.math.Vector;
import io.github.itzispyder.math.VertexBuffer;

public class Voxel {

    private Vector position;
    private int sideLength;

    public Voxel(Vector position) {
        this.position = position.floor();
        this.sideLength = 1;
    }

    public void render(VertexBuffer buf) {
        int o = 0;
        int i = o + sideLength;

        buf.vertex(position.add(o, o, o));
        buf.vertex(position.add(i, o, o));
        buf.vertex(position.add(i, o, o));
        buf.vertex(position.add(i, o, i));
        buf.vertex(position.add(i, o, i));
        buf.vertex(position.add(o, o, i));
        buf.vertex(position.add(o, o, i));
        buf.vertex(position.add(o, o, o));

        buf.vertex(position.add(o, i, o));
        buf.vertex(position.add(i, i, o));
        buf.vertex(position.add(i, i, o));
        buf.vertex(position.add(i, i, i));
        buf.vertex(position.add(i, i, i));
        buf.vertex(position.add(o, i, i));
        buf.vertex(position.add(o, i, i));
        buf.vertex(position.add(o, i, o));

        buf.vertex(position.add(o, o, o));
        buf.vertex(position.add(o, i, o));
        buf.vertex(position.add(i, o, o));
        buf.vertex(position.add(i, i, o));
        buf.vertex(position.add(i, o, i));
        buf.vertex(position.add(i, i, i));
        buf.vertex(position.add(o, o, i));
        buf.vertex(position.add(o, i, i));
    }

    public Vector getPosition() {
        return position;
    }

    public void setPosition(Vector position) {
        this.position = position;
    }

    public void setSideLength(int sideLength) {
        this.sideLength = sideLength;
    }

    public int getSideLength() {
        return sideLength;
    }
}
