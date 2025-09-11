package io.github.itzispyder;

import io.github.itzispyder.app.Keyboard;
import io.github.itzispyder.app.Mouse;
import io.github.itzispyder.app.Window;
import io.github.itzispyder.math.Camera;
import io.github.itzispyder.math.Vector;
import io.github.itzispyder.math.VertexBuffer;
import io.github.itzispyder.render.StarBox;
import io.github.itzispyder.render.WorldManager;
import io.github.itzispyder.render.entity.Sphere;
import io.github.itzispyder.render.entity.Tile;
import io.github.itzispyder.render.entity.Voxel;

public class Main {

    public static Window window;
    public static long time, nextFrameTime;
    public static Camera camera;
    public static Keyboard keyboard;
    public static Mouse mouse;
    public static VertexBuffer vertexBuffer;
    public static WorldManager world;
    public static int fps, frame;

    private static void init() {
        keyboard = new Keyboard();
        mouse = new Mouse();
        window = new Window("Wireframed");
        window.open();
        camera = new Camera();
        camera.updateBounds(window);
        camera.position = new Vector(0, 2, 0);
        vertexBuffer = new VertexBuffer(1024 * 100);
        world = new WorldManager();

        // map

        world.addEntity(new StarBox());
        world.addEntity(new Sphere(5));

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

    public static float tickDelta() {
        return 1 - (time - System.currentTimeMillis()) / 50F;
    }

    public static void onTick() {
        if (keyboard.paused)
            return;

        try {
            camera.onTick();
            keyboard.onTick();
            world.onTick();
        }
        catch (IndexOutOfBoundsException ignore) {}
    }

    public static void onRender() {
        window.getRenderPanel().repaint();
    }

    public static void main(String[] args) {
        init();
        startGameLoop();
    }

    private static void startGameLoop() {
        time = nextFrameTime = System.currentTimeMillis();
        while (window.isVisible()) {
            long sysTime = System.currentTimeMillis();
            if (sysTime >= time) {
                time += 50;
                onTick();
            }
            if (sysTime >= nextFrameTime) {
                nextFrameTime += 1000;
                fps = frame;
                frame = 0;
            }

            onRender();
        }
    }
}