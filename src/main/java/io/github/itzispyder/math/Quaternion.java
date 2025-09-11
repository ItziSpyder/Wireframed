package io.github.itzispyder.math;

import io.github.itzispyder.util.MathUtil;

public class Quaternion {

    public final float w, x, y, z;

    public Quaternion(float w, float x, float y, float z) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Quaternion(Vector v) {
        this(0, v.x, v.y, v.z);
    }

    public static Quaternion fromRotation(float pitch, float yaw) {
        pitch = (float) Math.toRadians(pitch);
        yaw = (float) Math.toRadians(yaw);
        return new Quaternion((float) Math.cos(pitch), (float) Math.sin(pitch), 0, 0)
                .mul(new Quaternion((float) Math.cos(yaw), 0, (float) Math.sin(yaw), 0));
    }

    public static Quaternion fromRotationClient(float pitch, float yaw) {
        pitch = -(float) Math.toRadians(pitch);
        yaw = -(float) Math.toRadians(yaw);
        return new Quaternion((float) Math.cos(yaw), 0, (float) Math.sin(yaw), 0)
                .mul(new Quaternion((float) Math.cos(pitch), (float) Math.sin(pitch), 0, 0));
    }

    public static Quaternion fromLerpRotation(float prevPitch, float pitch, float prevYaw, float yaw, float tickDelta) {
        return fromRotation(MathUtil.lerp(prevPitch, pitch, tickDelta), MathUtil.lerp(prevYaw, yaw, tickDelta));
    }

    public Vector transform(Vector v) {
        return this.normalize().mul(new Quaternion(v)).mul(this.inverse()).toVector();
    }

    public Vector toVector() {
        return new Vector(x, y, z);
    }

    public float length() {
        return (float) Math.sqrt(w * w + x * x + y * y + z * z);
    }

    public Quaternion normalize() {
        float len = 1 / this.length();
        return new Quaternion(w * len, x * len, y * len, z * len);
    }

    public Quaternion inverse() {
        return new Quaternion(w, -x, -y, -z);
    }

    public Quaternion mul(Quaternion q) {
        return new Quaternion(
                this.w*q.w - this.x*q.x - this.y*q.y - this.z*q.z,
                this.w*q.x + this.x*q.w + this.y*q.z - this.z*q.y,
                this.w*q.y - this.x*q.z + this.y*q.w + this.z*q.x,
                this.w*q.z + this.x*q.y - this.y*q.x + this.z*q.w
        );
    }

    public float getW() {
        return w;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }
}
