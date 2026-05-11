package io.github.itzispyder.math;

import io.github.itzispyder.util.Mth;

import static io.github.itzispyder.util.Mth.cos;
import static io.github.itzispyder.util.Mth.sin;

public class Matrix {

    public final float mx0, mx1, mx2;
    public final float my0, my1, my2;
    public final float mz0, mz1, mz2;

    public Matrix(float mx0, float mx1, float mx2, float my0, float my1, float my2, float mz0, float mz1, float mz2) {
        this.mx0 = mx0;
        this.mx1 = mx1;
        this.mx2 = mx2;
        this.my0 = my0;
        this.my1 = my1;
        this.my2 = my2;
        this.mz0 = mz0;
        this.mz1 = mz1;
        this.mz2 = mz2;
    }

    public static Matrix ROT_X(float theta) {
        return new Matrix(
                1, 0, 0,
                0, cos(theta), sin(theta),
                0, -sin(theta), cos(theta)
        );
    }

    public static Matrix ROT_Y(float theta) {
        return new Matrix(
                cos(theta), 0, -sin(theta),
                0, 1, 0,
                sin(theta), 0, cos(theta)
        );
    }

    public static Matrix ROT_Z(float theta) {
        return new Matrix(
                cos(theta), -sin(theta), 0,
                sin(theta), cos(theta), 0,
                0, 0, 1
        );
    }

    public static Matrix rotationFirstPerson(Camera camera, float tickDelta) {
        float pitch = Mth.TO_RAD * Mth.lerp(camera.prevPitch, camera.pitch, tickDelta);
        float yaw = Mth.TO_RAD * Mth.lerp(camera.prevYaw, camera.yaw, tickDelta);
        return ROT_X(-pitch).mul(ROT_Y(-yaw));
    }

    public static Matrix rotationThirdPerson(Camera camera, float tickDelta) {
        float pitch = Mth.TO_RAD * Mth.lerp(camera.prevPitch, camera.pitch, tickDelta);
        float yaw = Mth.TO_RAD * Mth.lerp(camera.prevYaw, camera.yaw, tickDelta);
        return ROT_Y(yaw).mul(ROT_X(pitch));
    }

    public Matrix mul(Matrix other) {
        float nx0 = mx0 * other.mx0 + mx1 * other.my0 + mx2 * other.mz0;
        float ny0 = my0 * other.mx0 + my1 * other.my0 + my2 * other.mz0;
        float nz0 = mz0 * other.mx0 + mz1 * other.my0 + mz2 * other.mz0;

        float nx1 = mx0 * other.mx1 + mx1 * other.my1 + mx2 * other.mz1;
        float ny1 = my0 * other.mx1 + my1 * other.my1 + my2 * other.mz1;
        float nz1 = mz0 * other.mx1 + mz1 * other.my1 + mz2 * other.mz1;

        float nx2 = mx0 * other.mx2 + mx1 * other.my2 + mx2 * other.mz2;
        float ny2 = my0 * other.mx2 + my1 * other.my2 + my2 * other.mz2;
        float nz2 = mz0 * other.mx2 + mz1 * other.my2 + mz2 * other.mz2;

        return new Matrix(nx0, nx1, nx2, ny0, ny1, ny2, nz0, nz1, nz2);
    }

    public Vector transform(Vector other) {
        float nx0 = mx0 * other.x + mx1 * other.y + mx2 * other.z;
        float ny0 = my0 * other.x + my1 * other.y + my2 * other.z;
        float nz0 = mz0 * other.x + mz1 * other.y + mz2 * other.z;

        return new Vector(nx0, ny0, nz0);
    }
}
