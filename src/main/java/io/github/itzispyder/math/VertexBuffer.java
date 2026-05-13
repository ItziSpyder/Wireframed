package io.github.itzispyder.math;

import io.github.itzispyder.util.Mth;

import java.awt.*;

public class VertexBuffer {

    private final VertexBufferEntry[] buffer;
    private int size;

    public VertexBuffer(int capacity) {
        this.buffer = new VertexBufferEntry[capacity];
    }

    public void vertex(Vertex vertex) {
        if (vertex != null)
            buffer[size++] = vertex;
    }

    public VertexBufferEntry fetch(int index) {
        return buffer[index];
    }

    public void swap(VertexFormat format) {
        download(format);
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

    public VertexBufferEntry[] getArray() {
        return buffer;
    }

    private void download(VertexBufferEntry entry) {
        if (entry != null)
            buffer[size++] = entry;
    }

    public void uploadTo(VertexBuffer dest) {
        for (int i = 0; i < size; i++)
            dest.download(buffer[i]);
    }

    public void drawTo(Camera camera, Graphics graphics, float tickDelta) {
        Graphics2D context = (Graphics2D) graphics;
        context.setColor(Color.WHITE);

        Vector position = Mth.lerp(camera.prevPosition, camera.position, tickDelta).add(0, camera.height, 0);
        Matrix rotation = Matrix.rotationFirstPerson(camera, tickDelta);
        float focalLength = Mth.lerp(camera.focalLength, camera.focalLength - 0.069F, camera.fovAnimator.getProgressClamped());

        Vector v1, v2;
        int index = 0;
        VertexFormat currFormat = VertexFormat.LINES;
        int[] xPoints = new int[4], yPoints = new int[4];

        while (index < size) {
            if (buffer[index] instanceof VertexFormat f) {
                currFormat = f;
                index++;
                continue;
            }

            int color = ((Vertex) buffer[index]).color;
            int a = color >> 24 & 0xFF;
            int r = color >> 16 & 0xFF;
            int g = color >> 8 & 0xFF;
            int b = color & 0xFF;
            context.setColor(new Color(r, g, b, a));

            switch (currFormat) {
                case LINES -> {
                    v1 = (Vertex) buffer[index++];
                    v2 = (Vertex) buffer[index++];
                    v1 = rotation.transform(v1.sub(position));
                    v2 = rotation.transform(v2.sub(position));

                    if (v1.z < 0 || v2.z < 0)
                        continue;

                    v1 = camera.projectTransformedViewSpace(v1, focalLength);
                    v2 = camera.projectTransformedViewSpace(v2, focalLength);
                    context.drawLine((int)v1.x, (int)v1.y, (int)v2.x, (int)v2.y);
                }
                case QUADS -> {
                    Vector v3, v4;
                    v1 = (Vertex) buffer[index++];
                    v2 = (Vertex) buffer[index++];
                    v3 = (Vertex) buffer[index++];
                    v4 = (Vertex) buffer[index++];
                    v1 = rotation.transform(v1.sub(position));
                    v2 = rotation.transform(v2.sub(position));
                    v3 = rotation.transform(v3.sub(position));
                    v4 = rotation.transform(v4.sub(position));

                    if (v1.z < 0 || v2.z < 0 || v3.z < 0 || v4.z < 0)
                        continue;

                    v1 = camera.projectTransformedViewSpace(v1, focalLength);
                    v2 = camera.projectTransformedViewSpace(v2, focalLength);
                    v3 = camera.projectTransformedViewSpace(v3, focalLength);
                    v4 = camera.projectTransformedViewSpace(v4, focalLength);
                    xPoints[0] = (int)v1.x;
                    xPoints[1] = (int)v2.x;
                    xPoints[2] = (int)v3.x;
                    xPoints[3] = (int)v4.x;
                    yPoints[0] = (int)v1.y;
                    yPoints[1] = (int)v2.y;
                    yPoints[2] = (int)v3.y;
                    yPoints[3] = (int)v4.y;
                    context.fillPolygon(xPoints, yPoints, 4);
                }
            }
        }
    }
}
