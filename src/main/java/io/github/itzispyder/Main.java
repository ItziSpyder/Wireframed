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
        camera.position = Vector.ZERO;
        vertexBuffer = new VertexBuffer(1024 * 1024);
        world = new WorldManager();

        // map
        world.addEntity(new StarBox());
        world.addEntity(new Sphere(5));
        world.tile = Gen.GRAPH_PILLARS;
        world.addEntity(world.tile);

        Gen.generateWorld(world, camera);

        // crosshair
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