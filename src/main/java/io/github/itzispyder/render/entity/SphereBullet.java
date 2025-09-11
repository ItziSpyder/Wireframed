package io.github.itzispyder.render.entity;

import io.github.itzispyder.math.Vector;
import io.github.itzispyder.math.VertexBuffer;

import static io.github.itzispyder.Main.world;

public class SphereBullet extends Sphere {

    private int age;
    public boolean gravity;
    public int color;

    public SphereBullet(float radius) {
        super(radius);
        this.color = 0xFF00B7FF;
    }

    @Override
    public void onTick() {
        super.onTick();
        if (age++ >= 20) {
            world.removeEntity(this);
        }

        for (int i = 0; i < 5; i++)
            travel();
    }

    private void travel() {
        position = position.add(velocity);
        velocity = velocity.mul(0.99F).sub(0, gravity ? 0.01F : 0, 0);
    }

    @Override
    public void render(VertexBuffer buf, float tickDelta) {
        Vector position = this.getPosition(tickDelta);
        float radius = this.getRadius();

        for (int pitch = 0; pitch < 360; pitch += 60) {
            for (int yaw = 0; yaw < 180; yaw += 60) {
                buf.vertex(position.add(new Vector(pitch, yaw, 0).polar2vector().mul(radius)), color);
                buf.vertex(position.add(new Vector(pitch, yaw + 60, 0).polar2vector().mul(radius)), color);
            }
        }
        for (int yaw = 0; yaw < 180; yaw += 60) {
            for (int pitch = 0; pitch < 360; pitch += 60) {
                buf.vertex(position.add(new Vector(pitch, yaw, 0).polar2vector().mul(radius)), color);
                buf.vertex(position.add(new Vector(pitch + 60, yaw, 0).polar2vector().mul(radius)), color);
            }
        }
    }
}
