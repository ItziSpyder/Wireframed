package io.github.itzispyder.app;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import static io.github.itzispyder.Main.mouse;
import static io.github.itzispyder.Main.window;

public class Keyboard {

    private final List<Integer> pressedKeys;
    public boolean forward, backward, left, right, paused, ascend, descend;
    public boolean accelerating, fullScreen;

    public Keyboard() {
        this.pressedKeys = new ArrayList<>();
    }

    public void onTick() {
        forward = backward = left = right = ascend = descend = false;

        for (int i = pressedKeys.size() - 1; i >= 0; i--) switch (pressedKeys.get(i)) {
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
        if (pressedKeys.contains(keycode))
            return;
        if (keycode == KeyEvent.VK_ESCAPE)
            paused = !paused;
        if (keycode == KeyEvent.VK_W)
            accelerating = true;
        if (keycode == KeyEvent.VK_F11) {
            fullScreen = !fullScreen;
            window.setExtendedState(fullScreen ? JFrame.MAXIMIZED_BOTH : JFrame.NORMAL);
        }
        pressedKeys.add(keycode);
    }

    public void releaseKey(int keycode) {
        if (keycode == KeyEvent.VK_W)
            accelerating = false;
        pressedKeys.remove((Object) keycode);
    }
}
