package io.github.itzispyder.math;

public class Vector {

    public static final Vector ZERO = new Vector();

    public final double x, y, z;

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
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
        double pitch = Math.toRadians(this.x);
        double yaw = Math.toRadians(this.y);
        return new Vector(
                Math.cos(yaw) * Math.cos(pitch),
                Math.sin(pitch),
                Math.sin(yaw) * Math.cos(pitch)
        );
    }

    public double lengthSquared() {
        return x * x + y * y + z * z;
    }

    public double length() {
        return Math.sqrt(this.lengthSquared());
    }

    public Vector withX(double x) {
        return new Vector(x, y, z);
    }

    public Vector withY(double y) {
        return new Vector(x, y, z);
    }

    public Vector withZ(double z) {
        return new Vector(x, y, z);
    }

    public Vector with(double x, double y, double z) {
        return new Vector(x, y, z);
    }

    public Vector add(double x, double y, double z) {
        return new Vector(this.x + x, this.y + y, this.z + z);
    }

    public Vector add(double v) {
        return add(v, v, v);
    }

    public Vector add(Vector v) {
        return add(v.x, v.y, v.z);
    }

    public Vector sub(double x, double y, double z) {
        return new Vector(this.x - x, this.y - y, this.z - z);
    }

    public Vector sub(double v) {
        return sub(v, v, v);
    }

    public Vector sub(Vector v) {
        return sub(v.x, v.y, v.z);
    }

    public Vector mul(double x, double y, double z) {
        return new Vector(this.x * x, this.y * y, this.z * z);
    }

    public Vector mul(double v) {
        return mul(v, v, v);
    }

    public Vector mul(Vector v) {
        return mul(v.x, v.y, v.z);
    }

    public Vector div(double x, double y, double z) {
        return new Vector(this.x / x, this.y / y, this.z / z);
    }

    public Vector div(double v) {
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
        return this.floor().toString();
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

    public Vector negate() {
        return mul(-1);
    }
}
