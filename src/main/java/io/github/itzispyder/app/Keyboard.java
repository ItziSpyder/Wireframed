package io.github.itzispyder.app;

import javax.swing.event.MenuKeyEvent;
import java.util.HashSet;
import java.util.Set;

import static io.github.itzispyder.Main.mouse;
import static io.github.itzispyder.Main.window;

public class Keyboard {

    private final Set<Integer> pressedKeys;
    public boolean forward, backward, left, right, paused;

    public Keyboard() {
        this.pressedKeys = new HashSet<>();
    }

    public void onTick() {
        forward = backward = left = right = false;
        for (int key: pressedKeys) switch (key) {
            case MenuKeyEvent.VK_W -> forward = true;
            case MenuKeyEvent.VK_A -> left = true;
            case MenuKeyEvent.VK_S -> backward = true;
            case MenuKeyEvent.VK_D -> right = true;
        }

        if (!paused && window.isFocused()) {
            mouse.zero(window);
            mouse.syncCursor(window);
        }
    }

    public void pressKey(int keycode) {
        if (keycode == MenuKeyEvent.VK_ESCAPE)
            paused = !paused;
        pressedKeys.add(keycode);
    }

    public void releaseKey(int keycode) {
        pressedKeys.remove(keycode);
    }
}
