package io.github.itzispyder.app;

import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import static io.github.itzispyder.Main.mouse;
import static io.github.itzispyder.Main.window;

public class Keyboard {

    private final Set<Integer> pressedKeys;
    public boolean forward, backward, left, right, paused, ascend, descend;
    public boolean accelerating;

    public Keyboard() {
        this.pressedKeys = new HashSet<>();
    }

    public void onTick() {
        forward = backward = left = right = ascend = descend = false;
        for (int key: pressedKeys) switch (key) {
            case KeyEvent.VK_W -> forward = true;
            case KeyEvent.VK_A -> left = true;
            case KeyEvent.VK_S -> backward = true;
            case KeyEvent.VK_D -> right = true;
            case KeyEvent.VK_SHIFT -> descend = true;
            case KeyEvent.VK_SPACE -> ascend = true;
        }

        if (!paused && window.isFocused()) {
            mouse.zero(window);
            mouse.syncCursor(window);
        }
    }

    public void pressKey(int keycode) {
        if (keycode == KeyEvent.VK_ESCAPE)
            paused = !paused;
        if (keycode == KeyEvent.VK_W)
            accelerating = true;
        pressedKeys.add(keycode);
    }

    public void releaseKey(int keycode) {
        if (keycode == KeyEvent.VK_W)
            accelerating = false;
        pressedKeys.remove(keycode);
    }
}
