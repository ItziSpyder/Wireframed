package io.github.itzispyder;

import io.github.itzispyder.math.Camera;
import io.github.itzispyder.math.Vector;
import io.github.itzispyder.render.GraphFunction;
import io.github.itzispyder.render.WorldManager;
import io.github.itzispyder.render.entity.Tile;
import io.github.itzispyder.render.entity.Tree;
import io.github.itzispyder.render.entity.Voxel;
import io.github.itzispyder.util.Mth;

public class Gen {

    public static void generateWorld(WorldManager world, Camera camera) {
        int range = 100;
        GraphFunction func = new GraphFunction(Vector.ZERO, -range, -range, range, range, 2, (x, z) -> {
            return Math.cos(0.1 * x) * Math.sin(0.1 * z);
        }, 0xFF02400c);
        world.addEntity(func);

        genTrees(world, camera);
    }

    private static void genFloor(WorldManager world, Camera camera) {
        // mesh floor
        int floorSize = 10;

        for (int x = -floorSize; x <= floorSize; x++) {
            for (int z = -floorSize; z <= floorSize; z++) {
                world.addEntity(new Tile(new Vector(x, 0, z)));
            }
        }

        for (int x = floorSize + 1; x <= floorSize + 20; x++) {
            world.addEntity(new Voxel(new Vector(x, -1, 0)));
        }
    }

    private static void genTrees(WorldManager world, Camera camera) {
        for (int i = 0; i < 360; i += 60) {
            Vector pos = new Vector(Mth.cos(i), 0, Mth.sin(i));
            Tree tree = new Tree(pos.mul(20));
            world.addEntity(tree);
        }

        for (int i = 0; i < 20; i++) {
            Vector pos = Vector.ZERO.applyRandomization(100).withY(0);
            Tree tree = new Tree(pos);
            world.addEntity(tree);
        }
    }
}
