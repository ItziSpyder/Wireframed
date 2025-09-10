package io.github.itzispyder.render;

import io.github.itzispyder.math.Vector;
import io.github.itzispyder.math.VertexBuffer;
import io.github.itzispyder.util.MathUtil;

import static io.github.itzispyder.Main.world;

public class SphereBullet extends Sphere {

    private int age;

    public SphereBullet(double radius) {
        super(radius);
    }

    @Override
    public void onTick() {
        super.onTick();
        if (age++ >= 20) {
            world.removeEntity(this);
        }

        position = position.add(velocity);
        velocity = velocity.mul(0.99).sub(0, 0.05, 0);
    }

    @Override
    public void render(VertexBuffer buf, float tickDelta) {
        Vector position = MathUtil.lerp(this.getPrevPosition(), this.getPosition());
        double radius = this.getRadius();

        for (int pitch = 0; pitch < 360; pitch += 15) {
            for (int yaw = 0; yaw < 180; yaw += 15) {
                buf.vertex(position.add(new Vector(pitch, yaw, 0).polar2vector().mul(radius)), 0xFFFFA0A0);
                buf.vertex(position.add(new Vector(pitch, yaw + 15, 0).polar2vector().mul(radius)), 0xFFFFA0A0);
            }
        }
        for (int yaw = 0; yaw < 180; yaw += 15) {
            for (int pitch = 0; pitch < 360; pitch += 15) {
                buf.vertex(position.add(new Vector(pitch, yaw, 0).polar2vector().mul(radius)), 0xFFFFA0A0);
                buf.vertex(position.add(new Vector(pitch + 15, yaw, 0).polar2vector().mul(radius)), 0xFFFFA0A0);
            }
        }
    }
}
