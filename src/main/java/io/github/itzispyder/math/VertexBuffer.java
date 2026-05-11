package io.github.itzispyder.math;

import io.github.itzispyder.util.Mth;

import java.awt.*;

public class VertexBuffer {

    private final Vertex[] buffer;
    private int size;

    public VertexBuffer(int capacity) {
        this.buffer = new Vertex[capacity];
    }

    public void vertex(Vertex vertex) {
        if (vertex != null)
            buffer[size++] = vertex;
    }

    public void vertex(Vector vertex) {
        this.vertex(new Vertex(vertex.x, vertex.y, vertex.z));
    }

    public void vertex(Vector vertex, int color) {
        this.vertex(new Vertex(vertex.x, vertex.y, vertex.z, color));
    }

    public void clear() {
        size = 0;
    }

    public int getSize() {
        return size;
    }

    public int getCapacity() {
        return buffer.length;
    }

    public void uploadTo(VertexBuffer dest) {
        for (int i = 0; i < size; i++)
            dest.vertex(buffer[i]);
    }

    public void drawTo(Camera camera, Graphics graphics, float tickDelta) {
        Graphics2D context = (Graphics2D) graphics;
        context.setColor(Color.WHITE);

        Vector position = Mth.lerp(camera.prevPosition, camera.position, tickDelta);
        Matrix rotation = Matrix.rotationFirstPerson(camera, tickDelta);
        float focalLength = Mth.lerp(camera.focalLength, camera.focalLength - 0.069F, camera.fovAnimator.getProgressClamped());

        Vector v1, v2;
        for (int i = 0; i < size; i += 2) {
            int color = buffer[i].color;

            v1 = rotation.transform(buffer[i].sub(position));
            v2 = rotation.transform(buffer[i + 1].sub(position));

            if (v1.z < 0 || v2.z < 0)
                continue;

            v1 = camera.projectTransformedViewSpace(v1, focalLength);
            v2 = camera.projectTransformedViewSpace(v2, focalLength);
            int a = color >> 24 & 0xFF;
            int r = color >> 16 & 0xFF;
            int g = color >> 8 & 0xFF;
            int b = color & 0xFF;
            context.setColor(new Color(r, g, b, a));
            context.drawLine((int)v1.x, (int)v1.y, (int)v2.x, (int)v2.y);
        }
    }
}
