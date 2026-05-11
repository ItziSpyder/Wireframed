package io.github.itzispyder.render;

import io.github.itzispyder.math.Vector;
import io.github.itzispyder.math.VertexBuffer;

import java.util.function.BiFunction;

public class GraphFunction extends Entity {

    private final VertexBuffer buf;
    private final int color;

    public GraphFunction(Vector position, int minX, int minZ, int maxX, int maxZ, double step, BiFunction<Double, Double, Double> f, int color) {
        super(position);
        this.color = color;

        int wid = (int) Math.ceil((maxX - minX) / step);
        int len = (int) Math.ceil((maxZ - minZ) / step);

        int bufLen = wid * len * 8;
        buf = new VertexBuffer(bufLen);

        for (int i = 0; i < wid; i++) {
            double x = minX + (i * step);
            double nextX = minX + ((i + 1) * step);

            for (int j = 0; j < len; j++) {
                double z = minZ + (j * step);
                double nextZ = minZ + ((j + 1) * step);

                buf.vertex(new Vector(x, f.apply(x, z), z), color);
                buf.vertex(new Vector(nextX, f.apply(nextX, z), z), color);
                buf.vertex(new Vector(nextX, f.apply(nextX, z), z), color);
                buf.vertex(new Vector(nextX, f.apply(nextX, nextZ), nextZ), color);
                buf.vertex(new Vector(nextX, f.apply(nextX, nextZ), nextZ), color);
                buf.vertex(new Vector(x, f.apply(x, nextZ), nextZ), color);
                buf.vertex(new Vector(x, f.apply(x, nextZ), nextZ), color);
                buf.vertex(new Vector(x, f.apply(x, z), z), color);
            }
        }
    }

    @Override
    public void render(VertexBuffer buf, float tickDelta) {
        this.buf.uploadTo(buf);
    }
}
