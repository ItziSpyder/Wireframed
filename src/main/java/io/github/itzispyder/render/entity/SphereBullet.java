package io.github.itzispyder.render.entity;

import io.github.itzispyder.math.Matrix;
import io.github.itzispyder.math.Vector;
import io.github.itzispyder.math.VertexBuffer;
import io.github.itzispyder.render.Entity;
import io.github.itzispyder.util.Mth;

import static io.github.itzispyder.Main.world;

public class SphereBullet extends Sphere {

    private int age;
    public boolean gravity;
    public int color;
    private final Matrix rotationInitial;
    private Matrix rotation;

    public SphereBullet(Vector position, Matrix rotationInitial, float radius) {
        super(position, radius);
        this.color = 0xFF00B7FF;
        this.rotationInitial = rotationInitial;
        this.rotation = rotationInitial;
    }

    public SphereBullet(Vector position, float radius) {
        this(position, Matrix.IDENTITY, radius);
    }

    @Override
    public void onTick() {
        super.onTick();
        if (age++ >= 20) {
            world.removeEntity(this);
        }

        for (int i = 0; i < 10; i++)
            travel();
    }

    private void travel() {
        position = position.add(velocity);
        velocity = velocity.mul(0.99F).sub(0, gravity ? 0.001F : 0, 0);
        rotation = rotationInitial.mul(Matrix.ROT_X((age / 20F) * Mth.PI_OVER_TWO));
        pollCollisionWithMissile();
    }

    private void pollCollisionWithMissile() {
        for (int i = world.getEntities().size() - 1; i >= 0; i--) {
            Entity entity = world.getEntities().get(i);
            if (!(entity instanceof Missile missile))
                continue;
            if (!missile.isInRange(this, 2))
                continue;

            world.removeEntity(missile);
            world.removeEntity(this);
        }
    }

    @Override
    public void render(VertexBuffer buf, float tickDelta) {
        Vector position = this.getPosition(tickDelta);
        float radius = this.getRadius();

        float dTheta = Mth.PI / 6;
        for (float pitch = 0; pitch < Mth.TWO_PI; pitch += dTheta) {
            for (float yaw = 0; yaw < Mth.PI; yaw += dTheta) {
                buf.vertex(position.add(polar2vectorSpecial(pitch, yaw).mul(radius)), color);
                buf.vertex(position.add(polar2vectorSpecial(pitch, yaw + dTheta).mul(radius)), color);
            }
        }
        for (float yaw = 0; yaw < Mth.PI; yaw += dTheta) {
            for (float pitch = 0; pitch < Mth.TWO_PI; pitch += dTheta) {
                buf.vertex(position.add(polar2vectorSpecial(pitch, yaw).mul(radius)), color);
                buf.vertex(position.add(polar2vectorSpecial(pitch + dTheta, yaw).mul(radius)), color);
            }
        }
    }

    private Vector polar2vectorSpecial(float pitch, float yaw) {
        Vector cartesian = new Vector(
                Mth.cos(yaw) * Mth.cos(pitch),
                0.1 * Mth.sin(pitch),
                Mth.sin(yaw) * Mth.cos(pitch));
        return rotation.transform(cartesian);
    }
}
