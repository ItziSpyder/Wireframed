package io.github.itzispyder.ai_generated.entity;

import io.github.itzispyder.math.Vector;
import io.github.itzispyder.math.VertexBuffer;
import io.github.itzispyder.render.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Procedural, high-detail Lamborghini-like wireframe.
 * - All math uses floats (Math.* results cast to float).
 * - Geometry is generated once (constructor) and cached in cachedLines.
 * - render() replays cached lines (fast per-frame).
 */
public class Lambo extends Entity {

    // ---------- Tunables: dimensions & fidelity ----------
    private static final float LENGTH = 4.60f;         // meters (approx)
    private static final float WIDTH  = 2.02f;
    private static final float HEIGHT = 1.14f;

    private static final float WHEEL_RADIUS = 0.38f;
    private static final float WHEEL_WIDTH  = 0.30f;
    private static final float GROUND_CLEARANCE = 0.09f;

    private static final int BODY_SECTIONS = 40;       // longitudinal resolution
    private static final int BODY_RING_SEGMENTS = 36;  // perimeter resolution per section

    private static final int WHEEL_SEGMENTS = 28;      // rim circle resolution
    private static final int RIM_SPOKES = 8;

    // ---------- Colors (ARGB) ----------
    private static final int COLOR_BODY      = 0xFF0D6EFD; // blue paint (changeable)
    private static final int COLOR_HIGHLIGHT = 0xFF7FB3FF; // lighter highlight
    private static final int COLOR_GLASS     = 0xAA444F63; // semi-dark tinted
    private static final int COLOR_TIRE      = 0xFF0E0E0E;
    private static final int COLOR_RIM       = 0xFFD9D9D9;
    private static final int COLOR_LIGHTS    = 0xFFF6F60A; // headlight warm
    private static final int COLOR_TAIL      = 0xFFDA1C16;
    private static final int COLOR_SHADOW    = 0xFF2E2E2E;
    private static final int COLOR_ACCENT    = 0xFFAAAAAA; // vents / grills

    // cached geometry
    private final List<VertexLine> cachedLines = new ArrayList<>();

    public Lambo(Vector position) {
        super(position);
        generate();
    }

    private void generate() {
        // deterministic randomness per position
        Random rand = new Random(13L * position.hashCode() + 7L);

        // front/rear extents along Z (forward/back)
        final float frontZ = -LENGTH * 0.5f;
        final float rearZ  =  LENGTH * 0.5f;

        // Precompute body cross-sections (rings) across LENGTH
        Vector[][] rings = new Vector[BODY_SECTIONS][BODY_RING_SEGMENTS];

        for (int i = 0; i < BODY_SECTIONS; i++) {
            float t = (float) i / (BODY_SECTIONS - 1f); // 0..1 from front to rear
            float z = lerp(frontZ, rearZ, t);

            // shape control: width & height profile functions (Lamborghini low-slung supercar)
            float halfWidth = 0.5f * WIDTH * bodyWidthProfile(t);
            float halfHeight = 0.5f * HEIGHT * bodyHeightProfile(t);

            // create slight panel noise for realism
            float noise = (rand.nextFloat() - 0.5f) * 0.009f;

            for (int s = 0; s < BODY_RING_SEGMENTS; s++) {
                float ang = TWO_PI() * s / BODY_RING_SEGMENTS;
                // elliptical ring adapted by angle to emphasize hood/roof/side geometry
                float cosA = cosf(ang);
                float sinA = sinf(ang);

                // bias heights so bottom sits near -GROUND_CLEARANCE
                float y = sinA * halfHeight + (-GROUND_CLEARANCE);

                // x (side) scaled by halfWidth but compress bottom/back to emulate diffuser
                float x = cosA * halfWidth * (1f + sideBulgeFactor(t, cosA));

                // push some vertices inward for vents / wheel arches using simple distance checks
                Vector pt = position.add(x + (noise * cosA), y + (noise * sinA), z);
                rings[i][s] = pt;
            }
        }

        // connect rings -> body mesh lines
        for (int i = 0; i < BODY_SECTIONS; i++) {
            for (int s = 0; s < BODY_RING_SEGMENTS; s++) {
                Vector a = rings[i][s];
                Vector b = rings[i][(s + 1) % BODY_RING_SEGMENTS];
                addLine(a, b, COLOR_BODY);

                if (i < BODY_SECTIONS - 1) {
                    Vector c = rings[i + 1][s];
                    addLine(a, c, COLOR_BODY);
                }
            }
        }

        // windows / windshield / roof glass
        makeGlassAndCabin(rings, rand);

        // hood & front intake seams & headlights
        makeFrontDetails(frontZ, rings, rand);

        // doors seam line (scissor-door seam near roofline)
        makeDoorSeam(rings);

        // rear details: spoiler, engine vents, taillights, diffuser
        makeRearDetails(rearZ, rings, rand);

        // wheels (4): compute axle positions and add wheel meshes
        float axleOffsetZFront = lerp(frontZ + 0.8f, frontZ + 1.0f, 0.5f);
        float axleOffsetZRear  = lerp(rearZ - 1.1f, rearZ - 0.9f, 0.5f);
        float wheelCenterY = -GROUND_CLEARANCE + WHEEL_RADIUS * 0.6f; // center slightly above ground due to axle

        // x offsets for left/right
        float sideX = 0.5f * WIDTH * 0.92f;

        // front-left
        makeWheel(position.add(-sideX, wheelCenterY, axleOffsetZFront), WHEEL_RADIUS, WHEEL_WIDTH, rand, true);
        // front-right
        makeWheel(position.add(sideX, wheelCenterY, axleOffsetZFront), WHEEL_RADIUS, WHEEL_WIDTH, rand, false);
        // rear-left
        makeWheel(position.add(-sideX, wheelCenterY, axleOffsetZRear), WHEEL_RADIUS * 1.02f, WHEEL_WIDTH * 1.05f, rand, true);
        // rear-right
        makeWheel(position.add(sideX, wheelCenterY, axleOffsetZRear), WHEEL_RADIUS * 1.02f, WHEEL_WIDTH * 1.05f, rand, false);

        // mirrors
        makeMirrors(rings, rand);

        // interior: seats & steering wheel visible through glass
        makeInterior(rings, rand);

        // accent lines: panel seams, vents and grills
        makeVentsAndGrills(rings, rand);

        // exhausts
        makeExhausts(rearZ, rand);

        // highlight strokes for glossy paint (add a few long thin highlight lines)
        addPaintHighlights(rings, rand);
    }

    // -------------------- Helpers & detail builders --------------------

    private void addLine(Vector a, Vector b, int color) {
        cachedLines.add(new VertexLine(a, b, color));
    }

    private void makeGlassAndCabin(Vector[][] rings, Random rand) {
        // Approximate windshield + side windows by finding cabin section indices
        int frontCabinIdx = (int) (BODY_SECTIONS * 0.26f);
        int rearCabinIdx = (int) (BODY_SECTIONS * 0.52f);

        // roof center line (midpoints across top)
        for (int i = frontCabinIdx; i <= rearCabinIdx; i++) {
            // find top-most points by scanning ring for largest Y
            int topIdx = 0;
            float bestY = Float.NEGATIVE_INFINITY;
            for (int s = 0; s < BODY_RING_SEGMENTS; s++) {
                float y = rings[i][s].getY();
                if (y > bestY) { bestY = y; topIdx = s; }
            }
            Vector top = rings[i][topIdx];
            // connect a simulated roof ridge (slightly forward/backwards offset) to make cabin silhouette
            addLine(top, top.add(0f, 0.01f, 0f), COLOR_GLASS);
        }

        // windshield polygon: use four corner approximations
        Vector wl1 = rings[frontCabinIdx].length > 0 ? rings[frontCabinIdx][getTopIndex(rings[frontCabinIdx])] : position.add(-0.6f, 0.45f, -0.3f);
        Vector wl2 = rings[frontCabinIdx + 2][getTopIndex(rings[frontCabinIdx + 2])];
        Vector wr1 = rings[rearCabinIdx - 2][getTopIndex(rings[rearCabinIdx - 2])];
        Vector wr2 = rings[rearCabinIdx][getTopIndex(rings[rearCabinIdx])];

        // make a windshield polygon (connect a few edges)
        addLine(wl1, wl2, COLOR_GLASS);
        addLine(wl2, wr2, COLOR_GLASS);
        addLine(wr2, wr1, COLOR_GLASS);

        // side windows: simple rectangular bands along cabin sides
        int sideSamples = 6;
        for (int side = -1; side <= 1; side += 2) {
            for (int k = 0; k < sideSamples; k++) {
                float tt = (float) k / (sideSamples - 1f);
                int idx = frontCabinIdx + (int) (tt * (rearCabinIdx - frontCabinIdx));
                // find top and side point near side sign
                int sideIdx = side > 0 ? getRightIndex(rings[idx]) : getLeftIndex(rings[idx]);
                int topIdx = getTopIndex(rings[idx]);
                addLine(rings[idx][sideIdx], rings[idx][topIdx], COLOR_GLASS);
                if (idx < rings.length - 1) addLine(rings[idx][sideIdx], rings[idx + 1][sideIdx], COLOR_GLASS);
            }
        }

        // roofline seam
        for (int i = frontCabinIdx; i <= rearCabinIdx - 1; i++) {
            int top1 = getTopIndex(rings[i]);
            int top2 = getTopIndex(rings[i + 1]);
            addLine(rings[i][top1], rings[i + 1][top2], COLOR_GLASS);
        }
    }

    private void makeFrontDetails(float frontZ, Vector[][] rings, Random rand) {
        // headlights: small triangular arrays at the front corners
        float lightInset = 0.18f;
        float lightY = -GROUND_CLEARANCE + 0.25f;
        float lightZ = frontZ + 0.18f;
        float halfW = 0.5f * WIDTH * 0.6f;

        Vector hlL = position.add(-halfW * 0.9f, lightY, lightZ);
        Vector hlR = position.add(halfW * 0.9f, lightY, lightZ);

        // triangular detail
        addLine(hlL, hlL.add(0.14f, 0.04f, 0.06f), COLOR_LIGHTS);
        addLine(hlL, hlL.add(0.06f, 0.02f, 0.03f), COLOR_LIGHTS);
        addLine(hlR, hlR.add(-0.14f, 0.04f, 0.06f), COLOR_LIGHTS);
        addLine(hlR, hlR.add(-0.06f, 0.02f, 0.03f), COLOR_LIGHTS);

        // hood central intake seams (three slats)
        float hoodZ = frontZ + 0.32f;
        for (int i = 0; i < 3; i++) {
            float offset = -0.04f + i * 0.04f;
            Vector a = position.add(-0.32f, -GROUND_CLEARANCE + 0.12f + i * 0.01f, hoodZ + offset);
            Vector b = position.add(0.32f, -GROUND_CLEARANCE + 0.12f + i * 0.01f, hoodZ + offset);
            addLine(a, b, COLOR_ACCENT);
        }

        // front splitter edge
        Vector splitterL = position.add(-WIDTH * 0.48f, -GROUND_CLEARANCE - 0.02f, frontZ + 0.08f);
        Vector splitterR = position.add(WIDTH * 0.48f, -GROUND_CLEARANCE - 0.02f, frontZ + 0.08f);
        addLine(splitterL, splitterR, COLOR_SHADOW);
    }

    private void makeDoorSeam(Vector[][] rings) {
        // rough seam from roof down along mid-side to mid-doors
        int cabinStart = (int) (BODY_SECTIONS * 0.28f);
        int cabinEnd   = (int) (BODY_SECTIONS * 0.5f);
        for (int i = cabinStart; i <= cabinEnd; i++) {
            int rightIdx = getRightIndex(rings[i]);
            Vector seamTop = rings[i][rightIdx];
            // project seam slightly upward to suggest scissor hinge
            addLine(seamTop, seamTop.add(0f, 0.08f, 0f), COLOR_ACCENT);
        }
    }

    private void makeRearDetails(float rearZ, Vector[][] rings, Random rand) {
        // spoiler: two vertical supports and a top blade
        float bladeY = -GROUND_CLEARANCE + 0.38f;
        Vector sL = position.add(-0.6f, bladeY - 0.06f, rearZ - 0.12f);
        Vector sR = position.add(0.6f, bladeY - 0.06f, rearZ - 0.12f);
        addLine(sL, sL.add(0f, 0.12f, 0f), COLOR_ACCENT);
        addLine(sR, sR.add(0f, 0.12f, 0f), COLOR_ACCENT);
        addLine(sL.add(0f, 0.12f, 0f), sR.add(0f, 0.12f, 0f), COLOR_ACCENT);

        // taillights: thin slits
        Vector tlL = position.add(-WIDTH * 0.32f, -GROUND_CLEARANCE + 0.18f, rearZ - 0.06f);
        Vector tlR = position.add(WIDTH * 0.32f, -GROUND_CLEARANCE + 0.18f, rearZ - 0.06f);
        addLine(tlL, tlL.add(0.18f, 0f, 0.02f), COLOR_TAIL);
        addLine(tlR, tlR.add(-0.18f, 0f, 0.02f), COLOR_TAIL);

        // rear diffuser slats
        for (int i = 0; i < 4; i++) {
            float offset = -0.18f + i * 0.12f;
            Vector a = position.add(-0.28f, -GROUND_CLEARANCE - 0.02f, rearZ - 0.02f + offset);
            Vector b = position.add(0.28f, -GROUND_CLEARANCE - 0.02f, rearZ - 0.02f + offset);
            addLine(a, b, COLOR_SHADOW);
        }

        // engine vents grid
        Vector ventCenter = position.add(0f, -GROUND_CLEARANCE + 0.26f, rearZ - 0.24f);
        for (int x = -2; x <= 2; x++) {
            for (int y = -1; y <= 1; y++) {
                Vector a = ventCenter.add(x * 0.08f, y * 0.06f, x * 0.02f);
                Vector b = a.add(0.06f, 0f, 0.06f);
                addLine(a, b, COLOR_ACCENT);
            }
        }
    }

    private void makeWheel(Vector center, float radius, float width, Random rand, boolean isLeft) {
        // Wheel lies in vertical Y-Z plane, with center.x fixed (left or right)
        // Outer tire ring
        for (int i = 0; i < WHEEL_SEGMENTS; i++) {
            float a1 = TWO_PI() * i / WHEEL_SEGMENTS;
            float a2 = TWO_PI() * (i + 1) / WHEEL_SEGMENTS;
            Vector p1 = center.add(0f, cosf(a1) * radius, sinf(a1) * radius);
            Vector p2 = center.add(0f, cosf(a2) * radius, sinf(a2) * radius);
            addLine(p1, p2, COLOR_TIRE);
        }

        // rim inner circle (smaller)
        float rimR = radius * 0.55f;
        for (int i = 0; i < WHEEL_SEGMENTS; i++) {
            float a1 = TWO_PI() * i / WHEEL_SEGMENTS;
            float a2 = TWO_PI() * (i + 1) / WHEEL_SEGMENTS;
            Vector p1 = center.add(0f, cosf(a1) * rimR, sinf(a1) * rimR);
            Vector p2 = center.add(0f, cosf(a2) * rimR, sinf(a2) * rimR);
            addLine(p1, p2, COLOR_RIM);
        }

        // spokes
        for (int s = 0; s < RIM_SPOKES; s++) {
            float ang = TWO_PI() * s / RIM_SPOKES + (rand.nextFloat() - 0.5f) * 0.04f;
            Vector rimPt = center.add(0f, cosf(ang) * rimR, sinf(ang) * rimR);
            addLine(center, rimPt, COLOR_RIM);
            // small cross-brace mid spokes
            Vector mid = center.add(0f, cosf(ang + 0.1f) * rimR * 0.6f, sinf(ang + 0.1f) * rimR * 0.6f);
            addLine(rimPt, mid, COLOR_RIM);
        }

        // axle connector lines to body - quick visual
        addLine(center, center.add(0f, 0f, isLeft ? 0.10f : -0.10f), COLOR_SHADOW);
    }

    private void makeMirrors(Vector[][] rings, Random rand) {
        // place small mirrors roughly at cabin front sections
        int mirrorIdx = (int) (BODY_SECTIONS * 0.34f);
        Vector leftMount = rings[mirrorIdx][getLeftIndex(rings[mirrorIdx])];
        Vector rightMount = rings[mirrorIdx][getRightIndex(rings[mirrorIdx])];

        Vector leftMirror = leftMount.add(-0.14f, 0.05f, 0.02f);
        Vector rightMirror = rightMount.add(0.14f, 0.05f, 0.02f);

        addLine(leftMount, leftMirror, COLOR_ACCENT);
        addLine(rightMount, rightMirror, COLOR_ACCENT);

        // mirror glass
        addLine(leftMirror, leftMirror.add(-0.06f, 0f, 0.02f), COLOR_GLASS);
        addLine(rightMirror, rightMirror.add(0.06f, 0f, 0.02f), COLOR_GLASS);
    }

    private void makeInterior(Vector[][] rings, Random rand) {
        // seats simplified as two sculpted cuboids inside cabin
        int cabinStart = (int) (BODY_SECTIONS * 0.30f);
        int cabinEnd   = (int) (BODY_SECTIONS * 0.48f);
        int seatIndex  = cabinStart + (cabinEnd - cabinStart) / 3;

        // left seat
        Vector seatL = position.add(-0.28f, -GROUND_CLEARANCE + 0.12f, lerp(-LENGTH * 0.5f + 0.9f, LENGTH * 0.5f - 1.4f, 0.34f));
        makeSeat(seatL, rand, -1);

        // right seat
        Vector seatR = position.add(0.28f, -GROUND_CLEARANCE + 0.12f, lerp(-LENGTH * 0.5f + 0.9f, LENGTH * 0.5f - 1.4f, 0.44f));
        makeSeat(seatR, rand, 1);

        // steering wheel near left side seat
        Vector wheelCenter = seatL.add(0.18f, 0.08f, -0.26f);
        makeSteeringWheel(wheelCenter);
    }

    private void makeSeat(Vector center, Random rand, int side) {
        float w = 0.36f;
        float h = 0.28f;
        float d = 0.36f;
        // seat frame rectangle
        Vector p0 = center.add(-w * 0.5f * side, 0f, -d * 0.4f);
        Vector p1 = center.add(w * 0.5f * side, 0f, -d * 0.4f);
        Vector p2 = center.add(w * 0.5f * side, 0f, d * 0.5f);
        Vector p3 = center.add(-w * 0.5f * side, 0f, d * 0.5f);
        addLine(p0, p1, COLOR_SHADOW);
        addLine(p1, p2, COLOR_SHADOW);
        addLine(p2, p3, COLOR_SHADOW);
        addLine(p3, p0, COLOR_SHADOW);
        // seat back
        addLine(p1, p1.add(0f, h, 0f), COLOR_SHADOW);
        addLine(p0, p0.add(0f, h, 0f), COLOR_SHADOW);
    }

    private void makeSteeringWheel(Vector center) {
        float r = 0.10f;
        int segs = 12;
        for (int i = 0; i < segs; i++) {
            float a1 = TWO_PI() * i / segs;
            float a2 = TWO_PI() * (i + 1) / segs;
            Vector p1 = center.add(cosf(a1) * r, sinf(a1) * r * 0.3f, 0f);
            Vector p2 = center.add(cosf(a2) * r, sinf(a2) * r * 0.3f, 0f);
            addLine(p1, p2, COLOR_SHADOW);
        }
        // spokes
        addLine(center, center.add(0f, -r * 0.2f, 0f), COLOR_SHADOW);
        addLine(center, center.add(r * 0.6f, 0f, 0f), COLOR_SHADOW);
        addLine(center, center.add(-r * 0.6f, 0f, 0f), COLOR_SHADOW);
    }

    private void makeVentsAndGrills(Vector[][] rings, Random rand) {
        // hood vents near front-mid region
        float ventZ = -LENGTH * 0.5f + 0.34f;
        for (int i = -2; i <= 2; i++) {
            Vector a = position.add(i * 0.08f, -GROUND_CLEARANCE + 0.14f, ventZ);
            Vector b = a.add(0.05f, 0f, 0.06f);
            addLine(a, b, COLOR_ACCENT);
        }

        // side intakes: near doors
        float sideIntakeZ = -LENGTH * 0.5f + 0.9f;
        for (int side = -1; side <= 1; side += 2) {
            Vector start = position.add(side * (WIDTH * 0.5f - 0.12f), -GROUND_CLEARANCE + 0.08f, sideIntakeZ);
            Vector end   = start.add(side * -0.14f, 0.04f, 0.02f);
            addLine(start, end, COLOR_ACCENT);
            addLine(start.add(0f, 0.02f, 0f), end.add(0f, 0.02f, 0f), COLOR_ACCENT);
        }
    }

    private void makeExhausts(float rearZ, Random rand) {
        // dual exhaust outlets in center rear
        Vector e1 = position.add(-0.18f, -GROUND_CLEARANCE + 0.02f, rearZ - 0.06f);
        Vector e2 = position.add(0.18f, -GROUND_CLEARANCE + 0.02f, rearZ - 0.06f);
        addLine(e1, e1.add(0f, -0.04f, -0.06f), COLOR_SHADOW);
        addLine(e2, e2.add(0f, -0.04f, -0.06f), COLOR_SHADOW);
    }

    private void addPaintHighlights(Vector[][] rings, Random rand) {
        // a few long, soft highlights along hood and roof to sell gloss
        int highlights = 6;
        for (int h = 0; h < highlights; h++) {
            int i0 = (int) (BODY_SECTIONS * (0.08f + rand.nextFloat() * 0.6f));
            int i1 = Math.min(BODY_SECTIONS - 1, i0 + 6 + (int) (rand.nextFloat() * 8f));
            int s0 = BODY_RING_SEGMENTS / 4 + (int) (rand.nextFloat() * 6f);
            for (int i = i0; i < i1; i++) {
                Vector a = rings[i][s0].add(0f, 0.01f + rand.nextFloat() * 0.02f, 0f);
                Vector b = rings[i + 1][s0].add(0f, 0.01f + rand.nextFloat() * 0.02f, 0f);
                addLine(a, b, COLOR_HIGHLIGHT);
            }
        }
    }

    // ---------- small utility math / index helpers ----------

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

    /**
     * width profile vs longitudinal t (0 front .. 1 rear)
     * returns multiplier for half-width
     */
    private static float bodyWidthProfile(float t) {
        // narrow nose, wide mid-cabin, slight taper rear
        float w = 0.9f + 0.26f * (1f - Math.abs(2f * t - 1f)); // peaked at center
        // compress very front and rear
        if (t < 0.08f) w *= (0.6f + 0.5f * t / 0.08f);
        if (t > 0.92f) w *= (0.75f + 0.25f * (1f - (t - 0.92f) / 0.08f));
        return w;
    }

    /**
     * height profile vs t (0..1)
     * small at nose, rises to roof at cabin, dips at rear
     */
    private static float bodyHeightProfile(float t) {
        // roof center around t ~ 0.45
        float center = 0.45f;
        float base = 0.32f; // base fraction of overall HEIGHT
        float peak = 0.78f; // multiplier near roof
        float d = 1f - Math.min(1f, Math.abs(t - center) / 0.3f);
        return base + (peak - base) * d;
    }

    private static float sideBulgeFactor(float t, float cosA) {
        // slight bulge on sides near wheel arches
        float archFrontT = 0.25f;
        float archRearT  = 0.72f;
        float f = 0f;
        f += Math.max(0f, 0.18f - Math.abs(t - archFrontT) * 1.6f);
        f += Math.max(0f, 0.20f - Math.abs(t - archRearT) * 1.8f);
        // emphasize at side angles
        return f * Math.abs(cosA);
    }

    private int getTopIndex(Vector[] ring) {
        int best = 0;
        float bestY = Float.NEGATIVE_INFINITY;
        for (int i = 0; i < ring.length; i++) {
            float y = ring[i].getY();
            if (y > bestY) { bestY = y; best = i; }
        }
        return best;
    }

    private int getLeftIndex(Vector[] ring) {
        // left is negative X
        int best = 0;
        float bestX = Float.POSITIVE_INFINITY;
        for (int i = 0; i < ring.length; i++) {
            float x = ring[i].getX();
            if (x < bestX) { bestX = x; best = i; }
        }
        return best;
    }

    private int getRightIndex(Vector[] ring) {
        int best = 0;
        float bestX = Float.NEGATIVE_INFINITY;
        for (int i = 0; i < ring.length; i++) {
            float x = ring[i].getX();
            if (x > bestX) { bestX = x; best = i; }
        }
        return best;
    }

    @Override
    public void render(VertexBuffer buf, float tickDelta) {
        for (VertexLine line : cachedLines) {
            buf.vertex(line.start, line.color);
            buf.vertex(line.end, line.color);
        }
    }

    // simple container for cached line segments
    private static class VertexLine {
        final Vector start, end;
        final int color;
        VertexLine(Vector s, Vector e, int c) { start = s; end = e; color = c; }
    }
}
