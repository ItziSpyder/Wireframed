package io.github.itzispyder.math;

import io.github.itzispyder.app.Window;
import io.github.itzispyder.util.MathUtil;

import static io.github.itzispyder.Main.keyboard;
import static io.github.itzispyder.Main.mouse;

public class Camera {

    private float focalLength, worldScale;
    private int windowWidth, windowHeight;
    private Vector prevPosition, position;
    public float prevPitch, prevYaw, pitch, yaw;

    public Camera() {
        this.focalLength = 0.15F;
        this.worldScale = 100;
        this.windowWidth = 0;
        this.windowHeight = 0;
        this.position = Vector.ZERO;
        this.prevPosition = this.position;
    }

    public void updateBounds(Window window) {
        windowWidth = window.getWidth();
        windowHeight = window.getHeight();
    }

    public void onTick() {
        if (keyboard.paused)
            return;

        prevPitch = pitch;
        prevYaw = yaw;
        prevPosition = position;

        pitch -= mouse.pollDeltaY() * 0.25F;
        pitch = (float) MathUtil.clamp(pitch, -45, 45);
        yaw += mouse.pollDeltaX() * 0.25F;

        Vector movement = Vector.ZERO;
        if (keyboard.forward) {
            movement = movement.add(0, 0, 1);
        }
        if (keyboard.backward) {
            movement = movement.add(0, 0, -1);
        }
        if (keyboard.left) {
            movement = movement.add(1, 0, 0);
        }
        if (keyboard.right) {
            movement = movement.add(-1, 0, 0);
        }
        position = position.add(Quaternion.fromRotation(0, -yaw).transform(movement.mul(0.25F)));
    }

    /**
     * Converts 3d space to 2d space using a focalLength value
     * @return (x, y, z) -> (x, y)
     */
    public Vector project(Vector vector) {
        Vector position = MathUtil.lerp(prevPosition, this.position);
        vector = Quaternion.fromLerpRotation(prevPitch, pitch, prevYaw, yaw)
                .transform(vector.sub(position))
                .mul(worldScale);
        float depth = (vector.z + focalLength) * 0.00025F;
        if (depth <= 0)
            depth = 0.000000000001F;

        return new Vector(
             (vector.x * focalLength) / -depth + windowWidth / 2.0,
             (vector.y * focalLength) / -depth + windowHeight / 2.0,
             0
        );
    }

    public Vector getRotationVector() {
        return Quaternion.fromRotationClient(pitch, yaw).transform(new Vector(0, 0, 1).normalize());
    }

    public void setFocalLength(float focalLength) {
        this.focalLength = focalLength;
    }

    public float getFocalLength() {
        return focalLength;
    }

    public void setWorldScale(float worldScale) {
        this.worldScale = worldScale;
    }

    public float getWorldScale() {
        return worldScale;
    }

    public Vector getPosition() {
        return position;
    }

    public void setPosition(Vector position) {
        this.position = position;
    }
}
