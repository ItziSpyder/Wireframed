package io.github.itzispyder.render;

import io.github.itzispyder.math.Vector;
import io.github.itzispyder.math.VertexBuffer;
import io.github.itzispyder.util.MathUtil;

public abstract class Entity {

    public Vector position, velocity;
    private Vector prevPosition;

    public Entity(Vector position) {
        this.position = position;
        this.prevPosition = position;
        this.velocity = Vector.ZERO;
    }

    public Entity() {
        this(Vector.ZERO);
    }


    public abstract void render(VertexBuffer buf, float tickDelta);

    public void onTick() {
        prevPosition = position;
    }

    public Vector getPosition() {
        return position;
    }

    public Vector getPosition(float tickDelta) {
        return MathUtil.lerp(prevPosition, position, tickDelta);
    }

    public void setPosition(Vector position) {
        this.position = position;
    }

    public Vector getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector velocity) {
        this.velocity = velocity;
    }

    public Vector getPrevPosition() {
        return prevPosition;
    }
}
