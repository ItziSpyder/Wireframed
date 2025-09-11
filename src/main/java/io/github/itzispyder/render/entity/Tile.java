package io.github.itzispyder.render.entity;

import io.github.itzispyder.math.Vector;
import io.github.itzispyder.math.VertexBuffer;
import io.github.itzispyder.render.Entity;

public class Tile extends Entity {

    public Tile(Vector position) {
        super(position);
    }

    @Override
    public void render(VertexBuffer buf, float tickDelta) {
        int o = 0;
        int i = o + 1;

        buf.vertex(position.add(o, o, o));
        buf.vertex(position.add(i, o, o));
        buf.vertex(position.add(i, o, o));
        buf.vertex(position.add(i, o, i));
        buf.vertex(position.add(i, o, i));
        buf.vertex(position.add(o, o, i));
        buf.vertex(position.add(o, o, i));
        buf.vertex(position.add(o, o, o));
    }
}
