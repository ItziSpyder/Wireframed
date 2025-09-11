package io.github.itzispyder.render.entity;

import io.github.itzispyder.math.Vector;
import io.github.itzispyder.math.VertexBuffer;
import io.github.itzispyder.util.MathUtil;

import static io.github.itzispyder.Main.world;

public class SphereBullet extends Sphere {

    private int age;

    public SphereBullet(float radius) {
        super(radius);
    }

    @Override
    public void onTick() {
        super.onTick();
        if (age++ >= 20) {
            world.removeEntity(this);
        }

        position = position.add(velocity);
        velocity = velocity.mul(0.99F).sub(0, 0.05F, 0);
    }

    @Override
    public void render(VertexBuffer buf, float tickDelta) {
        Vector position = MathUtil.lerp(this.getPrevPosition(), this.getPosition());
        float radius = this.getRadius();

        for (int pitch = 0; pitch < 360; pitch += 30) {
            for (int yaw = 0; yaw < 180; yaw += 30) {
                buf.vertex(position.add(new Vector(pitch, yaw, 0).polar2vector().mul(radius)), 0xFFFFA0A0);
                buf.vertex(position.add(new Vector(pitch, yaw + 30, 0).polar2vector().mul(radius)), 0xFFFFA0A0);
            }
        }
        for (int yaw = 0; yaw < 180; yaw += 30) {
            for (int pitch = 0; pitch < 360; pitch += 30) {
                buf.vertex(position.add(new Vector(pitch, yaw, 0).polar2vector().mul(radius)), 0xFFFFA0A0);
                buf.vertex(position.add(new Vector(pitch + 30, yaw, 0).polar2vector().mul(radius)), 0xFFFFA0A0);
            }
        }
    }
}
