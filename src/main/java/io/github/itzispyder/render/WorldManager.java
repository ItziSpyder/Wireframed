package io.github.itzispyder.render;

import io.github.itzispyder.math.Vector;
import io.github.itzispyder.math.VertexBuffer;
import io.github.itzispyder.render.entity.Missile;
import io.github.itzispyder.render.entity.SphereBullet;
import io.github.itzispyder.render.entity.Voxel;

import java.util.ArrayList;
import java.util.List;

import static io.github.itzispyder.Main.*;

public class WorldManager {

    private final List<Entity> entities;

    public WorldManager() {
        this.entities = new ArrayList<>();
    }

    public void render(VertexBuffer buf, float tickDelta) {
        for (int i = entities.size() - 1; i >= 0; i--) {
            entities.get(i).render(buf, tickDelta);
        }
//        this.renderSelection(buf, camera.position.add(camera.getRotationVector().mul(5)));
    }

    public void renderSelection(VertexBuffer buf, Vector v) {
        v = v.floor();
        Voxel.buildVertices(buf, v, 1, 0xFF00B7FF);
        Voxel.buildVertices(buf, v.add(0.2F), 0.6F, 0xFF00B7FF);
    }

    public void onTick() {
        for (int i = entities.size() - 1; i >= 0; i--) {
            entities.get(i).onTick();
        }

        // spawn
        if (Math.random() < 0.05) {
            Vector spawn = Vector.ZERO.applyRandomization(20).withY(45);
            Missile missile = new Missile(spawn);
            missile.velocity = new Vector(0, -0.2F, 0);
            this.addEntity(missile);
        }

        // shoot
        if (!keyboard.paused) {
            if (mouse.right) {
                SphereBullet bullet = new SphereBullet(0.1F);
                bullet.position = camera.position;
                bullet.velocity = camera.getRotationVector().mul(1);
                bullet.gravity = true;
                world.addEntity(bullet);
            }
            else if (mouse.left) {
                SphereBullet bullet = new SphereBullet(0.1F);
                bullet.position = camera.position;
                bullet.velocity = camera.getRotationVector().mul(1).applyRandomization(0.15F);
                world.addEntity(bullet);
            }
        }
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }
}
