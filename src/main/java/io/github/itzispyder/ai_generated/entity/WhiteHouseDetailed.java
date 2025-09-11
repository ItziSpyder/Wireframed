package io.github.itzispyder.ai_generated.entity;

import io.github.itzispyder.math.Vector;
import io.github.itzispyder.math.VertexBuffer;
import io.github.itzispyder.render.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Very-detailed White House wireframe (cached geometry).
 * - All math uses floats.
 * - Deterministic generation (seeded by position.hashCode()).
 * - render() replays cached lines.
 */
public class WhiteHouseDetailed extends Entity {

    // --------- Tunables: dimensions & fidelity (meters, approx) ----------
    private static final float MAIN_WIDTH = 52.0f;
    private static final float MAIN_DEPTH = 26.0f;
    private static final float MAIN_HEIGHT = 21.0f;

    private static final float WING_WIDTH = 25.0f;
    private static final float WING_DEPTH = 20.0f;
    private static final float WING_HEIGHT = 15.0f;

    private static final float NORTH_PORTICO_DEPTH = 6.0f;
    private static final float NORTH_PORTICO_WIDTH = 20.0f;
    private static final int   NORTH_PORTICO_COLS  = 6;
    private static final float COLUMN_RADIUS = 0.55f;
    private static final int   COLUMN_RADIAL_SEGMENTS = 12;

    private static final float SOUTH_PORTICO_RADIUS = 9.0f;
    private static final int   SOUTH_PORTICO_COLS   = 10;

    private static final float BALUSTRADE_POST_WIDTH = 0.18f;
    private static final int   BALUSTRADE_COUNT = 36;

    private static final int   WINDOW_ROWS = 3;
    private static final int   WINDOW_COLUMNS = 11;
    private static final float WINDOW_W = 2.4f;
    private static final float WINDOW_H = 2.2f;
    private static final float STEP_HEIGHT = 0.25f;

    // colors (ARGB)
    private static final int COLOR_WALL    = 0xFFFFFFFF;
    private static final int COLOR_SHADOW  = 0xFFBFBFBF;
    private static final int COLOR_ROOF    = 0xFF9E9E9E;
    private static final int COLOR_WINDOW  = 0xFF2F2F2F;
    private static final int COLOR_FLAG    = 0xFF0A6EF6;
    private static final int COLOR_ACCENT  = 0xFFCBCBCB;

    // cached geometry
    private final List<VertexLine> cachedLines = new ArrayList<>();

    public WhiteHouseDetailed(Vector position) {
        super(position);
        generate();
    }

    private void generate() {
        // deterministic per-position
        Random rand = new Random(97L * position.hashCode() + 13L);

        // base corners (origin is center on ground)
        Vector mainOrigin = position.add(-MAIN_WIDTH * 0.5f, 0f, -MAIN_DEPTH * 0.5f);

        // MAIN BLOCK
        makeBox(mainOrigin, MAIN_WIDTH, MAIN_HEIGHT, MAIN_DEPTH, COLOR_WALL);

        // EAST/WEST WINGS
        Vector eastWingOrigin = position.add(MAIN_WIDTH * 0.5f, 0f, -WING_DEPTH * 0.5f + 0f);
        Vector westWingOrigin = position.add(-MAIN_WIDTH * 0.5f - WING_WIDTH, 0f, -WING_DEPTH * 0.5f + 0f);
        makeBox(eastWingOrigin, WING_WIDTH, WING_HEIGHT, WING_DEPTH, COLOR_WALL);
        makeBox(westWingOrigin, WING_WIDTH, WING_HEIGHT, WING_DEPTH, COLOR_WALL);

        // Cornices / stringcourses for main facade
        addStringcourses(mainOrigin, MAIN_WIDTH, MAIN_HEIGHT, MAIN_DEPTH);

        // NORTH PORTICO (front)
        Vector northPorticoCenter = position.add(0f, 0f, -MAIN_DEPTH * 0.5f - NORTH_PORTICO_DEPTH * 0.5f);
        makeNorthPortico(northPorticoCenter, NORTH_PORTICO_WIDTH, NORTH_PORTICO_DEPTH, MAIN_HEIGHT, NORTH_PORTICO_COLS, rand);

        // stairs up to north portico
        makeStairs(position.add(-NORTH_PORTICO_WIDTH * 0.5f, 0f, -MAIN_DEPTH * 0.5f - NORTH_PORTICO_DEPTH), NORTH_PORTICO_WIDTH, 6);

        // SOUTH PORTICO (semi-circular colonnade / balcony)
        Vector southCenter = position.add(0f, 0f, MAIN_DEPTH * 0.5f + SOUTH_PORTICO_RADIUS * 0.1f);
        makeSouthPortico(southCenter, SOUTH_PORTICO_RADIUS, SOUTH_PORTICO_COLS, MAIN_HEIGHT, rand);

        // roof balustrade along main block
        makeBalustrade(position.add(-MAIN_WIDTH * 0.5f, MAIN_HEIGHT, -MAIN_DEPTH * 0.5f), MAIN_WIDTH, MAIN_DEPTH, BALUSTRADE_COUNT);

        // chimneys and roof-objects
        makeRoofDetails(position.add(0f, MAIN_HEIGHT, 0f), rand);

        // windows and doors on main façade (north side)
        makeWindowsAndDoors(mainOrigin, MAIN_WIDTH, MAIN_HEIGHT, WINDOW_ROWS, WINDOW_COLUMNS, rand);

        // small fence / walkway indicators in front (low fence)
        makeFrontFence(position.add(0f, 0f, -MAIN_DEPTH * 0.5f - NORTH_PORTICO_DEPTH - 2.5f), MAIN_WIDTH * 1.2f, rand);

        // central flagpole on roof
        Vector flagBase = position.add(0f, MAIN_HEIGHT + 0.3f, 0f);
        addLine(flagBase, flagBase.add(0f, 6.0f, 0f), COLOR_FLAG);
        // small flag geometry
        addLine(flagBase.add(0.6f, 5.4f, 0f), flagBase.add(1.2f, 5.4f, 0f), COLOR_FLAG);
        addLine(flagBase.add(0.6f, 5.2f, 0f), flagBase.add(1.2f, 5.2f, 0f), COLOR_FLAG);
    }

    // -------- major sub-builders --------

    private void makeNorthPortico(Vector center, float width, float depth, float buildingHeight, int columns, Random rand) {
        // portico base extents
        float halfW = width * 0.5f;
        float baseZ = center.getZ() - depth * 0.5f;
        float baseY = 0f;
        float colHeight = buildingHeight * 0.55f;

        // platform top
        Vector platformTL = position.add(-halfW, 0f, baseZ);
        Vector platformBR = position.add(halfW, 0.15f, baseZ + depth);
        // platform outline
        addLine(platformTL, platformTL.add(width, 0f, 0f), COLOR_ACCENT);
        addLine(platformTL.add(0f, 0f, depth), platformTL.add(width, 0f, depth), COLOR_ACCENT);

        // place columns evenly
        for (int i = 0; i < columns; i++) {
            float t = (float) i / (columns - 1f);
            float x = lerp(-halfW + COLUMN_RADIUS * 1.6f, halfW - COLUMN_RADIUS * 1.6f, t);
            Vector colBase = position.add(x, 0.15f, baseZ + depth * 0.2f);
            makeColumn(colBase, colHeight, COLUMN_RADIUS, COLUMN_RADIAL_SEGMENTS, COLOR_WALL);
        }

        // pediment (triangular gable) resting on entablature above columns
        float pedimentY = colHeight + 0.18f;
        Vector left = position.add(-halfW - 0.2f, pedimentY, center.getZ() - depth * 0.18f);
        Vector right= position.add(halfW + 0.2f, pedimentY, center.getZ() - depth * 0.18f);
        Vector apex = position.add(0f, pedimentY + 0.9f, center.getZ() - depth * 0.18f);
        addLine(left, apex, COLOR_ACCENT);
        addLine(right, apex, COLOR_ACCENT);
        addLine(left, right, COLOR_ACCENT);

        // cornice details (dentils) below pediment
        int dentils = (int) (width / 0.6f);
        for (int i = 0; i < dentils; i++) {
            float dx = -halfW + 0.3f + i * (width / Math.max(1, dentils));
            Vector d0 = position.add(dx, pedimentY - 0.12f, center.getZ() - depth * 0.18f - 0.02f);
            Vector d1 = d0.add(0.2f, 0f, 0f);
            addLine(d0, d1, COLOR_ACCENT);
        }
    }

    private void makeSouthPortico(Vector center, float radius, int columns, float buildingHeight, Random rand) {
        // semicircular colonnade facing south (center is slightly out from main block)
        float colHeight = buildingHeight * 0.48f;
        float arcRadius = radius;

        for (int i = 0; i < columns; i++) {
            float a = lerp((float) -Math.PI/2f, (float) Math.PI/2f, (float) i / (columns - 1f));
            float x = (float) Math.cos(a) * arcRadius;
            float z = (float) Math.sin(a) * arcRadius;
            Vector colBase = position.add(x, 0.15f, z + MAIN_DEPTH * 0.5f + 0.8f);
            makeColumn(colBase, colHeight, COLUMN_RADIUS * 0.9f, COLUMN_RADIAL_SEGMENTS, COLOR_WALL);
        }

        // balcony rail above colonnade
        int rails = columns * 2;
        for (int i = 0; i < rails; i++) {
            float a1 = lerp((float) -Math.PI/2f, (float) Math.PI/2f, (float) i / rails);
            float a2 = lerp((float) -Math.PI/2f, (float) Math.PI/2f, (float) (i + 1) / rails);
            Vector p1 = position.add((float) Math.cos(a1) * (arcRadius + 0.8f), colHeight + 0.25f, (float) Math.sin(a1) * (arcRadius) + MAIN_DEPTH * 0.5f + 0.8f);
            Vector p2 = position.add((float) Math.cos(a2) * (arcRadius + 0.8f), colHeight + 0.25f, (float) Math.sin(a2) * (arcRadius) + MAIN_DEPTH * 0.5f + 0.8f);
            addLine(p1, p2, COLOR_ACCENT);
        }
    }

    private void makeColumn(Vector baseCenter, float height, float radius, int radialSegs, int color) {
        // vertical cylinder approximation by stacked rings
        int rings = 8;
        for (int r = 0; r <= rings; r++) {
            float t = (float) r / (float) rings;
            float y = t * height;
            for (int s = 0; s < radialSegs; s++) {
                float ang = TWO_PI() * s / radialSegs;
                float nx = (float) Math.cos(ang) * radius;
                float nz = (float) Math.sin(ang) * radius;
                Vector p = baseCenter.add(nx, y, nz);
                Vector q = baseCenter.add((float) Math.cos(TWO_PI() * (s + 1) / radialSegs) * radius, y, (float) Math.sin(TWO_PI() * (s + 1) / radialSegs) * radius);
                addLine(p, q, color);
                if (r > 0) {
                    Vector prev = baseCenter.add((float) Math.cos(ang) * radius, (r - 1f) * (height / rings), (float) Math.sin(ang) * radius);
                    addLine(prev, p, color);
                }
            }
        }
        // simple capital & base
        Vector capLeft = baseCenter.add(-radius * 1.2f, height + 0.02f, -radius * 0.4f);
        Vector capRight= baseCenter.add(radius * 1.2f, height + 0.02f, radius * 0.4f);
        addLine(capLeft, capRight, COLOR_ACCENT);
        addLine(baseCenter.add(-radius * 1.1f, -0.02f, -radius * 0.4f), baseCenter.add(radius * 1.1f, -0.02f, radius * 0.4f), COLOR_ACCENT);
    }

    private void makeBox(Vector origin, float w, float h, float d, int color) {
        Vector a = origin;
        Vector b = origin.add(w, 0f, 0f);
        Vector c = origin.add(w, 0f, d);
        Vector d1= origin.add(0f, 0f, d);

        Vector aT = a.add(0f, h, 0f);
        Vector bT = b.add(0f, h, 0f);
        Vector cT = c.add(0f, h, 0f);
        Vector dT = d1.add(0f, h, 0f);

        // bottom rectangle (ground-level)
        addLine(a, b, color); addLine(b, c, color); addLine(c, d1, color); addLine(d1, a, color);
        // verticals
        addLine(a, aT, color); addLine(b, bT, color); addLine(c, cT, color); addLine(d1, dT, color);
        // top rectangle
        addLine(aT, bT, color); addLine(bT, cT, color); addLine(cT, dT, color); addLine(dT, aT, color);

        // cornice line around top
        addLine(aT.add(0.1f, 0f, 0.1f), bT.add(-0.1f, 0f, 0.1f), COLOR_ACCENT);
        addLine(bT.add(-0.1f, 0f, 0.1f), cT.add(-0.1f, 0f, -0.1f), COLOR_ACCENT);
    }

    private void addStringcourses(Vector mainOrigin, float w, float h, float d) {
        // horizontal trim lines across facade
        int lines = 3;
        for (int i = 1; i <= lines; i++) {
            float y = (h / (lines + 1f)) * i;
            Vector left = position.add(-w * 0.5f, y, -d * 0.5f);
            Vector right = position.add(w * 0.5f, y, -d * 0.5f);
            addLine(left, right, COLOR_ACCENT);
        }
    }

    private void makeStairs(Vector origin, float width, int steps) {
        // origin is left-front of stairs base on ground; build steps toward portico
        float stepDepth = 0.25f;
        for (int i = 0; i < steps; i++) {
            float y = (i + 1) * STEP_HEIGHT;
            Vector leftFront = origin.add(i * stepDepth, y, 0f);
            Vector rightFront = leftFront.add(width, 0f, 0f);
            Vector leftBack = leftFront.add(0f, 0f, stepDepth);
            Vector rightBack = leftBack.add(width, 0f, 0f);
            addLine(leftFront, rightFront, COLOR_ACCENT);
            addLine(leftBack, rightBack, COLOR_ACCENT);
            addLine(leftFront, leftBack, COLOR_ACCENT);
            addLine(rightFront, rightBack, COLOR_ACCENT);
        }
    }

    private void makeBalustrade(Vector corner, float w, float d, int posts) {
        // corner is NW roof corner; add posts along perimeter ring
        float stepX = w / (posts / 2f);
        float stepZ = d / (posts / 2f);
        // along front edge
        for (int i = 0; i <= posts / 2; i++) {
            Vector p = corner.add(i * stepX, 0f, 0f);
            addLine(p, p.add(0f, BALUSTRADE_POST_WIDTH * 2f, 0f), COLOR_ACCENT);
        }
        // along side edge
        for (int i = 0; i <= posts / 2; i++) {
            Vector p = corner.add(w, 0f, i * stepZ);
            addLine(p, p.add(0f, BALUSTRADE_POST_WIDTH * 2f, 0f), COLOR_ACCENT);
        }
    }

    private void makeRoofDetails(Vector roofCenter, Random rand) {
        // chimneys and vents
        int chimneys = 4;
        for (int i = 0; i < chimneys; i++) {
            float x = (rand.nextFloat() - 0.5f) * MAIN_WIDTH * 0.6f;
            float z = (rand.nextFloat() - 0.5f) * MAIN_DEPTH * 0.4f;
            Vector base = roofCenter.add(x, 0f, z);
            // small vertical box for chimney
            addLine(base, base.add(0.3f, 0.8f, 0.2f), COLOR_ROOF);
            addLine(base.add(0.3f, 0f, 0f), base.add(0.3f, 0.8f, 0f), COLOR_ROOF);
        }
    }

    private void makeWindowsAndDoors(Vector mainOrigin, float width, float height, int rows, int cols, Random rand) {
        float left = -width * 0.5f + 2.0f;
        float topY = height * 0.85f;
        float bottomY = height * 0.2f;
        float rowSpacing = (topY - bottomY) / (rows - 1f);
        float colSpacing = (width - 4.0f) / (cols - 1f);

        for (int r = 0; r < rows; r++) {
            float y = topY - r * rowSpacing;
            for (int c = 0; c < cols; c++) {
                float x = left + c * colSpacing;
                Vector tl = position.add(x, y, -MAIN_DEPTH * 0.5f - 0.02f);
                Vector tr = tl.add(WINDOW_W, 0f, 0f);
                Vector br = tl.add(WINDOW_W, -WINDOW_H, 0f);
                Vector bl = tl.add(0f, -WINDOW_H, 0f);
                // window frame
                addLine(tl, tr, COLOR_WINDOW);
                addLine(tr, br, COLOR_WINDOW);
                addLine(br, bl, COLOR_WINDOW);
                addLine(bl, tl, COLOR_WINDOW);
                // mullions: vertical + horizontal
                addLine(tl.add(WINDOW_W * 0.5f, 0f, 0f), bl.add(WINDOW_W * 0.5f, 0f, 0f), COLOR_WINDOW);
                addLine(tl.add(0f, -WINDOW_H * 0.5f, 0f), tr.add(0f, -WINDOW_H * 0.5f, 0f), COLOR_WINDOW);
            }
        }

        // central north door with small pediment
        Vector doorCenter = position.add(0f, 0f, -MAIN_DEPTH * 0.5f - 0.02f);
        Vector doorTL = doorCenter.add(-1.2f, 2.6f, 0f);
        Vector doorTR = doorTL.add(2.4f, 0f, 0f);
        Vector doorBR = doorTR.add(0f, -2.4f, 0f);
        Vector doorBL = doorTL.add(0f, -2.4f, 0f);
        addLine(doorTL, doorTR, COLOR_ACCENT);
        addLine(doorTR, doorBR, COLOR_ACCENT);
        addLine(doorBR, doorBL, COLOR_ACCENT);
        addLine(doorBL, doorTL, COLOR_ACCENT);

        // small triangular pediment above door
        Vector pA = doorTL.add(0f, 0.3f, 0f);
        Vector pB = doorTR.add(0f, 0.3f, 0f);
        Vector pTop = doorTL.add(1.2f, 0.9f, 0f);
        addLine(pA, pTop, COLOR_ACCENT);
        addLine(pTop, pB, COLOR_ACCENT);
        addLine(pA, pB, COLOR_ACCENT);
    }

    private void makeFrontFence(Vector centerLine, float width, Random rand) {
        // simple low fence posts with connectors across front lawn
        int posts = (int) (width / 1.2f);
        float left = centerLine.getX() - width * 0.5f;
        float z = centerLine.getZ();
        for (int i = 0; i <= posts; i++) {
            Vector p = position.add(left + i * (width / posts), 0.2f, z);
            addLine(p, p.add(0f, 0.8f, 0f), COLOR_ACCENT);
            if (i > 0) {
                Vector prev = position.add(left + (i - 1) * (width / posts), 0.2f, z);
                addLine(prev.add(0f, 0.6f, 0f), p.add(0f, 0.6f, 0f), COLOR_ACCENT);
            }
        }
    }

    // ---------- small utilities ----------

    private void addLine(Vector a, Vector b, int color) {
        cachedLines.add(new VertexLine(a, b, color));
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    private static float TWO_PI() {
        return (float) (2.0 * Math.PI);
    }

    @Override
    public void render(VertexBuffer buf, float tickDelta) {
        for (VertexLine l : cachedLines) {
            buf.vertex(l.start, l.color);
            buf.vertex(l.end, l.color);
        }
    }

    private static class VertexLine {
        final Vector start, end;
        final int color;
        VertexLine(Vector s, Vector e, int c) { start = s; end = e; color = c; }
    }
}
