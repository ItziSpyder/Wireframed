package io.github.itzispyder.app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static io.github.itzispyder.Main.*;

public class Window extends JFrame {

    private final JPanel renderPanel;

    public Window(String title) {
        super(title);
        this.renderPanel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                if (world == null)
                    return;

                world.render(vertexBuffer);
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, this.getWidth(), this.getHeight());
                vertexBuffer.drawTo(camera, g);
            }
        };
        this.renderPanel.setBackground(Color.BLACK);
        this.renderPanel.setDoubleBuffered(false);
        this.add(renderPanel);
    }

    public void open() {
        this.setSize(1000, 500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                keyboard.pressKey(e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                keyboard.releaseKey(e.getKeyCode());
            }
        });
        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouse.moveTo(e.getX(), e.getY());
            }
        });
        this.addWindowStateListener(e -> camera.updateBounds(Window.this));
    }

    public JPanel getRenderPanel() {
        return renderPanel;
    }
}
