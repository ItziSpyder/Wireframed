package io.github.itzispyder.render.entity;

import io.github.itzispyder.math.Vector;
import io.github.itzispyder.math.VertexBuffer;
import io.github.itzispyder.render.Entity;
import io.github.itzispyder.util.Mth;

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
    }

    @Override
    public void render(VertexBuffer buf, float tickDelta) {
        float deltaTheta = Mth.TWO_PI / sides;
        Vector position = this.getPosition(tickDelta);

        for (float i = 0; i <= Mth.TWO_PI; i += deltaTheta) {
            float i2 = i + deltaTheta;

            buf.vertex(position, 0xFFFFAAAA);
            buf.vertex(position.add(Mth.cos(i), height, Mth.sin(i)), 0xFFFFAAAA);

            buf.vertex(position.add(Mth.cos(i), height, Mth.sin(i)), 0xFFFFAAAA);
            buf.vertex(position.add(Mth.cos(i2), height, Mth.sin(i2)), 0xFFFFAAAA);
        }
    }
}