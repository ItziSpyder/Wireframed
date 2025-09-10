package io.github.itzispyder.math;

import io.github.itzispyder.util.MathUtil;

public class Quaternion {

    public final double w, x, y, z;

    public Quaternion(double w, double x, double y, double z) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Quaternion(Vector v) {
        this(0, v.x, v.y, v.z);
    }

    public static Quaternion fromRotation(double pitch, double yaw) {
        pitch = Math.toRadians(pitch);
        yaw = Math.toRadians(yaw);
        return new Quaternion(Math.cos(pitch), Math.sin(pitch), 0, 0)
                .mul(new Quaternion(Math.cos(yaw), 0, Math.sin(yaw), 0));
    }

    public static Quaternion fromLerpRotation(double prevPitch, double pitch, double prevYaw, double yaw) {
        return fromRotation(MathUtil.lerp(prevPitch, pitch), MathUtil.lerp(prevYaw, yaw));
    }

    public Vector transform(Vector v) {
        return this.normalize().mul(new Quaternion(v)).mul(this.inverse()).toVector();
    }

    public Vector toVector() {
        return new Vector(x, y, z);
    }

    public double length() {
        return Math.sqrt(w * w + x * x + y * y + z * z);
    }

    public Quaternion normalize() {
        double len = 1 / this.length();
        return new Quaternion(w * len, x * len, y * len, z * len);
    }

    public Quaternion inverse() {
        return new Quaternion(w, -x, -y, -z);
    }

    public Quaternion mul(Quaternion q) {
        double nw = this.w*q.w - this.x*q.x - this.y*q.y - this.z*q.z;
        double nx = this.w*q.x + this.x*q.w + this.y*q.z - this.z*q.y;
        double ny = this.w*q.y - this.x*q.z + this.y*q.w + this.z*q.x;
        double nz = this.w*q.z + this.x*q.y - this.y*q.x + this.z*q.w;
        return new Quaternion(nw, nx, ny, nz);
    }

    public double getW() {
        return w;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
}
