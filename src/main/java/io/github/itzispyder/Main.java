package io.github.itzispyder;

import io.github.itzispyder.app.Keyboard;
import io.github.itzispyder.app.Mouse;
import io.github.itzispyder.app.Window;
import io.github.itzispyder.math.Camera;
import io.github.itzispyder.math.Vector;
import io.github.itzispyder.math.VertexBuffer;
import io.github.itzispyder.render.Sphere;
import io.github.itzispyder.render.Voxel;
import io.github.itzispyder.render.WorldManager;

public class Main {

    public static Window window;
    public static long time;
    public static Camera camera;
    public static Keyboard keyboard;
    public static Mouse mouse;
    public static VertexBuffer vertexBuffer;
    public static WorldManager world;

    private static void init() {
        keyboard = new Keyboard();
        mouse = new Mouse();
        window = new Window("Wireframed");
        window.open();
        camera = new Camera();
        camera.updateBounds(window);
        camera.setPosition(new Vector(0, 2, 0));
        vertexBuffer = new VertexBuffer(1024 * 45);
        world = new WorldManager();

        world.addEntity(new Sphere(5));

        // mesh floor
        for (int x = -10; x <= 10; x++) {
            for (int z = -10; z <= 10; z++) {
                world.addEntity(new Voxel(new Vector(x, 0, z)));
            }
        }
    }

    public static float tickDelta() {
        return 1 - (time - System.currentTimeMillis()) / 50F;
    }

    public static void onTick() {
        camera.onTick();
        keyboard.onTick();
        world.onTick();
    }

    public static void onRender() {
        window.getRenderPanel().repaint();
    }

    public static void main(String[] args) {
        init();
        startGameLoop();
    }

    private static void startGameLoop() {
        time = System.currentTimeMillis();
        while (window.isVisible()) {
            if (System.currentTimeMillis() >= time) {
                time += 50;
                onTick();
            }
            onRender();
        }
    }
}