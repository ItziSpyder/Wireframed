package io.github.itzispyder.render.entity;

import io.github.itzispyder.math.Vector;
import io.github.itzispyder.math.VertexBuffer;
import io.github.itzispyder.render.Entity;
import io.github.itzispyder.util.Trig;

import static io.github.itzispyder.Main.world;

public class Missile extends Entity {

    private final float height;
    private final int sides;
    private int age;

    public Missile(Vector position) {
        super(position);
        this.height = (float)(1 + Math.random() * 3);
        this.sides = 3 + (int)(Math.random() * 6);
    }

    @Override
    public void onTick() {
        super.onTick();

        if (age++ >= 60)
            world.removeEntity(this);

        position = position.add(velocity);
        velocity = velocity.mul(1.067F);

        for (int i = world.getEntities().size() - 1; i >= 0; i--) {
            Entity ent = world.getEntities().get(i);
            if (!(ent instanceof SphereBullet bullet))
                continue;
            if (!bullet.isInRange(position.add(0, height * 0.5F, 0), 5))
                continue;
            world.removeEntity(bullet);
            world.removeEntity(this);
            break;
        }
    }

    @Override
    public void render(VertexBuffer buf, float tickDelta) {
        float deltaTheta = 360F / sides;
        Vector position = this.getPosition(tickDelta);

        for (float i = 0; i <= 360F; i += deltaTheta) {
            float i2 = i + deltaTheta;

            buf.vertex(position, 0xFFFFAAAA);
            buf.vertex(position.add(Trig.cos(i), height, Trig.sin(i)), 0xFFFFAAAA);

            buf.vertex(position.add(Trig.cos(i), height, Trig.sin(i)), 0xFFFFAAAA);
            buf.vertex(position.add(Trig.cos(i2), height, Trig.sin(i2)), 0xFFFFAAAA);
        }
    }
}
