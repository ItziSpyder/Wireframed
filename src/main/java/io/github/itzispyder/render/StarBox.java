package io.github.itzispyder.render;

import io.github.itzispyder.math.Vector;
import io.github.itzispyder.math.VertexBuffer;

public class StarBox extends Entity {

    private final VertexBuffer stars;

    public StarBox() {
        super();

        this.stars = new VertexBuffer(2593);
        float radius = 1000;

        for (int pitch = 0; pitch < 360; pitch += 10)
            for (int yaw = 0; yaw < 180; yaw += 10)
                plot(stars, new Vector(pitch, yaw, 0).polar2vector().mul(radius));
        for (int yaw = 0; yaw < 180; yaw += 10)
            for (int pitch = 0; pitch < 360; pitch += 10)
                plot(stars, new Vector(pitch, yaw, 0).polar2vector().mul(radius));
    }

    @Override
    public void render(VertexBuffer buf, float tickDelta) {
        stars.uploadTo(buf);
    }

    private void plot(VertexBuffer buf, Vector position) {
        position = position.applyRandomization(100);
        buf.vertex(position);
        buf.vertex(position.applyRandomization(1));
    }
}
