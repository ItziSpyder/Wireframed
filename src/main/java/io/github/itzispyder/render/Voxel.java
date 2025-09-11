package io.github.itzispyder.render;

import io.github.itzispyder.math.Vector;
import io.github.itzispyder.math.VertexBuffer;

public class Voxel extends Entity {

    private int sideLength;

    public Voxel() {
        this(Vector.ZERO);
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

    public void setSideLength(int sideLength) {
        this.sideLength = sideLength;
    }

    public int getSideLength() {
        return sideLength;
    }
}
