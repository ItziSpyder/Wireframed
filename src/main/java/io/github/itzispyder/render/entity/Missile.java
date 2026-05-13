package io.github.itzispyder.render.entity;

import io.github.itzispyder.math.Matrix;
import io.github.itzispyder.math.Vector;
import io.github.itzispyder.math.VertexBuffer;
import io.github.itzispyder.render.Entity;
import io.github.itzispyder.util.Mth;

import static io.github.itzispyder.Main.world;

public class Missile extends Entity {

    private final float height;
    private final int sides;
    private int age;
    private Matrix rotation;

    public Missile(Vector position) {
        super(position);
        this.height = (float)(1 + Math.random() * 3);
        this.sides = 3 + (int)(Math.random() * 6);
        this.rotation = Matrix.IDENTITY;
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
            if (!bullet.isInRange(position.add(rotation.transform(new Vector(0, height * 0.5F, 0))), 5))
                continue;
            world.removeEntity(bullet);
            world.removeEntity(this);
            break;
        }
        rotation = Matrix.ROT_Y(age);
    }

    @Override
    public void render(VertexBuffer buf, float tickDelta) {
        float deltaTheta = Mth.TWO_PI / sides;
        Vector position = this.getPosition(tickDelta);

        for (float i = 0; i <= Mth.TWO_PI; i += deltaTheta) {
            float i2 = i + deltaTheta;

            buf.vertex(position, 0xFFFFAAAA);
            buf.vertex(position.add(rotation.transform(new Vector(Mth.cos(i), height, Mth.sin(i)))), 0xFFFFAAAA);

            buf.vertex(position.add(rotation.transform(new Vector(Mth.cos(i), height, Mth.sin(i)))), 0xFFFFAAAA);
            buf.vertex(position.add(rotation.transform(new Vector(Mth.cos(i2), height, Mth.sin(i2)))), 0xFFFFAAAA);
        }
    }
}
