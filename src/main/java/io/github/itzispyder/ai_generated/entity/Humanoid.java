package io.github.itzispyder.ai_generated.entity;

import io.github.itzispyder.math.Vector;
import io.github.itzispyder.math.VertexBuffer;
import io.github.itzispyder.render.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * High-detail, cached-line humanoid (procedural).
 * - Generates a dense wireframe skin & skeleton once (constructor).
 * - All math uses floats (Math.sin/cos cast to float).
 * - Render replays cached vertex lines.
 */
public class Humanoid extends Entity {

    // ---------- Tunables (change these for size/detail/perf) ----------
    private static final float TOTAL_HEIGHT = 1.85f * 2.0f; // scale factor (rough human scale)
    private static final float HEAD_HEIGHT = 0.22f * 2.0f;
    private static final float HEAD_WIDTH  = 0.16f * 2.0f;
    private static final float HEAD_DEPTH  = 0.18f * 2.0f;

    private static final float TORSO_HEIGHT = 0.55f * 2.0f;
    private static final float TORSO_WIDTH  = 0.28f * 2.0f;
    private static final float TORSO_DEPTH  = 0.18f * 2.0f;

    private static final float UPPER_ARM_LENGTH = 0.25f * 2.0f;
    private static final float LOWER_ARM_LENGTH = 0.23f * 2.0f;
    private static final float SHOULDER_OFFSET = 0.22f * 2.0f;

    private static final float THIGH_LENGTH = 0.45f * 2.0f;
    private static final float CALF_LENGTH  = 0.43f * 2.0f;
    private static final float HIP_OFFSET   = 0.12f * 2.0f;

    // mesh resolution
    private static final int HEAD_LAT = 18;
    private static final int HEAD_LON = 28;
    private static final int TORSO_LAT = 20;
    private static final int TORSO_LON = 28;
    private static final int LIMB_RING_SEGMENTS = 12;

    // colors (ARGB)
    private static final int COLOR_SKIN = 0xFFDFC0A8;
    private static final int COLOR_SHADOW = 0xFFB08974;
    private static final int COLOR_BONE = 0xFFF0E9E0;
    private static final int COLOR_JOINT = 0xFF7A6E64;
    private static final int COLOR_EYE_WHITE = 0xFFFFFFFF;
    private static final int COLOR_PUPIL = 0xFF1B1B1B;
    private static final int COLOR_MOUTH = 0xFF8B3A2F;
    private static final int COLOR_HAIR = 0xFF2F1E13;

    // cached geometry
    private final List<VertexLine> cachedLines = new ArrayList<>();

    public Humanoid(Vector position) {
        super(position);
        generate();
    }

    private void generate() {
        // deterministic randomness by position
        Random rand = new Random(97L * position.hashCode());

        // compute body origin and key points (all relative to `position`)
        float pelvisY = 0f;
        float torsoTopY = pelvisY + TORSO_HEIGHT;
        Vector pelvis = position.add(0f, pelvisY, 0f);
        Vector torsoTop = position.add(0f, torsoTopY, 0f);

        // HEAD: ellipsoid with facial features
        Vector headCenter = torsoTop.add(0f, HEAD_HEIGHT * 0.95f, 0f);
        makeEllipsoid(headCenter, HEAD_WIDTH * 0.95f, HEAD_HEIGHT, HEAD_DEPTH * 0.95f, HEAD_LAT, HEAD_LON, COLOR_SKIN);

        // add eyes (two small spheres on the forward face)
        Vector leftEye = headCenter.add(-HEAD_WIDTH * 0.28f, 0.06f * HEAD_HEIGHT, HEAD_DEPTH * 0.55f);
        Vector rightEye = headCenter.add(HEAD_WIDTH * 0.28f, 0.06f * HEAD_HEIGHT, HEAD_DEPTH * 0.55f);
        makeSphere(leftEye, 0.03f * 2.0f, 8, 12, COLOR_EYE_WHITE);
        makeSphere(rightEye, 0.03f * 2.0f, 8, 12, COLOR_EYE_WHITE);
        makeSphere(leftEye.add(0f, 0f, 0.03f), 0.01f * 2.0f, 8, 8, COLOR_PUPIL);
        makeSphere(rightEye.add(0f, 0f, 0.03f), 0.01f * 2.0f, 8, 8, COLOR_PUPIL);

        // nose (simple bridge + tip)
        Vector noseRoot = headCenter.add(0f, 0.02f * 2.0f, 0.55f * HEAD_DEPTH);
        Vector noseTip  = headCenter.add(0f, -0.03f * 2.0f, 0.92f * HEAD_DEPTH);
        addLine(noseRoot, noseTip, COLOR_SHADOW);
        addLine(noseRoot.add(-0.02f, -0.01f, 0f), noseTip, COLOR_SHADOW);
        addLine(noseRoot.add(0.02f, -0.01f, 0f), noseTip, COLOR_SHADOW);

        // mouth arc (a slight curve)
        float mouthRadius = HEAD_WIDTH * 0.28f;
        Vector mouthCenter = headCenter.add(0f, -0.07f * 2.0f, HEAD_DEPTH * 0.62f);
        int mouthSegments = 10;
        for (int i = 0; i < mouthSegments; i++) {
            float a1 = -0.4f + (0.8f * i / mouthSegments);
            float a2 = -0.4f + (0.8f * (i + 1) / mouthSegments);
            Vector p1 = mouthCenter.add((float) Math.sin(a1) * mouthRadius, (float) Math.cos(a1) * 0.03f, (float) Math.cos(a1) * 0.02f);
            Vector p2 = mouthCenter.add((float) Math.sin(a2) * mouthRadius, (float) Math.cos(a2) * 0.03f, (float) Math.cos(a2) * 0.02f);
            addLine(p1, p2, COLOR_MOUTH);
        }

        // hairline (a few struts)
        for (int i = 0; i < 14; i++) {
            float a = (2f * (float) Math.PI) * i / 14f;
            Vector h1 = headCenter.add((float) Math.cos(a) * HEAD_WIDTH * 0.9f, HEAD_HEIGHT * 0.42f, (float) Math.sin(a) * HEAD_DEPTH * 0.6f);
            Vector h2 = h1.add(0f, 0.03f + rand.nextFloat() * 0.04f, 0f);
            addLine(h1, h2, COLOR_HAIR);
        }

        // NECK
        Vector neckTop = headCenter.add(0f, -HEAD_HEIGHT * 0.45f, -0.02f);
        Vector neckBottom = torsoTop.add(0f, -0.12f * 2.0f, 0f);
        makeCylinder(neckBottom, neckTop, 0.08f * 2.0f, 10, COLOR_SHADOW);

        // CLAVICLES (collarbones)
        Vector clavL = torsoTop.add(-TORSO_WIDTH * 0.46f, -0.06f, 0.06f);
        Vector clavR = torsoTop.add(TORSO_WIDTH * 0.46f, -0.06f, 0.06f);
        addLine(clavL, clavR, COLOR_SHADOW);
        // subtle sternum line
        addLine(torsoTop.add(0f, -0.08f, 0f), torsoTop.add(0f, -TORSO_HEIGHT * 0.6f, 0f), COLOR_SHADOW);

        // TORSO (ribcage + abdomen as stacked ellipsoids)
        Vector torsoCenter = position.add(0f, pelvisY + TORSO_HEIGHT * 0.55f, 0f);
        makeEllipsoid(torsoCenter.add(0f, 0.08f, 0f), TORSO_WIDTH * 0.95f, TORSO_HEIGHT * 0.65f, TORSO_DEPTH * 1.05f, TORSO_LAT, TORSO_LON, COLOR_SKIN);
        makeEllipsoid(torsoCenter.add(0f, -TORSO_HEIGHT * 0.15f, 0f), TORSO_WIDTH * 0.82f, TORSO_HEIGHT * 0.45f, TORSO_DEPTH * 1.02f, TORSO_LAT, TORSO_LON, COLOR_SKIN);
        // ribs lines (horizontal arcs)
        int ribs = 6;
        for (int r = 0; r < ribs; r++) {
            float fy = torsoTopY - (r + 1) * (TORSO_HEIGHT / (ribs + 2f));
            for (int s = 0; s < 24; s++) {
                float a1 = (2f * (float) Math.PI) * s / 24f;
                float a2 = (2f * (float) Math.PI) * (s + 1) / 24f;
                Vector p1 = position.add((float) Math.cos(a1) * TORSO_WIDTH * (0.9f - 0.08f * r), fy - 0.05f, (float) Math.sin(a1) * TORSO_DEPTH * 0.9f);
                Vector p2 = position.add((float) Math.cos(a2) * TORSO_WIDTH * (0.9f - 0.08f * r), fy - 0.05f, (float) Math.sin(a2) * TORSO_DEPTH * 0.9f);
                addLine(p1, p2, COLOR_SHADOW);
            }
        }

        // SPINE vertebrae (small stacked spheres center back)
        int verts = 12;
        for (int v = 0; v < verts; v++) {
            float t = (float) v / (verts - 1f);
            float y = torsoTopY - t * (TORSO_HEIGHT + 0.05f);
            Vector vert = position.add(0f, y, -TORSO_DEPTH * 0.42f);
            makeSphere(vert, 0.03f, 6, 6, COLOR_BONE);
            if (v > 0) {
                Vector prev = position.add(0f, torsoTopY - (t - 1f / (verts - 1f)) * (TORSO_HEIGHT + 0.05f), -TORSO_DEPTH * 0.42f);
                addLine(prev, vert, COLOR_BONE);
            }
        }

        // HIPS / PELVIS (ellipsoid)
        makeEllipsoid(pelvis.add(0f, -0.05f, 0f), TORSO_WIDTH * 0.72f, 0.14f * 2.0f, TORSO_DEPTH * 0.9f, 12, 20, COLOR_SKIN);

        // SHOULDERS (deltoids) and ARMS (upper + lower, with muscle bulge)
        Vector shoulderLeft = torsoTop.add(-SHOULDER_OFFSET, -0.06f, 0f);
        Vector shoulderRight = torsoTop.add(SHOULDER_OFFSET, -0.06f, 0f);
        makeSphere(shoulderLeft, 0.12f * 2.0f, 8, 10, COLOR_SKIN);
        makeSphere(shoulderRight, 0.12f * 2.0f, 8, 10, COLOR_SKIN);

        // left arm
        Vector elbowL = shoulderLeft.add(-UPPER_ARM_LENGTH * 0.6f, -UPPER_ARM_LENGTH * 0.6f, 0f);
        makeCylinder(shoulderLeft, elbowL, 0.10f * 2.0f, LIMB_RING_SEGMENTS, COLOR_SKIN);
        makeSphere(elbowL, 0.06f * 2.0f, 8, 8, COLOR_JOINT);

        Vector wristL = elbowL.add(-LOWER_ARM_LENGTH * 0.75f, -LOWER_ARM_LENGTH * 0.35f, 0.02f);
        makeCylinder(elbowL, wristL, 0.08f * 2.0f, LIMB_RING_SEGMENTS, COLOR_SKIN);
        makeSphere(wristL, 0.05f * 2.0f, 8, 8, COLOR_JOINT);
        makeHand(wristL, -1, rand); // -1 indicates left

        // right arm (mirror)
        Vector elbowR = shoulderRight.add(UPPER_ARM_LENGTH * 0.6f, -UPPER_ARM_LENGTH * 0.6f, 0f);
        makeCylinder(shoulderRight, elbowR, 0.10f * 2.0f, LIMB_RING_SEGMENTS, COLOR_SKIN);
        makeSphere(elbowR, 0.06f * 2.0f, 8, 8, COLOR_JOINT);

        Vector wristR = elbowR.add(LOWER_ARM_LENGTH * 0.75f, -LOWER_ARM_LENGTH * 0.35f, 0.02f);
        makeCylinder(elbowR, wristR, 0.08f * 2.0f, LIMB_RING_SEGMENTS, COLOR_SKIN);
        makeSphere(wristR, 0.05f * 2.0f, 8, 8, COLOR_JOINT);
        makeHand(wristR, 1, rand); // 1 indicates right

        // LEGS: thighs -> knees -> calves -> ankles -> feet
        Vector hipLeft = pelvis.add(-HIP_OFFSET, 0f, 0f);
        Vector hipRight = pelvis.add(HIP_OFFSET, 0f, 0f);

        Vector kneeL = hipLeft.add(0f, -THIGH_LENGTH * 0.55f, 0f);
        makeCylinder(hipLeft, kneeL, 0.14f * 2.0f, LIMB_RING_SEGMENTS, COLOR_SKIN);
        makeSphere(kneeL, 0.07f * 2.0f, 8, 8, COLOR_JOINT);

        Vector ankleL = kneeL.add(0f, -CALF_LENGTH * 0.75f, 0.06f);
        makeCylinder(kneeL, ankleL, 0.12f * 2.0f, LIMB_RING_SEGMENTS, COLOR_SKIN);
        makeSphere(ankleL, 0.06f * 2.0f, 8, 8, COLOR_JOINT);
        makeFoot(ankleL, -1, rand);

        Vector kneeR = hipRight.add(0f, -THIGH_LENGTH * 0.55f, 0f);
        makeCylinder(hipRight, kneeR, 0.14f * 2.0f, LIMB_RING_SEGMENTS, COLOR_SKIN);
        makeSphere(kneeR, 0.07f * 2.0f, 8, 8, COLOR_JOINT);

        Vector ankleR = kneeR.add(0f, -CALF_LENGTH * 0.75f, 0.06f);
        makeCylinder(kneeR, ankleR, 0.12f * 2.0f, LIMB_RING_SEGMENTS, COLOR_SKIN);
        makeSphere(ankleR, 0.06f * 2.0f, 8, 8, COLOR_JOINT);
        makeFoot(ankleR, 1, rand);

        // add some muscular striations across torso / limbs for realism (oblique lines)
        addMuscleStriations(rand, torsoCenter, TORSO_WIDTH, TORSO_HEIGHT, TORSO_DEPTH);
    }

    // ----- Helpers that write into cachedLines -----

    private void addLine(Vector a, Vector b, int color) {
        cachedLines.add(new VertexLine(a, b, color));
    }

    private void makeSphere(Vector center, float radius, int lat, int lon, int color) {
        makeEllipsoid(center, radius, radius, radius, lat, lon, color);
    }

    private void makeEllipsoid(Vector center, float rx, float ry, float rz, int lat, int lon, int color) {
        // latitude runs 0..pi, longitude runs 0..2pi
        Vector[][] pts = new Vector[lat + 1][lon];
        for (int i = 0; i <= lat; i++) {
            float phi = (float) Math.PI * i / lat;
            float sinPhi = (float) Math.sin(phi);
            float cosPhi = (float) Math.cos(phi);
            for (int j = 0; j < lon; j++) {
                float theta = (2f * (float) Math.PI) * j / lon;
                float x = sinPhi * (float) Math.cos(theta) * rx;
                float y = cosPhi * ry;
                float z = sinPhi * (float) Math.sin(theta) * rz;
                pts[i][j] = center.add(x, y, z);
            }
        }
        for (int i = 0; i < lat; i++) {
            for (int j = 0; j < lon; j++) {
                Vector v1 = pts[i][j];
                Vector v2 = pts[i][(j + 1) % lon];
                Vector v3 = pts[i + 1][j];
                Vector v4 = pts[i + 1][(j + 1) % lon];
                // mesh lines
                addLine(v1, v2, color);
                addLine(v1, v3, color);
                // optionally connect diagonals for density
                addLine(v2, v4, color);
                addLine(v3, v4, color);
            }
        }
    }

    private void makeCylinder(Vector a, Vector b, float radius, int radialSegments, int color) {
        // produce rings along the straight line from a to b with simple XZ-based circular cross-sections
        // since we don't have full rotate helpers, we align rings in world XZ plane and place them along AB.
        int rings = 6;
        for (int r = 0; r <= rings; r++) {
            float t = (float) r / (float) rings;
            Vector center = lerp(a, b, t);
            float localRadius = radius * (1.0f - 0.25f * t); // slight taper
            Vector[] ring = new Vector[radialSegments];
            for (int s = 0; s < radialSegments; s++) {
                float theta = (2f * (float) Math.PI) * s / radialSegments;
                float rx = (float) Math.cos(theta) * localRadius;
                float rz = (float) Math.sin(theta) * localRadius;
                ring[s] = center.add(rx, 0f, rz);
            }
            // connect ring edges
            for (int s = 0; s < radialSegments; s++) {
                addLine(ring[s], ring[(s + 1) % radialSegments], color);
                if (r > 0) {
                    // vertical connector to previous ring
                    Vector prevCenter = lerp(a, b, (float) (r - 1) / rings);
                    float prevRadius = radius * (1.0f - 0.25f * ((float) (r - 1) / rings));
                    Vector prev = prevCenter.add((float) Math.cos((2f * (float) Math.PI) * s / radialSegments) * prevRadius, 0f,
                            (float) Math.sin((2f * (float) Math.PI) * s / radialSegments) * prevRadius);
                    addLine(prev, ring[s], color);
                }
                // cross connector to other side to imply roundness
                addLine(ring[s], ring[(s + radialSegments/2) % radialSegments], color);
            }
        }
    }

    private Vector lerp(Vector a, Vector b, float t) {
        // linear interpolation pointwise
        // assume Vector.add returns a new Vector; build using positions relative to `position`
        // compute a + (b-a)*t via adds
        float ax = a.getX(); float ay = a.getY(); float az = a.getZ();
        float bx = b.getX(); float by = b.getY(); float bz = b.getZ();
        return position.add(ax + (bx - ax) * t - position.getX(),
                ay + (by - ay) * t - position.getY(),
                az + (bz - az) * t - position.getZ());
    }

    // Make a hand at wrist. side = -1 for left, 1 for right
    private void makeHand(Vector wrist, int side, Random rand) {
        // palm base
        float palmWidth = 0.09f * 2.0f;
        float palmDepth = 0.04f * 2.0f;
        Vector palmCenter = wrist.add(side * (palmWidth * 0.4f), -0.02f, palmDepth * 0.6f);
        // simple palm rectangle edges
        Vector p0 = palmCenter.add(-palmWidth * 0.5f * side, 0f, -palmDepth * 0.4f);
        Vector p1 = palmCenter.add(palmWidth * 0.5f * side, 0f, -palmDepth * 0.4f);
        Vector p2 = palmCenter.add(palmWidth * 0.6f * side, 0f, palmDepth * 0.6f);
        Vector p3 = palmCenter.add(-palmWidth * 0.6f * side, 0f, palmDepth * 0.6f);
        addLine(p0, p1, COLOR_SHADOW);
        addLine(p1, p2, COLOR_SHADOW);
        addLine(p2, p3, COLOR_SHADOW);
        addLine(p3, p0, COLOR_SHADOW);

        // fingers: 4 fingers + thumb
        float[] fingerLengths = {0.08f * 2.0f, 0.075f * 2.0f, 0.07f * 2.0f, 0.06f * 2.0f};
        float fingerSpacing = palmWidth * 0.18f;
        for (int f = 0; f < 4; f++) {
            Vector base = palmCenter.add(side * (-palmWidth * 0.2f + f * fingerSpacing), 0f, palmDepth * 0.7f);
            Vector prev = base;
            // three phalanges per finger (proximal, intermediate, distal)
            for (int p = 0; p < 3; p++) {
                float seg = fingerLengths[f] * (p == 0 ? 0.5f : (p == 1 ? 0.35f : 0.15f));
                Vector tip = prev.add(side * 0.01f, -seg, 0.02f * (p + 1));
                addLine(prev, tip, COLOR_SHADOW);
                prev = tip;
            }
        }
        // thumb
        Vector thumbBase = palmCenter.add(side * (palmWidth * 0.6f), -0.02f, 0.0f);
        Vector thumbTip = thumbBase.add(side * 0.06f, -0.03f, -0.01f);
        addLine(thumbBase, thumbTip, COLOR_SHADOW);
    }

    private void makeFoot(Vector ankle, int side, Random rand) {
        // simple triangular foot mesh with toes
        float footLength = 0.24f * 2.0f;
        float footWidth = 0.10f * 2.0f;
        Vector heel = ankle.add(-0.02f, -0.03f, -0.02f);
        Vector toeCenter = ankle.add(0f, -0.03f, footLength * 0.7f);
        Vector left = ankle.add(side * (footWidth * 0.6f), -0.03f, footLength * 0.25f);
        Vector right = ankle.add(-side * (footWidth * 0.6f), -0.03f, footLength * 0.25f);

        addLine(heel, left, COLOR_SHADOW);
        addLine(left, toeCenter, COLOR_SHADOW);
        addLine(toeCenter, right, COLOR_SHADOW);
        addLine(right, heel, COLOR_SHADOW);

        // toes (5 small claws)
        int toes = 5;
        for (int t = 0; t < toes; t++) {
            float frac = (float) t / (toes - 1f);
            Vector base = left.add((right.getX() - left.getX()) * (frac), 0f, (toeCenter.getZ() - left.getZ()) * 0.2f);
            Vector tip = base.add(0f, 0f, 0.04f + frac * 0.02f);
            addLine(base, tip, COLOR_SHADOW);
        }
    }

    private void addMuscleStriations(Random rand, Vector torsoCenter, float w, float h, float d) {
        // create many small oblique lines around torso to imply skin texture & muscles
        int count = 140;
        for (int i = 0; i < count; i++) {
            float a = (2f * (float) Math.PI) * rand.nextFloat();
            float vy = torsoCenter.getY() + (rand.nextFloat() - 0.5f) * h;
            float rx = (float) Math.cos(a) * w * (0.6f + rand.nextFloat() * 0.4f);
            float rz = (float) Math.sin(a) * d * (0.7f + rand.nextFloat() * 0.3f);
            Vector p = position.add(rx, vy, rz);
            Vector q = p.add((rand.nextFloat() - 0.5f) * 0.06f, (rand.nextFloat() - 0.3f) * 0.06f, (rand.nextFloat() - 0.5f) * 0.06f);
            addLine(p, q, COLOR_SHADOW);
        }
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
