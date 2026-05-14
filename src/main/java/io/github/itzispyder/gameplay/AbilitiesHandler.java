package io.github.itzispyder.gameplay;

import io.github.itzispyder.math.Matrix;
import io.github.itzispyder.math.Vector;
import io.github.itzispyder.render.entity.SphereBullet;

import static io.github.itzispyder.Main.*;

public class AbilitiesHandler {

    public static void handleProjectiles() {
        if (mouse.right) {
            Vector spawn = camera.eyePosition.sub(0, 0.5F, 0).add(camera.getRotationVector());
            SphereBullet bullet = new SphereBullet(spawn, Matrix.rotationThirdPerson(camera, 1), 0.5F);
            bullet.velocity = camera.getRotationVector().mul(1);
            bullet.gravity = true;
            world.addEntity(bullet);
        }
        else if (mouse.left) {
            Vector spawn = camera.eyePosition.sub(0, 0.5F, 0).add(camera.getRotationVector());
            SphereBullet bullet = new SphereBullet(spawn, Matrix.rotationThirdPerson(camera, 1), 0.5F);
            bullet.velocity = camera.getRotationVector().mul(0.5F).applyRandomization(0.15F);
            bullet.gravity = true;
            world.addEntity(bullet);
        }
    }

    public static void handleDash(int amount) {
        camera.position = camera.position.add(camera.getRotationVector().mul(-10 * amount));
    }
}
