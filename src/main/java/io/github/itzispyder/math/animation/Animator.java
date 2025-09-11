package io.github.itzispyder.math.animation;

import io.github.itzispyder.util.MathUtil;

public class Animator {

    private long start, length;
    private boolean reversed;
    private Animations.AnimationController animationController;

    public Animator(long length, Animations.AnimationController animationController) {
        this.start = System.currentTimeMillis();
        this.length = length;
        this.reversed = false;
        this.animationController = animationController;
    }

    public Animator(long length) {
        this(length, Animations.LINEAR);
    }

    private float getAnimation(float x) {
        if (x <= 0 || x >= 1)
            return x;
        return (float) animationController.f(x); // lmao the f(x) math pun
    }

    public float getAnimation() {
        return getAnimation(getProgressClamped());
    }

    public float getAnimationReversed() {
        return getAnimation(getProgressClampedReversed());
    }

    public Animations.AnimationController getAnimationController() {
        return animationController;
    }

    public void setAnimationController(Animations.AnimationController animationController) {
        this.animationController = animationController;
    }

    public float getProgress() {
        long pass = System.currentTimeMillis() - start;
        float rat = pass / (float)length;
        return reversed ? 1 - rat : rat;
    }

    public float getProgressClamped() {
        return MathUtil.clamp(getProgress(), 0, 1);
    }

    public float getProgressReversed() {
        return 1 - getProgress();
    }

    public float getProgressClampedReversed() {
        return MathUtil.clamp(getProgressReversed(), 0, 1);
    }

    public boolean isFinished() {
        float p = getProgress();
        return reversed ? p <= 0.0 : p >= 1.0;
    }

    public void reverse() {
        reversed = !reversed;
    }

    public void setReversed(boolean reversed) {
        this.reversed = reversed;
    }

    public boolean isReversed() {
        return reversed;
    }

    public void reset(long length) {
        this.start = System.currentTimeMillis();
        this.length = length;
    }

    public void reset() {
        this.start = System.currentTimeMillis();
    }

    public long getLength() {
        return length;
    }
}
