package io.github.itzispyder.math;

import io.github.itzispyder.app.Window;
import io.github.itzispyder.math.animation.Animator;
import io.github.itzispyder.math.animation.PollingAnimator;
import io.github.itzispyder.util.Mth;

import static io.github.itzispyder.Main.*;

public class Camera {

    private final float worldScale;
    public float focalLength;
    private int windowWidth, windowHeight;
    public Vector prevPosition, position, eyePosition;
    public float prevPitch, prevYaw, pitch, yaw;
    public final Animator fovAnimator;
    public float height;

    public Camera() {
        this.focalLength = 0.15F;
        this.worldScale = 100;
        this.windowWidth = 0;
        this.windowHeight = 0;
        this.position = Vector.ZERO;
        this.eyePosition = position.add(0, height, 0);
        this.prevPosition = this.position;
        this.fovAnimator = new PollingAnimator(150, () -> keyboard.accelerating);
        this.height = 1.6F;
    }

    public void updateBounds(Window window) {
        windowWidth = window.getWidth();
        windowHeight = window.getHeight();
    }

    public void onTick() {
        prevPitch = pitch;
        prevYaw = yaw;
        prevPosition = position;

        pitch -= mouse.pollDeltaY() * 0.15F;
        pitch = Mth.clamp(pitch, -90, 90);
        yaw += mouse.pollDeltaX() * 0.15F;

        Vector movement = getMovement();
        position = position.add(Matrix.ROT_Y(yaw * Mth.TO_RAD).transform(movement.mul(2F)));
        processFloorCollision(movement);
        eyePosition = position.add(0, height, 0);
    }

    private void processFloorCollision(Vector movement) {
        if (movement.x == 0 && movement.y == 0 && movement.z == 0)
            return;
        for (int i = 0; i < 10; i++) {
            float yThis = position.y;
            float yFloor = world.tile.getGraphPos(position).y;
            if (yThis > yFloor)
                position = position.add(0, -0.01, 0);
            else if (yThis < yFloor && Math.abs(yThis - yFloor) < 0.5)
                position = position.add(0, 0.01, 0);
        }
    }

    /**
     * Converts 3d space to 2d space using a focalLength value
     * @return (x, y, z) -> (x, y)
     */
    public Vector project(Vector vector, Vector position, Matrix rotation, float focalLength) {
        vector = rotation.transform(vector.sub(position)).mul(worldScale);
        float depth = (vector.z + focalLength) * 0.00025F;
        if (depth <= 0)
            depth = 0.000000000001F;

        return new Vector(
             (vector.x * focalLength) / -depth + windowWidth * 0.5,
             (vector.y * focalLength) / -depth + windowHeight * 0.5,
             0
        );
    }

    /**
     * Converts 3d space to 2d space using a focalLength value
     * @return (x, y, z) -> (x, y)
     */
    public Vector projectTransformedViewSpace(Vector transformedViewSpace, float focalLength) {
        transformedViewSpace = transformedViewSpace.mul(worldScale);
        float depth = (transformedViewSpace.z + focalLength) * 0.00025F;

        return new Vector(
                (transformedViewSpace.x * focalLength) / -depth + windowWidth * 0.5,
                (transformedViewSpace.y * focalLength) / -depth + windowHeight * 0.5,
                0
        );
    }

    public Vector getRotationVector() {
        return Matrix.rotationThirdPerson(this, 1).transform(new Vector(0, 0, 1));
    }

    private static Vector getMovement() {
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
        if (keyboard.ascend) {
            movement = movement.add(0, 1, 0);
        }
        if (keyboard.descend) {
            movement = movement.add(0, -1, 0);
        }
        return movement.mul(0.3F);
    }
}
