package io.github.itzispyder.render;

import io.github.itzispyder.Main;
import io.github.itzispyder.math.VertexBuffer;

import java.util.ArrayList;
import java.util.List;

public class WorldManager {

    private final List<Entity> entities;

    public WorldManager() {
        this.entities = new ArrayList<>();
    }

    public void render(VertexBuffer buf) {
        for (int i = entities.size() - 1; i >= 0; i--) {
            entities.get(i).render(buf, Main.tickDelta());
        }
    }

    public void onTick() {
        for (int i = entities.size() - 1; i >= 0; i--) {
            entities.get(i).onTick();
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
