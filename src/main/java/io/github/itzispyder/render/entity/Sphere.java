package io.github.itzispyder.render.entity;

import io.github.itzispyder.math.Vector;
import io.github.itzispyder.math.VertexBuffer;
import io.github.itzispyder.render.Entity;
import io.github.itzispyder.util.Mth;

public class Sphere extends Entity {

    private final float radius;

    public Sphere(Vector position, float radius) {
        super(position);
        this.radius = radius;
    }

    public Sphere(float radius) {
        this(Vector.ZERO, radius);
    }

    @Override
    public void render(VertexBuffer buf, float tickDelta) {
        float dTheta = Mth.PI / 6;
        for (float pitch = 0; pitch < Mth.TWO_PI; pitch += dTheta) {
            for (float yaw = 0; yaw < Mth.PI; yaw += dTheta) {
                buf.vertex(position.add(new Vector(pitch, yaw, 0).polar2vector().mul(radius)));
                buf.vertex(position.add(new Vector(pitch, yaw + dTheta, 0).polar2vector().mul(radius)));
            }
        }
        for (float yaw = 0; yaw < Mth.PI; yaw += dTheta) {
            for (float pitch = 0; pitch < Mth.TWO_PI; pitch += dTheta) {
                buf.vertex(position.add(new Vector(pitch, yaw, 0).polar2vector().mul(radius)));
                buf.vertex(position.add(new Vector(pitch + dTheta, yaw, 0).polar2vector().mul(radius)));
            }
        }
    }

    public float getRadius() {
        return radius;
    }
}
