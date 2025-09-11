package io.github.itzispyder.app;

import java.awt.*;

public class Mouse {

    private int prevX, prevY, x, y, deltaX, deltaY;
    public boolean left, right;

    public Mouse() {

    }

    public void onClick(int button, int action) {

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
