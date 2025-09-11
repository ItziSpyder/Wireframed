package io.github.itzispyder.render.entity;

import io.github.itzispyder.math.Vector;
import io.github.itzispyder.math.VertexBuffer;
import io.github.itzispyder.render.Entity;

public class Sphere extends Entity {

    private final float radius;

    public Sphere(float radius) {
        super();
        this.radius = radius;
    }

    @Override
    public void render(VertexBuffer buf, float tickDelta) {
        for (int pitch = 0; pitch < 360; pitch += 30) {
            for (int yaw = 0; yaw < 180; yaw += 30) {
                buf.vertex(position.add(new Vector(pitch, yaw, 0).polar2vector().mul(radius)));
                buf.vertex(position.add(new Vector(pitch, yaw + 30, 0).polar2vector().mul(radius)));
            }
        }
        for (int yaw = 0; yaw < 180; yaw += 30) {
            for (int pitch = 0; pitch < 360; pitch += 30) {
                buf.vertex(position.add(new Vector(pitch, yaw, 0).polar2vector().mul(radius)));
                buf.vertex(position.add(new Vector(pitch + 30, yaw, 0).polar2vector().mul(radius)));
            }
        }
    }

    public float getRadius() {
        return radius;
    }
}
