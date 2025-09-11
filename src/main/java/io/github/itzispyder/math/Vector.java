package io.github.itzispyder.math;

public class Vector {

    public static final Vector ZERO = new Vector();

    public final float x, y, z;

    public Vector(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector(double x, double y, double z) {
        this((float)x, (float)y, (float)z);
    }

    private Vector() {
        this(0, 0, 0);
    }

    /**
     * Converts vector coordinate to polar coordinate
     * @return (x, y, z) ==> (pitch, yaw, 0)
     */
    public Vector vector2polar() {
        return new Vector(
                Math.toDegrees(Math.atan2(y, Math.sqrt(x * x + z * z))),
                Math.toDegrees(Math.atan2(z, x)),
                0
        );
    }

    /**
     * Converts polar coordinate to vector coordinate
     * @return (pitch, yaw, 0) ==> (x, y, z)
     */
    public Vector polar2vector() {
        float pitch = (float) Math.toRadians(this.x);
        float yaw = (float) Math.toRadians(this.y);
        return new Vector(
                Math.cos(yaw) * Math.cos(pitch),
                Math.sin(pitch),
                Math.sin(yaw) * Math.cos(pitch)
        );
    }

    public float lengthSquared() {
        return x * x + y * y + z * z;
    }

    public float length() {
        return (float) Math.sqrt(this.lengthSquared());
    }

    public Vector withX(float x) {
        return new Vector(x, y, z);
    }

    public Vector withY(float y) {
        return new Vector(x, y, z);
    }

    public Vector withZ(float z) {
        return new Vector(x, y, z);
    }

    public Vector with(float x, float y, float z) {
        return new Vector(x, y, z);
    }

    public Vector add(float x, float y, float z) {
        return new Vector(this.x + x, this.y + y, this.z + z);
    }

    public Vector add(float v) {
        return add(v, v, v);
    }

    public Vector add(Vector v) {
        return add(v.x, v.y, v.z);
    }

    public Vector sub(float x, float y, float z) {
        return new Vector(this.x - x, this.y - y, this.z - z);
    }

    public Vector sub(float v) {
        return sub(v, v, v);
    }

    public Vector sub(Vector v) {
        return sub(v.x, v.y, v.z);
    }

    public Vector mul(float x, float y, float z) {
        return new Vector(this.x * x, this.y * y, this.z * z);
    }

    public Vector mul(float v) {
        return mul(v, v, v);
    }

    public Vector mul(Vector v) {
        return mul(v.x, v.y, v.z);
    }

    public Vector div(float x, float y, float z) {
        return new Vector(this.x / x, this.y / y, this.z / z);
    }

    public Vector div(float v) {
        return div(v, v, v);
    }

    public Vector div(Vector v) {
        return div(v.x, v.y, v.z);
    }

    public Vector normalize() {
        return this.div(this.length());
    }

    public Vector floor() {
        return new Vector(Math.floor(x), Math.floor(y), Math.floor(z));
    }

    public Vector ceil() {
        return new Vector(Math.ceil(x), Math.ceil(y), Math.ceil(z));
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Vector v
                && v.x == x
                && v.y == y
                && v.z == z;
    }

    @Override
    public String toString() {
        return "<" + x + ", " + y + ", " + z + ">";
    }

    public String toStringFloored() {
        return "<" + (int)x + ", " + (int)y + ", " + (int)z + ">";
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

    public Vector negate() {
        return mul(-1);
    }

    public Vector applyRandomization(float amplitude) {
        return add(
                (float) (amplitude * Math.random() * (Math.random() < 0.5 ? 1 : -1)),
                (float) (amplitude * Math.random() * (Math.random() < 0.5 ? 1 : -1)),
                (float) (amplitude * Math.random() * (Math.random() < 0.5 ? 1 : -1))
        );
    }
}
