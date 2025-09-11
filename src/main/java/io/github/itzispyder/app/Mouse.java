package io.github.itzispyder.app;

import io.github.itzispyder.render.entity.SphereBullet;

import java.awt.*;

import static io.github.itzispyder.Main.*;

public class Mouse {

    private int prevX, prevY, x, y, deltaX, deltaY;

    public Mouse() {

    }

    public void onClick(int button, int action) {
        if (action == 0 && !keyboard.paused) {
            SphereBullet bullet = new SphereBullet(0.2F);
            bullet.position = camera.position;
            bullet.velocity = camera.getRotationVector().mul(3).applyRandomization(0.1F);
            world.addEntity(bullet);
        }
    }

    public void moveTo(int x, int y) {
        prevX = this.x;
        prevY = this.y;

        this.x = x;
        this.y = y;

        deltaX += (x - prevX);
        deltaY += (y - prevY);
    }

    public void zero(Window window) {
        this.moveTo(window.getWidth() / 2, window.getHeight() / 2);
        deltaX = deltaY = 0;
    }

    public void syncCursor(Window window) {
        try {
            Robot robot = new Robot();
            robot.mouseMove(window.getX() + x, window.getY() + y);
        }
        catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    public int pollDeltaX() {
        int val = this.deltaX;
        this.deltaX = 0;
        return val;
    }

    public int pollDeltaY() {
        int val = this.deltaY;
        this.deltaY = 0;
        return val;
    }

    public int getPrevX() {
        return prevX;
    }

    public int getPrevY() {
        return prevY;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
