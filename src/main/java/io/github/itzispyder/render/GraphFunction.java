package io.github.itzispyder.render;

import io.github.itzispyder.math.Vector;
import io.github.itzispyder.math.VertexBuffer;
import io.github.itzispyder.math.VertexBufferEntry;

import java.util.function.BiFunction;

public class GraphFunction extends Entity {

    private final VertexBuffer buf;
    private final int minX, minZ;
    private final double step;
    private final int wid, len;

    public GraphFunction(Vector position, int minX, int minZ, int maxX, int maxZ, double step, BiFunction<Double, Double, Double> f, int color) {
        super(position);
        this.minX = minX;
        this.minZ = minZ;
        this.step = step;
        this.wid = (int) Math.ceil((maxX - minX) / step);
        this.len = (int) Math.ceil((maxZ - minZ) / step);

        int bufLen = wid * len * 8;
        buf = new VertexBuffer(bufLen);

        for (int i = 0; i <= wid; i++) {
            double x = minX + (i * step);
            for (int j = 0; j < len; j++) {
                double z = minZ + (j * step);
                double nextZ = minZ + ((j + 1) * step);

                buf.vertex(position.add(x, f.apply(x, z), z), color);
                buf.vertex(position.add(x, f.apply(x, nextZ), nextZ), color);
            }
        }
        for (int j = 0; j <= len; j++) {
            double z = minZ + (j * step);
            for (int i = 0; i < wid; i++) {
                double x = minX + (i * step);
                double nextX = minX + ((i + 1) * step);

                buf.vertex(position.add(x, f.apply(x, z), z), color);
                buf.vertex(position.add(nextX, f.apply(nextX, z), z), color);
            }
        }
    }

    public Vector getGraphPos(Vector worldPos) {
        VertexBufferEntry entry = getEntryAt(worldPos);
        if (entry instanceof Vector vector)
            return vector;
        return null;
    }

    public VertexBufferEntry getEntryAt(Vector position) {
        return getEntryAt(position.x, position.y, position.z);
    }

    public VertexBufferEntry getEntryAt(double x, double y, double z) {
        VertexBufferEntry closest = null;
        double minDistanceSq = Double.MAX_VALUE;

        for (VertexBufferEntry entry : buf.getArray()) {
            if (!(entry instanceof Vector v))
                continue;

            double dx = x - v.x;
            double dy = y - v.y;
            double dz = z - v.z;
            double distSq = dx * dx + dy * dy + dz * dz;

            if (distSq < minDistanceSq) {
                minDistanceSq = distSq;
                closest = entry;
            }
        }

        return closest;
    }

    public VertexBufferEntry getEntryAtFast(double x, double y, double z) {
        int i = (int) Math.round((x - position.x - minX) / step);
        int j = (int) Math.round((z - position.z - minZ) / step);
        i = Math.max(0, Math.min(i, wid));
        j = Math.max(0, Math.min(j, len));

        int index;
        if (i <= wid && j < len) {
            index = (i * len + j) * 2;
        }
        else {
            int secondLoopOffset = (wid + 1) * len * 2;
            int clampedJ = Math.min(j, len);
            int clampedI = Math.min(i, wid - 1);
            index = secondLoopOffset + (clampedJ * wid + clampedI) * 2;
        }

        return buf.fetch(index);
    }

    @Override
    public void render(VertexBuffer buf, float tickDelta) {
        this.buf.uploadTo(buf);
    }
}
