package io.github.itzispyder.ai_generated.entity;

import io.github.itzispyder.math.Vector;
import io.github.itzispyder.math.VertexBuffer;
import io.github.itzispyder.render.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Extremely detailed, cached line-wireframe model of a huge Boeing-style jumbo.
 * - Float-only math (casts applied where needed).
 * - Deterministic: seeded by position.hashCode().
 * - Constructor generates geometry once and stores as VertexLine entries.
 * - render() replays the cached lines.
 */
public class BoeingJumbo extends Entity {

    // ---------- Size & fidelity tunables ----------
    private static final float FUSE_LENGTH = 70.0f;       // overall fuselage length (meters-ish)
    private static final float FUSE_RADIUS = 3.8f;        // main fuselage radius
    private static final int   FUSE_SECTIONS = 160;       // longitudinal resolution
    private static final int   FUSE_RING_SEGMENTS = 36;   // circular resolution per section

    private static final float WING_SPAN = 64.0f;         // wing tip-to-tip span
    private static final float WING_ROOT_CHORD = 12.0f;   // chord at root
    private static final float WING_TIP_CHORD = 3.0f;     // chord at tip
    private static final int   WING_SPAN_SEGS = 120;      // spanwise resolution
    private static final int   WING_CHORD_SEGS = 18;      // chordwise resolution
    private static final float WING_THICKNESS = 0.55f;    // thickness at root (tapers)

    private static final int   ENGINE_CIRCLE_SEGMENTS = 20;
    private static final int   FAN_BLADES = 18;
    private static final float ENGINE_RADIUS = 1.8f;
    private static final float ENGINE_LENGTH = 4.6f;

    private static final float TAIL_HEIGHT = 16.0f;
    private static final float H_STAB_SPAN = 22.0f;
    private static final int   STAB_SEGS = 40;

    private static final int   WINDOW_ROWS = 2;
    private static final float WINDOW_PITCH = 0.65f;
    private static final float WINDOW_W = 0.38f;
    private static final float WINDOW_H = 0.36f;

    // colors (ARGB)
    private static final int COLOR_BODY = 0xFFCED7E3;
    private static final int COLOR_ACCENT = 0xFF9EA8B2;
    private static final int COLOR_GLASS = 0xFF213040;
    private static final int COLOR_ENGINE = 0xFF707070;
    private static final int COLOR_FAN = 0xFF2E2E2E;
    private static final int COLOR_LIGHT = 0xFFF6F60A;
    private static final int COLOR_SHADOW = 0xFF6E7880;

    // cached geometry lines
    private final List<VertexLine> cachedLines = new ArrayList<>();

    public BoeingJumbo(Vector position) {
        super(position);
        generate();
    }

    private void generate() {
        // deterministic RNG by position so repeated runs produce same geometry
        Random rand = new Random(23L * position.hashCode() + 101L);

        // --- Fuselage rings (longitudinal) ---
        Vector[][] rings = new Vector[FUSE_SECTIONS][FUSE_RING_SEGMENTS];
        for (int i = 0; i < FUSE_SECTIONS; i++) {
            float t = (float) i / (FUSE_SECTIONS - 1f);                 // 0..1 from nose to tail
            float z = lerp(-FUSE_LENGTH * 0.5f, FUSE_LENGTH * 0.5f, t);
            float radius = FUSE_RADIUS * fuselageProfile(t);           // varying radius (hump / taper)
            float jitter = (rand.nextFloat() - 0.5f) * 0.02f * (1f - Math.abs(2f * t - 1f));

            for (int s = 0; s < FUSE_RING_SEGMENTS; s++) {
                float ang = TWO_PI() * s / FUSE_RING_SEGMENTS;
                float x = cosf(ang) * (radius + jitter);
                float y = sinf(ang) * (radius + jitter) * 0.98f; // slight flattening
                rings[i][s] = position.add(x, y, z);
            }
        }

        // Connect ring edges and longitudinal connectors
        for (int i = 0; i < FUSE_SECTIONS; i++) {
            for (int s = 0; s < FUSE_RING_SEGMENTS; s++) {
                Vector a = rings[i][s];
                Vector b = rings[i][(s + 1) % FUSE_RING_SEGMENTS];
                addLine(a, b, COLOR_BODY);
                if (i < FUSE_SECTIONS - 1) {
                    Vector c = rings[i + 1][s];
                    addLine(a, c, COLOR_BODY);
                }
            }
        }

        // --- Upper-deck "hump" detail (distinct rings for visually different surface) ---
        makeHumpDetail(rings, rand);

        // --- Cockpit and nose details ---
        makeCockpitAndNose(rings, rand);

        // --- Windows & doors along fuselage sides ---
        makeWindowsAndDoorsAlongFuselage(rings, rand);

        // --- Wings: generate left and right wings (sweep, taper, flaps, winglets) ---
        float halfSpan = WING_SPAN * 0.5f;
        float wingRootZ = lerp(-FUSE_LENGTH * 0.05f, FUSE_LENGTH * 0.05f, 0.5f); // slightly forward of center
        float wingRootY = 0.0f; // centerline Y

        makeWing(-1, wingRootZ, wingRootY, halfSpan, rand, rings); // left
        makeWing(1, wingRootZ, wingRootY, halfSpan, rand, rings);  // right

        // --- Engines: four pods under wings (two per wing) ---
        // place engines at fractions along half-span
        float[] engineSpanFracs = {0.27f, 0.6f}; // positions along half-span (fraction)
        for (int side = -1; side <= 1; side += 2) {
            for (float frac : engineSpanFracs) {
                float span = halfSpan * frac;
                Vector engineCenter = position.add(side * span, -0.8f, wingRootZ + sweepAtSpan(frac) + 0.8f);
                makeEngine(engineCenter, rand, side == -1);
            }
        }

        // --- Tail (vertical stabilizer + horizontal stabilizers) ---
        Vector tailRoot = position.add(0f, 0f, FUSE_LENGTH * 0.5f - 2.5f);
        makeVerticalStab(tailRoot, rand);
        makeHorizontalStabs(tailRoot, rand);

        // --- Landing gear bays (retracted-looking lines) ---
        makeLandingGearBays(rings, rand);

        // --- Lights & exterior smalls (antennas, static ports, paint highlights) ---
        makeExteriorAccents(rings, rand);
    }

    // -------------------- Detailed builders --------------------

    private void makeHumpDetail(Vector[][] rings, Random rand) {
        // highlight upper-deck hump area (forward section) with extra rings and seam lines
        int start = (int) (FUSE_SECTIONS * 0.08f);
        int end   = (int) (FUSE_SECTIONS * 0.18f);
        for (int i = start; i <= end; i++) {
            for (int s = 0; s < FUSE_RING_SEGMENTS; s++) {
                Vector a = rings[i][s];
                Vector b = rings[i][(s + 1) % FUSE_RING_SEGMENTS];
                // ridge seam near roof (upper quarter of ring)
                if (a.getY() > position.getY() + FUSE_RADIUS * 0.6f && b.getY() > position.getY() + FUSE_RADIUS * 0.6f) {
                    addLine(a, b, COLOR_ACCENT);
                }
            }
        }

        // small window row for upper deck (if present)
        float upperDeckZ = lerp(-FUSE_LENGTH * 0.5f + 6f, -FUSE_LENGTH * 0.5f + 10f, 0.5f);
        int windows = 12;
        for (int i = 0; i < windows; i++) {
            float tz = upperDeckZ + i * 0.22f;
            Vector wpos = position.add(-1.2f + i * 0.22f, FUSE_RADIUS * 0.55f, tz);
            // tiny window rectangles (two lines for top/bottom)
            addLine(wpos, wpos.add(0.18f, 0f, 0f), COLOR_GLASS);
            addLine(wpos.add(0f, -0.15f, 0f), wpos.add(0.18f, -0.15f, 0f), COLOR_GLASS);
        }
    }

    private void makeCockpitAndNose(Vector[][] rings, Random rand) {
        // cockpit windshield: triangulated panes at nose front
        float noseFrontZ = -FUSE_LENGTH * 0.5f;
        float cw = 2.2f; // cockpit width
        float ch = 0.9f; // cockpit height
        Vector cTop = position.add(0f, 1.2f, noseFrontZ + 6.0f);
        Vector left = position.add(-cw * 0.5f, 0.45f, noseFrontZ + 6.6f);
        Vector right= position.add(cw * 0.5f, 0.45f, noseFrontZ + 6.6f);

        // windshield panes fan
        addLine(left, cTop, COLOR_GLASS);
        addLine(right, cTop, COLOR_GLASS);
        addLine(left, right, COLOR_GLASS);

        // nose radome seam
        addLine(position.add(-1.0f, 0.2f, noseFrontZ + 2.2f), position.add(1.0f, 0.2f, noseFrontZ + 2.2f), COLOR_ACCENT);

        // pitot/static/antennae small details along nose
        addLine(position.add(0.6f, 0.12f, noseFrontZ + 3.1f), position.add(0.9f, 0.12f, noseFrontZ + 3.6f), COLOR_ACCENT);
        addLine(position.add(-0.6f, 0.12f, noseFrontZ + 3.1f), position.add(-0.9f, 0.12f, noseFrontZ + 3.6f), COLOR_ACCENT);
    }

    private void makeWindowsAndDoorsAlongFuselage(Vector[][] rings, Random rand) {
        // continuous passenger window rows along both sides
        int passengerWindows = (int) (FUSE_LENGTH / WINDOW_PITCH);
        float zStart = -FUSE_LENGTH * 0.45f;
        for (int i = 0; i < passengerWindows; i++) {
            float z = zStart + i * WINDOW_PITCH;
            // top/bottom of window rectangle on each side
            float sideY = 0.2f;
            Vector leftWindow = position.add(-FUSE_RADIUS - 0.02f, sideY, z);
            Vector rightWindow = position.add(FUSE_RADIUS + 0.02f, sideY, z);
            // draw small rectangles outward from skin
            addLine(leftWindow, leftWindow.add(WINDOW_W, 0f, 0f), COLOR_GLASS);
            addLine(leftWindow.add(0f, -WINDOW_H, 0f), leftWindow.add(WINDOW_W, -WINDOW_H, 0f), COLOR_GLASS);

            addLine(rightWindow, rightWindow.add(-WINDOW_W, 0f, 0f), COLOR_GLASS);
            addLine(rightWindow.add(0f, -WINDOW_H, 0f), rightWindow.add(-WINDOW_W, -WINDOW_H, 0f), COLOR_GLASS);
        }

        // passenger doors: larger rectangles at standard positions
        float[] doorPositions = {-22f, -8f, 8f, 22f}; // approximate z positions
        for (float dz : doorPositions) {
            Vector dCenterLeft = position.add(-FUSE_RADIUS - 0.01f, 0.3f, dz);
            Vector tl = dCenterLeft.add(0f, 1.8f, 0f);
            Vector tr = tl.add(1.4f, 0f, 0f);
            Vector br = tr.add(0f, -1.6f, 0f);
            Vector bl = tl.add(0f, -1.6f, 0f);
            addLine(tl, tr, COLOR_ACCENT);
            addLine(tr, br, COLOR_ACCENT);
            addLine(br, bl, COLOR_ACCENT);
            addLine(bl, tl, COLOR_ACCENT);

            // mirrored right side
            Vector dCenterRight = position.add(FUSE_RADIUS + 0.01f, 0.3f, dz);
            Vector rtl = dCenterRight.add(0f, 1.8f, 0f);
            Vector rtr = rtl.add(-1.4f, 0f, 0f);
            Vector rbr = rtr.add(0f, -1.6f, 0f);
            Vector rbl = rtl.add(0f, -1.6f, 0f);
            addLine(rtl, rtr, COLOR_ACCENT);
            addLine(rtr, rbr, COLOR_ACCENT);
            addLine(rbr, rbl, COLOR_ACCENT);
            addLine(rbl, rtl, COLOR_ACCENT);
        }
    }

    private void makeWing(int sideSign, float wingRootZ, float wingRootY, float halfSpan, Random rand, Vector[][] fuselageRings) {
        // produce a wing surface as a grid of points across span and chord, then connect mesh lines
        int spanSteps = WING_SPAN_SEGS / 2;
        int chordSteps = WING_CHORD_SEGS;

        Vector[][] wingGrid = new Vector[spanSteps + 1][chordSteps];

        for (int si = 0; si <= spanSteps; si++) {
            float frac = (float) si / spanSteps;
            float span = frac * halfSpan;
            float absSpan = span;
            // local chord length taper
            float chord = lerp(WING_ROOT_CHORD, WING_TIP_CHORD, frac);
            // sweep rearwards by sweepAtSpan(frac)
            float sweep = sweepAtSpan(frac);
            // dihedral (slight upward angle)
            float dihedral = 0.04f * absSpan; // small upward offset as span increases
            float zBase = wingRootZ + sweep;

            for (int ci = 0; ci < chordSteps; ci++) {
                float cfrac = (float) ci / (chordSteps - 1f);
                float leadOffset = 0f; // leading edge coordinate offset along chord
                float localX = sideSign * span; // lateral
                float localZ = zBase + cfrac * chord; // chord moves rearwards
                float localY = wingRootY - (WING_THICKNESS * (1f - cfrac) * 0.35f) + dihedral;
                // small washout: rotate tip slightly (approx)
                float wash = 0.0f;
                wingGrid[si][ci] = position.add(localX, localY, localZ + wash);
            }
        }

        // mesh lines across chord and span
        for (int si = 0; si <= spanSteps; si++) {
            for (int ci = 0; ci < chordSteps; ci++) {
                Vector a = wingGrid[si][ci];
                if (ci < chordSteps - 1) addLine(a, wingGrid[si][ci + 1], COLOR_BODY);
                if (si < spanSteps) addLine(a, wingGrid[si + 1][ci], COLOR_BODY);
                // cross-bracing to suggest thickness & ribs
                if (ci + 2 < chordSteps) addLine(a, wingGrid[si][(ci + 2) % chordSteps], COLOR_ACCENT);
            }
        }

        // trailing-edge flaps & ailerons: produce separated lines toward tip
        int flapStart = (int) (chordSteps * 0.6f);
        for (int si = (int) (spanSteps * 0.35f); si < spanSteps; si++) {
            Vector a = wingGrid[si][flapStart];
            Vector b = wingGrid[si][chordSteps - 1];
            addLine(a, b, COLOR_ACCENT);
        }

        // winglet at tip
        Vector wingTip = wingGrid[spanSteps][chordSteps / 2];
        Vector wingletTop = wingTip.add(sideSign * 0.6f, 1.2f, 0.6f);
        addLine(wingTip, wingletTop, COLOR_ACCENT);
        addLine(wingletTop, wingTip.add(sideSign * 0.25f, 0.9f, -0.2f), COLOR_ACCENT);
    }

    private void makeEngine(Vector center, Random rand, boolean leftSide) {
        // engine cylinder (axis roughly along Z, centered at `center`)
        int rings = 10;
        for (int r = 0; r <= rings; r++) {
            float t = (float) r / rings;
            float cx = center.getX();
            float cy = center.getY();
            float cz = center.getZ() + (t - 0.5f) * ENGINE_LENGTH;
            float localR = ENGINE_RADIUS * (1f - 0.12f * t); // slight taper aft
            for (int s = 0; s < ENGINE_CIRCLE_SEGMENTS; s++) {
                float ang = TWO_PI() * s / ENGINE_CIRCLE_SEGMENTS;
                float y = cy + cosf(ang) * localR;
                float z = cz + sinf(ang) * localR * 0.08f;
                float x = cx;
                Vector p = position.add(x, y, z);
                Vector q = position.add(x, cy + cosf(TWO_PI() * (s + 1) / ENGINE_CIRCLE_SEGMENTS) * localR, cz + sinf(TWO_PI() * (s + 1) / ENGINE_CIRCLE_SEGMENTS) * localR * 0.08f);
                addLine(p, q, COLOR_ENGINE);
                if (r > 0) {
                    // connect longitudinally
                    Vector prev = position.add(x, cy + cosf(ang) * (ENGINE_RADIUS * (1f - 0.12f * (r - 1f) / rings)),
                                               cz - (1f / rings) * ENGINE_LENGTH + sinf(ang) * (ENGINE_RADIUS * 0.08f));
                    addLine(prev, p, COLOR_ENGINE);
                }
            }
        }

        // fan face (front): spokes
        Vector fanCenter = center.add(0f, 0f, -ENGINE_LENGTH * 0.45f);
        for (int f = 0; f < FAN_BLADES; f++) {
            float a = TWO_PI() * f / FAN_BLADES + (rand.nextFloat() - 0.5f) * 0.02f;
            Vector tip = fanCenter.add(0f, cosf(a) * (ENGINE_RADIUS * 0.9f), sinf(a) * (ENGINE_RADIUS * 0.9f));
            addLine(fanCenter, tip, COLOR_FAN);
        }

        // pylon (connect engine to wing): simple triangular strut
        Vector pylonTop = center.add(0f, 0.5f + rand.nextFloat() * 0.1f, 0.8f);
        addLine(pylonTop, fanCenter.add(0f, 0.0f, 0.9f), COLOR_ENGINE);
        addLine(pylonTop.add(-0.06f, -0.08f, 0f), pylonTop.add(0.06f, -0.08f, 0f), COLOR_ENGINE);
    }

    private void makeVerticalStab(Vector tailRoot, Random rand) {
        // vertical stabilizer (simple mesh)
        int segs = STAB_SEGS;
        float height = TAIL_HEIGHT;
        float baseZ = tailRoot.getZ();
        float baseY = 0.8f;
        for (int i = 0; i <= segs; i++) {
            float t = (float) i / segs;
            float h = t * height;
            float width = 4.0f * (1f - t * 0.9f);
            Vector left = position.add(-width * 0.5f, baseY + h, baseZ);
            Vector right= position.add(width * 0.5f, baseY + h, baseZ - 0.2f * t);
            addLine(left, right, COLOR_BODY);
            if (i > 0) {
                // connect previous cross-section
                Vector prevLeft = position.add(-4.0f * 0.5f * (1f - (float) (i - 1) / segs), baseY + (i - 1f) / segs * height, baseZ);
                addLine(prevLeft, left, COLOR_BODY);
            }
        }
        // rudder seam
        addLine(position.add(0f, baseY + height * 0.2f, baseZ - 0.05f), position.add(0f, baseY + height * 0.9f, baseZ - 0.05f), COLOR_ACCENT);
    }

    private void makeHorizontalStabs(Vector tailRoot, Random rand) {
        // left and right horizontal stabilizers
        float span = H_STAB_SPAN * 0.5f;
        int chord = 10;
        for (int side = -1; side <= 1; side += 2) {
            for (int i = 0; i < chord; i++) {
                float t = (float) i / (chord - 1f);
                float x = side * (t * span);
                float z = tailRoot.getZ() + 1.2f + 0.8f * (t - 0.5f);
                float y = 0.9f + 0.05f * (1f - t);
                Vector a = position.add(side * 0.0f + x, y, z);
                Vector b = position.add(side * 0.0f + x, y + 0.02f, z + 0.4f);
                addLine(a, b, COLOR_BODY);
                if (i < chord - 1) {
                    Vector an = position.add(side * ( (i+1f)/ (chord - 1f) * span ), y, tailRoot.getZ() + 1.2f + 0.8f * ((i+1f)/(chord - 1f) - 0.5f));
                    addLine(a, an, COLOR_BODY);
                }
            }
            // elevator seam
            addLine(position.add(side * (span * 0.6f), 0.92f, tailRoot.getZ() + 1.0f), position.add(side * (span * 0.95f), 0.92f, tailRoot.getZ() + 1.6f), COLOR_ACCENT);
        }
    }

    private void makeLandingGearBays(Vector[][] rings, Random rand) {
        // hint at landing gear doors and struts on belly
        float zNoseGear = -FUSE_LENGTH * 0.35f;
        float zMainGear1 = -FUSE_LENGTH * 0.05f;
        float zMainGear2 = FUSE_LENGTH * 0.12f;

        // nose gear bay rectangle
        Vector ngTL = position.add(-0.6f, -FUSE_RADIUS - 0.04f, zNoseGear);
        Vector ngTR = ngTL.add(1.2f, 0f, 0f);
        Vector ngBR = ngTR.add(0f, -0.2f, 0f);
        Vector ngBL = ngTL.add(0f, -0.2f, 0f);
        addLine(ngTL, ngTR, COLOR_ACCENT);
        addLine(ngTR, ngBR, COLOR_ACCENT);
        addLine(ngBR, ngBL, COLOR_ACCENT);
        addLine(ngBL, ngTL, COLOR_ACCENT);

        // two main gear bays (left/right) near zMainGear1 and zMainGear2
        for (float z : new float[] {zMainGear1, zMainGear2}) {
            Vector leftBay = position.add(-2.0f, -FUSE_RADIUS - 0.05f, z);
            Vector rightBay= position.add(2.0f, -FUSE_RADIUS - 0.05f, z);
            addLine(leftBay, leftBay.add(1.6f, 0f, 0f), COLOR_ACCENT);
            addLine(rightBay, rightBay.add(-1.6f, 0f, 0f), COLOR_ACCENT);
            // strut hint
            addLine(leftBay.add(0.8f, 0f, -0.3f), leftBay.add(0.8f, -1.4f, -0.6f), COLOR_ACCENT);
            addLine(rightBay.add(-0.8f, 0f, -0.3f), rightBay.add(-0.8f, -1.4f, -0.6f), COLOR_ACCENT);
        }
    }

    private void makeExteriorAccents(Vector[][] rings, Random rand) {
        // nav lights at wing tips and tail
        float halfSpan = WING_SPAN * 0.5f;
        Vector leftTip = position.add(-halfSpan, 0.02f, 0f);
        Vector rightTip= position.add(halfSpan, 0.02f, 0f);
        addLine(leftTip, leftTip.add(0.0f, 0.0f, 0.4f), COLOR_LIGHT);  // nav
        addLine(rightTip, rightTip.add(0.0f, 0.0f, 0.4f), COLOR_LIGHT);
        addLine(position.add(0f, TAIL_HEIGHT * 0.95f, FUSE_LENGTH * 0.5f), position.add(0f, TAIL_HEIGHT * 0.95f, FUSE_LENGTH * 0.6f), COLOR_LIGHT);

        // small panel seams across fuselage
        int seams = 28;
        for (int i = 0; i < seams; i++) {
            float z = lerp(-FUSE_LENGTH * 0.45f, FUSE_LENGTH * 0.45f, (float) i / (seams - 1f));
            addLine(position.add(-FUSE_RADIUS, 0.0f, z), position.add(FUSE_RADIUS, 0.0f, z), COLOR_SHADOW);
        }

        // tail registration / accent stripe
        addLine(position.add(0f, TAIL_HEIGHT * 0.6f, FUSE_LENGTH * 0.52f), position.add(0f, TAIL_HEIGHT * 0.6f, FUSE_LENGTH * 0.45f), COLOR_ACCENT);
    }

    // -------------------- Utility functions --------------------

    private void addLine(Vector a, Vector b, int color) {
        cachedLines.add(new VertexLine(a, b, color));
    }

    private static float fuselageProfile(float t) {
        // t=0..1 from nose to tail.
        // nose taper: first 10% small, hump around 12..18% (upper deck), cylindrical mid-section, tapered tail.
        if (t < 0.08f) {
            // nose taper
            return 0.35f + 0.65f * (t / 0.08f);
        } else if (t < 0.18f) {
            // upper deck hump
            float u = (t - 0.08f) / 0.10f;
            return 1.0f + 0.18f * (1f - Math.abs(2f * u - 1f));
        } else if (t < 0.78f) {
            // cylindrical midfuselage
            return 1.0f;
        } else {
            // tail taper
            float u = (t - 0.78f) / 0.22f;
            return 1.0f * (1f - 0.85f * u);
        }
    }

    private static float sweepAtSpan(float frac) {
        // returns longitudinal sweep offset at given span fraction (0..1)
        // modest sweep (negative moves rearwards)
        return -1.6f * frac * frac;
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    private static float TWO_PI() {
        return (float) (2.0 * Math.PI);
    }

    private static float sinf(float v) {
        return (float) Math.sin(v);
    }

    private static float cosf(float v) {
        return (float) Math.cos(v);
    }

    @Override
    public void render(VertexBuffer buf, float tickDelta) {
        for (VertexLine l : cachedLines) {
            buf.vertex(l.start, l.color);
            buf.vertex(l.end, l.color);
        }
    }

    // simple container
    private static class VertexLine {
        final Vector start, end;
        final int color;
        VertexLine(Vector s, Vector e, int c) { start = s; end = e; color = c; }
    }
}
