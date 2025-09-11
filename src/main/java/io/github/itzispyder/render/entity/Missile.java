package io.github.itzispyder.render.entity;

import io.github.itzispyder.math.Vector;
import io.github.itzispyder.math.VertexBuffer;
import io.github.itzispyder.render.Entity;

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
            if (!bullet.isInRange(this, 10))
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
            float angle1 = (float) Math.toRadians(i);
            float angle2 = (float) Math.toRadians(i + deltaTheta);

            buf.vertex(position, 0xFFFFAAAA);
            buf.vertex(position.add((float)Math.cos(angle1), height, (float)Math.sin(angle1)), 0xFFFFAAAA);

            buf.vertex(position.add((float)Math.cos(angle1), height, (float)Math.sin(angle1)), 0xFFFFAAAA);
            buf.vertex(position.add((float)Math.cos(angle2), height, (float)Math.sin(angle2)), 0xFFFFAAAA);
        }
    }
}
