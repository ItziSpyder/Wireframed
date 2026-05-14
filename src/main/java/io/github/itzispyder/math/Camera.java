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
    public Vector velocity;
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
        this.fovAnimator = new PollingAnimator(150, () -> keyboard.accelerating || keyboard.fly);
        this.height = 1.6F;
        this.velocity = Vector.ZERO;
    }

    public void updateBounds(Window window) {
        windowWidth = window.getWidth();
        windowHeight = window.getHeight();
    }

    public void onTick() {
        prevPitch = pitch;
        prevYaw = yaw;
        prevPosition = position;

        pitch -= mouse.pollDeltaY() * 0.05F;
        pitch = Mth.clamp(pitch, -90, 90);
        yaw += mouse.pollDeltaX() * 0.05F;

        Vector movement = Matrix.ROT_Y(yaw * Mth.TO_RAD).transform(getMovement());
        Vector predictPosition = position.add(movement).add(velocity);

        float predictFloorY = world.tile.getHeightAt(predictPosition.x, predictPosition.z);
        boolean onGround = position.y <= predictFloorY + 0.05F;

        if (!keyboard.fly)
            this.handleMovementCollisions(movement, onGround, predictFloorY);
        else
            position = predictPosition;

        eyePosition = position.add(0, height, 0);
    }

    private void handleMovementCollisions(Vector movement, boolean onGround, float predictFloorY) {
        if (onGround) {
            if (keyboard.ascend)
                velocity = velocity.withY(0.25F);
            else if (velocity.y < 0)
                velocity = velocity.withY(0);
        }
        else {
            velocity = velocity.withY(velocity.y - 0.067F);
        }

        if (predictFloorY - position.y < 0.6F) {
            position = position.add(movement).add(velocity);

            if (position.y < predictFloorY)
                position = position.withY(predictFloorY);
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

    public static Vector getMovement() {
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
        return movement.mul(keyboard.fly ? 1 : 0.3F);
    }
}
