package io.github.itzispyder.render;

import io.github.itzispyder.math.Vector;
import io.github.itzispyder.math.VertexBuffer;

import java.util.function.BiFunction;

public class GraphFunction extends Entity {

    public static final int SEA_LEVEL = -256;

    private final VertexBuffer buf;
    private final BiFunction<Double, Double, Double> f;
    private final int minX, minZ, maxX, maxZ;

    public GraphFunction(Vector position, int minX, int minZ, int maxX, int maxZ, double step, BiFunction<Double, Double, Double> f, int color) {
        super(position);
        this.f = f;
        this.minX = minX;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxZ = maxZ;

        int wid = (int) Math.ceil((maxX - minX) / step);
        int len = (int) Math.ceil((maxZ - minZ) / step);

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

//        for (float x = minX; x <= maxX; x = (float) (x + step)) {
//            for (float z = minZ; z <= maxZ; z = (float) (z + step)) {
//                buf.vertex(position.add(x, f.apply((double) x, (double) z), z), color);
//                buf.vertex(position.add(x + step, f.apply((double) x + step, (double) z), z), color);
//                buf.vertex(position.add(x + step, f.apply((double) x + step, (double) z + step), z + step), color);
//                buf.vertex(position.add(x, f.apply((double) x, (double) z + step ), z + step), color);
//            }
//        }
    }

    public float getHeightAt(double worldX, double worldZ) {
        double localX = worldX - position.x;
        double localZ = worldZ - position.z;

        if (localX < minX || localX > maxX || localZ < minZ || localZ > maxZ)
            return SEA_LEVEL;
        return f.apply(localX, localZ).floatValue();
    }

    public Vector getGraphAt(Vector pos) {
        return new Vector(pos.x, getHeightAt(pos.x, pos.z), pos.z);
    }

    @Override
    public void render(VertexBuffer buf, float tickDelta) {
        this.buf.uploadTo(buf);
    }
}
