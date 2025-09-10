package io.github.itzispyder.math;

import java.awt.*;

public class VertexBuffer {

    private final Vector[] buffer;
    private int size;

    public VertexBuffer(int capacity) {
        this.buffer = new Vector[capacity];
    }

    public void vertex(Vector vertex) {
        if (vertex != null)
            buffer[size++] = vertex;
    }

    public void clear() {
        size = 0;
    }

    public int getSize() {
        return size;
    }

    public void drawTo(Camera camera, Graphics g) {
        Graphics2D context = (Graphics2D) g;
        context.setColor(Color.WHITE);

        Vector v1, v2;
        for (int i = 0; i < size; i += 2) {
            v1 = camera.project(buffer[i]);
            v2 = camera.project(buffer[i + 1]);
            context.drawLine((int)v1.x, (int)v1.y, (int)v2.x, (int)v2.y);
        }
        this.clear();
    }
}
