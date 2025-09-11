package io.github.itzispyder.ai_generated.entity;

import io.github.itzispyder.math.Vector;
import io.github.itzispyder.math.VertexBuffer;
import io.github.itzispyder.render.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Tree extends Entity {

    private static final float TRUNK_HEIGHT = 10f;
    private static final float TRUNK_RADIUS = 1.25f;
    private static final int TRUNK_RADIAL_SEGMENTS = 16;
    private static final int TRUNK_VERTICAL_SEGMENTS = 12;

    private static final int NUM_BRANCHES = 8;
    private static final int BRANCH_SEGMENTS = 5;
    private static final float BRANCH_LENGTH_MIN = 3.0f;
    private static final float BRANCH_LENGTH_MAX = 6.0f;

    private static final int LEAF_DENSITY = 24;
    private static final float LEAF_CLUSTER_RADIUS = 1.2f;

    private static final int COLOR_BARK = 0xFF6B3E26;
    private static final int COLOR_BARK_DARK = 0xFF472B1A;
    private static final int COLOR_LEAF = 0xFF2E8B57;
    private static final int COLOR_LEAF_LIGHT = 0xFF66CDAA;
    private static final int COLOR_ROOT = 0xFF4A2F1E;

    private final List<VertexLine> cachedLines = new ArrayList<>();

    public Tree(Vector position) {
        super(position);
        generate(); // build geometry once
    }

    private void generate() {
        Random rand = new Random(31L * position.hashCode());

        // --- TRUNK ---
        Vector[][] rings = new Vector[TRUNK_VERTICAL_SEGMENTS + 1][TRUNK_RADIAL_SEGMENTS];
        for (int yi = 0; yi <= TRUNK_VERTICAL_SEGMENTS; yi++) {
            float fy = (float) yi / (float) TRUNK_VERTICAL_SEGMENTS;
            float y = fy * TRUNK_HEIGHT;
            float radius = TRUNK_RADIUS * (1.0f - 0.6f * fy);
            float jitter = (rand.nextFloat() - 0.5f) * 0.05f;

            for (int ri = 0; ri < TRUNK_RADIAL_SEGMENTS; ri++) {
                float angle = (2f * (float) Math.PI) * ri / TRUNK_RADIAL_SEGMENTS;
                float rx = (float) Math.cos(angle) * (radius + jitter);
                float rz = (float) Math.sin(angle) * (radius + jitter);
                rings[yi][ri] = position.add(rx, y, rz);
            }
        }
        // trunk lines
        for (int yi = 0; yi < TRUNK_VERTICAL_SEGMENTS; yi++) {
            for (int ri = 0; ri < TRUNK_RADIAL_SEGMENTS; ri++) {
                Vector a = rings[yi][ri];
                Vector b = rings[yi][(ri + 1) % TRUNK_RADIAL_SEGMENTS];
                Vector c = rings[yi + 1][ri];
                cachedLines.add(new VertexLine(a, b, COLOR_BARK));
                cachedLines.add(new VertexLine(a, c, COLOR_BARK_DARK));
            }
        }

        // --- ROOTS ---
        for (int r = 0; r < 6; r++) {
            float angle = (2f * (float) Math.PI) * r / 6f;
            float rl = 1.0f + rand.nextFloat() * 0.8f;
            Vector tip = position.add((float) Math.cos(angle) * rl, -0.4f, (float) Math.sin(angle) * rl);
            cachedLines.add(new VertexLine(position, tip, COLOR_ROOT));
        }

        // --- BRANCHES ---
        for (int i = 0; i < NUM_BRANCHES; i++) {
            float heightFrac = 0.3f + rand.nextFloat() * 0.6f;
            float baseY = heightFrac * TRUNK_HEIGHT;
            float angle = (2f * (float) Math.PI) * i / NUM_BRANCHES + (rand.nextFloat() - 0.5f);
            float radiusAtY = TRUNK_RADIUS * (1.0f - 0.6f * heightFrac);
            Vector base = position.add((float) Math.cos(angle) * radiusAtY, baseY, (float) Math.sin(angle) * radiusAtY);

            float length = BRANCH_LENGTH_MIN + rand.nextFloat() * (BRANCH_LENGTH_MAX - BRANCH_LENGTH_MIN);
            float pitch = (20f + rand.nextFloat() * 40f) * ((float) Math.PI / 180f);

            Vector prev = base;
            for (int s = 1; s <= BRANCH_SEGMENTS; s++) {
                float t = (float) s / (float) BRANCH_SEGMENTS;
                float dx = (float) Math.cos(angle) * (length * t);
                float dz = (float) Math.sin(angle) * (length * t);
                float dy = (float) Math.sin(pitch) * (length * t);

                Vector curr = position.add((float) Math.cos(angle) * radiusAtY + dx,
                        baseY + dy,
                        (float) Math.sin(angle) * radiusAtY + dz);

                cachedLines.add(new VertexLine(prev, curr, COLOR_BARK));
                prev = curr;
            }
            makeLeafCluster(rand, prev, LEAF_CLUSTER_RADIUS * (0.8f + rand.nextFloat() * 0.6f));
        }

        // --- CROWN CLUSTER ---
        Vector crown = position.add(0, TRUNK_HEIGHT, 0);
        makeLeafCluster(rand, crown, LEAF_CLUSTER_RADIUS * 1.6f);
    }

    private void makeLeafCluster(Random rand, Vector center, float radius) {
        for (int i = 0; i < LEAF_DENSITY; i++) {
            float theta = (2f * (float) Math.PI) * i / LEAF_DENSITY + (rand.nextFloat() - 0.5f) * 0.2f;
            float phi = (rand.nextFloat() - 0.3f) * (float) Math.PI * 0.6f;
            float r = radius * (0.7f + rand.nextFloat() * 0.6f);

            float dx = (float) (Math.cos(theta) * Math.cos(phi)) * r;
            float dy = (float) Math.sin(phi) * r;
            float dz = (float) (Math.sin(theta) * Math.cos(phi)) * r;

            Vector tip = center.add(dx, dy, dz);
            int color = (rand.nextFloat() < 0.25f) ? COLOR_LEAF_LIGHT : COLOR_LEAF;
            cachedLines.add(new VertexLine(center, tip, color));
        }
    }

    @Override
    public void render(VertexBuffer buf, float tickDelta) {
        for (VertexLine line : cachedLines) {
            buf.vertex(line.start, line.color);
            buf.vertex(line.end, line.color);
        }
    }

    private static class VertexLine {
        final Vector start, end;
        final int color;
        VertexLine(Vector s, Vector e, int c) { start = s; end = e; color = c; }
    }
}
