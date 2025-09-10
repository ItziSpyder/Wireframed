package io.github.itzispyder.render;

import io.github.itzispyder.math.VertexBuffer;

import java.util.ArrayList;
import java.util.List;

public class WorldManager {

    private final List<Voxel> voxels;

    public WorldManager() {
        this.voxels = new ArrayList<>();
    }

    public void render(VertexBuffer buf) {
        for (int i = 0; i < voxels.size(); i++) {
            voxels.get(i).render(buf);
        }
    }

    public List<Voxel> getVoxels() {
        return voxels;
    }
}
