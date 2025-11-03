package io.github.itzispyder.render.entity;

import io.github.itzispyder.math.Vector;
import io.github.itzispyder.math.VertexBuffer;
import io.github.itzispyder.render.Entity;
import io.github.itzispyder.util.Trig;

public class Tree extends Entity {

    private static final int colorTrunk = 0xFF8C6803;
    private static final int colorLeaves = 0xFF00BF39;
    private final VertexBuffer buffer;

    public Tree(Vector position) {
        super(position);
        this.buffer = new VertexBuffer(636);
        this.buildBuffer(buffer);
    }

    @Override
    public void render(VertexBuffer buf, float tickDelta) {
        buffer.uploadTo(buf);
    }

    private void buildBuffer(VertexBuffer buf) {
        this.bufferTrunk(buf);
    }

    private void bufferTrunk(VertexBuffer buf) {
        float trunkRadius = 1;
        int trunkHeight = 10;

        for (int y = 0; y < trunkHeight; y++) {
            int iRand1 = (int)(8 * Math.random()) * 45;
            int iRand2 = (int)(8 * Math.random()) * 45;
            for (int i = 0; i < 360; i += 45) {
                int i2 = i + 45;
                Vector trunkPoint = position.add(Trig.cos(i) * trunkRadius, y, Trig.sin(i) * trunkRadius);

                buf.vertex(trunkPoint, colorTrunk);
                buf.vertex(position.add(Trig.cos(i2) * trunkRadius, y, Trig.sin(i2) * trunkRadius), colorTrunk);

                if (y == 0) {
                    buf.vertex(position.add(Trig.cos(i) * trunkRadius, 0, Trig.sin(i) * trunkRadius), colorTrunk);
                    buf.vertex(position.add(Trig.cos(i) * (trunkRadius / 2), trunkHeight, Trig.sin(i) * (trunkRadius / 2)), colorTrunk);
                }
                if ((i == iRand1 || i == iRand2) && y >= 5) {
                    this.bufferBranch(buf, trunkPoint);
                }
            }
            trunkRadius -= 0.05F;
        }
        this.bufferLeaves(buf, position.add(0, trunkHeight, 0));
    }

    private void bufferBranch(VertexBuffer buf, Vector from) {
        float length = (float) (3 + Math.random() * 2);
        float toY = (float) (from.y - Math.random());
        Vector dir = from.sub(position.withY(toY)).normalize();
        Vector to = from.add(dir.mul(length)).applyRandomization(0.1F);

        buf.vertex(from, colorTrunk);
        buf.vertex(to, colorTrunk);

        this.bufferLeaves(buf, to);
    }

    private void bufferLeaves(VertexBuffer buf, Vector from) {
        for (int i = 0; i < 20; i++) {
            Vector to = from.applyRandomization(1.5F);

            buf.vertex(from, colorLeaves);
            buf.vertex(to, colorLeaves);
        }
    }
}
