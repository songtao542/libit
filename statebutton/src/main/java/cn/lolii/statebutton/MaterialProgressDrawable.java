package cn.lolii.statebutton;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.*;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

@SuppressWarnings({"unused", "WeakerAccess", "JavaDoc"})
public class MaterialProgressDrawable extends Drawable implements Animatable {
    private static final Interpolator LINEAR_INTERPOLATOR = new LinearInterpolator();
    private static final Interpolator END_CURVE_INTERPOLATOR = new EndCurveInterpolator();
    private static final Interpolator START_CURVE_INTERPOLATOR = new StartCurveInterpolator();

    @Retention(RetentionPolicy.CLASS)
    @IntDef({LARGE, DEFAULT, SMALL})
    public @interface ProgressDrawableSize {
    }

    // Maps to ProgressBar.Large style
    public static final int LARGE = 0;
    // Maps to ProgressBar default style
    public static final int DEFAULT = 1;
    // Maps to ProgressBar.Small style
    public static final int SMALL = 2;
    // Maps to ProgressBar default style
    private static final int CIRCLE_DIAMETER = 40;
    private static final int INNER_RADIUS = 10;
    private static final int STROKE_WIDTH = 4;
    // Maps to ProgressBar.Large style
    private static final int CIRCLE_DIAMETER_LARGE = 76;
    private static final float INNER_RADIUS_LARGE = 15.5f;
    private static final float STROKE_WIDTH_LARGE = 6f;
    // Maps to ProgressBar.Small style
    private static final int CIRCLE_DIAMETER_SMALL = 16;
    private static final float INNER_RADIUS_SMALL = 3.15f;
    private static final float STROKE_WIDTH_SMALL = 1.3f;
    private final int[] COLORS = new int[]{
            Color.BLACK
    };

    public enum Shape {
        ROUNDED_RECTANGLE,
        CIRCLE
    }

    /**
     * The duration of a single progress spin in milliseconds.
     */
    private static final int ANIMATION_DURATION = 1000 * 80 / 60;
    /**
     * The duration of a single progress spin in milliseconds.
     */
    private static final int ROUNDED_RECTANGLE_ANIMATION_DURATION = 1000 * 80 / 40;
//    private static final int ROUNDED_RECTANGLE_ANIMATION_DURATION = 20000;
    /**
     * The number of points in the progress "star".
     */
    private static final float NUM_POINTS = 5f;
    /**
     * The list of animators operating on this drawable.
     */
    private final ArrayList<Animation> mAnimators = new ArrayList<Animation>();
    /**
     * The indicator ring, used to manage animation state.
     */
    private final Ring mRing;
    /**
     * Canvas rotation in degrees.
     */
    private float mRotation;
    /**
     * Layout info for the arrowhead in dp
     */
    private static final int ARROW_WIDTH = 10;
    private static final int ARROW_HEIGHT = 5;
    private static final float ARROW_OFFSET_ANGLE = 5;
    private Resources mResources;
    private int mColorIndex;
    private View mParent;
    private Animation mAnimation;
    private float mRotationCount;
    private int[] mColors;
    private double mWidth;
    private double mHeight;
    private double mInnerRadius;
    private double mStrokeWidth;
    private Animation mFinishAnimation;

    private boolean mVisible = true;

    public MaterialProgressDrawable(Context context, View parent) {
        this(context, parent, CIRCLE_DIAMETER, CIRCLE_DIAMETER, INNER_RADIUS, STROKE_WIDTH, Shape.CIRCLE);
    }

    public MaterialProgressDrawable(Context context, View parent, double width, double height, double innerRadius, double strokeWidth, Shape shape) {
        mParent = parent;
        mResources = context.getResources();
        mRing = new Ring(mCallback);
        mColors = COLORS;
        mRing.setColors(mColors);
        mRing.setShape(shape);
        initialize(width, height, innerRadius, strokeWidth);
        setupAnimators();
    }

    private void initialize(double progressCircleWidth, double progressCircleHeight, double innerRadius, double strokeWidth) {
        final Ring ring = mRing;
        final DisplayMetrics metrics = mResources.getDisplayMetrics();
        final float screenDensity = metrics.density;
        mWidth = progressCircleWidth * screenDensity;
        mHeight = progressCircleHeight * screenDensity;
        mInnerRadius = innerRadius * screenDensity;
        mStrokeWidth = strokeWidth * screenDensity;
        ring.setStrokeWidth((float) mStrokeWidth);
        ring.setInnerRadius(mInnerRadius);
        ring.setDensity(screenDensity);
        ring.setColorIndex(0);
        final float minEdge = (float) Math.min(mWidth, mHeight);
        if (mInnerRadius <= 0 || minEdge < 0) {
            ring.setInsets((int) Math.ceil(mStrokeWidth / 2.0f));
        } else {
            float insets = (float) (minEdge / 2.0f - mInnerRadius);
            ring.setInsets(insets);
        }
    }

    /**
     * Set the overall size for the progress spinner. This updates the radius
     * and stroke width of the ring.
     *
     * @param size One of {@link MaterialProgressDrawable#LARGE},
     *             {@link MaterialProgressDrawable#DEFAULT}, or
     *             {@link MaterialProgressDrawable#SMALL}.
     */
    public void updateSizes(@ProgressDrawableSize int size) {
        final DisplayMetrics metrics = mResources.getDisplayMetrics();
        final float screenDensity = metrics.density;
        int progressCircleWidth;
        int progressCircleHeight;
        float innerRadius;
        float strokeWidth;
        if (size == LARGE) {
            progressCircleWidth = progressCircleHeight = CIRCLE_DIAMETER_LARGE;
            innerRadius = INNER_RADIUS_LARGE;
            strokeWidth = STROKE_WIDTH_LARGE;
        } else if (size == SMALL) {
            progressCircleWidth = progressCircleHeight = CIRCLE_DIAMETER_SMALL;
            innerRadius = INNER_RADIUS_SMALL;
            strokeWidth = STROKE_WIDTH_SMALL;
        } else {
            progressCircleWidth = progressCircleHeight = CIRCLE_DIAMETER;
            innerRadius = INNER_RADIUS;
            strokeWidth = STROKE_WIDTH;
        }
        mWidth = progressCircleWidth * screenDensity;
        mHeight = progressCircleHeight * screenDensity;

        mInnerRadius = innerRadius * screenDensity;
        mStrokeWidth = strokeWidth * screenDensity;
    }

    public void setStrokeWidth(double strokeWidth) {
        mStrokeWidth = strokeWidth;
        mRing.setStrokeWidth((float) mStrokeWidth);
        mRing.setInsets(((float) mStrokeWidth) / 2);
    }

    /**
     * @param show Set to true to display the arrowhead on the progress spinner.
     */
    public void showArrow(boolean show) {
        mRing.setShowArrow(show);
    }

    /**
     * @param scale Set the scale of the arrowhead for the spinner.
     */
    public void setArrowScale(float scale) {
        mRing.setArrowScale(scale);
    }

    /**
     * Set the start and end trim for the progress spinner arc.
     *
     * @param startAngle start angle
     * @param endAngle   end angle
     */
    public void setStartEndTrim(float startAngle, float endAngle) {
        mRing.setStartTrim(startAngle);
        mRing.setEndTrim(endAngle);
    }

    /**
     * Only works when hardware acceleration is turned off
     * {@link View#setLayerType(int, Paint)} setLayerType(View.LAYER_TYPE_SOFTWARE, null)
     *
     * @param radius
     * @param style
     */
    public void setBlurMaskFilter(float radius, BlurMaskFilter.Blur style) {
        mRing.setBlurMaskFilter(radius, style);
    }

    /**
     * clear the BlurMaskFilter
     */
    public void clearBlurMaskFilter() {
        mRing.clearBlurMaskFilter();
    }

    /**
     * Only works when hardware acceleration is turned off
     *
     * @param radius
     * @param color
     */
    public void setShadowLayer(float radius, int color) {
        mRing.setShadowLayer(radius, color);
    }

    /**
     * clear the ShadowLayer
     */
    public void clearShadowLayer() {
        mRing.clearShadowLayer();
    }

    public void setShape(Shape shape) {
        mRing.setShape(shape);
        //重新初始化动画
        setupAnimators();
    }

    /**
     * clear the ShadowLayer
     */
    public void setVisible(boolean visible) {
        mVisible = visible;
        invalidateSelf();
    }

    /**
     * Set the amount of rotation to apply to the progress spinner.
     *
     * @param rotation Rotation in degrees
     */
    public void setProgressRotation(float rotation) {
        mRing.setRotation(rotation);
    }

    /**
     * Set the colors used in the progress animation from color resources.
     * The first color will also be the color of the bar that grows in response
     * to a user swipe gesture.
     *
     * @param colors scheme colors
     */
    public void setColorSchemeColors(int... colors) {
        mColors = colors;
        mRing.setColors(mColors);
    }

    @Override
    public int getIntrinsicHeight() {
        return getBounds().width() > 0 ? getBounds().width() : (int) mHeight;
    }

    @Override
    public int getIntrinsicWidth() {
        return getBounds().height() > 0 ? getBounds().height() : (int) mWidth;
    }

    @Override
    public void draw(Canvas c) {
        if (!mVisible) {
            return;
        }
        final Rect bounds = getBounds();
        final int saveCount = c.save();
        if (mRing.mShape == Shape.CIRCLE) {
            c.rotate(mRotation, bounds.exactCenterX(), bounds.exactCenterY());
        } else {
            mRing.mRotationExtra = mRotation / 2;
        }
        mRing.draw(c, bounds);
        c.restoreToCount(saveCount);
    }

    @Override
    public void setAlpha(int alpha) {
        mRing.setAlpha(alpha);
    }

    public int getAlpha() {
        return mRing.getAlpha();
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mRing.setColorFilter(colorFilter);
    }

    @SuppressWarnings("unused")
    private void setRotation(float rotation) {
        mRotation = rotation;
        invalidateSelf();
    }

    @SuppressWarnings("unused")
    private float getRotation() {
        return mRotation;
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public boolean isRunning() {
        final ArrayList<Animation> animators = mAnimators;
        final int N = animators.size();
        for (int i = 0; i < N; i++) {
            final Animation animator = animators.get(i);
            if (animator.hasStarted() && !animator.hasEnded()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void start() {
        if (mAnimation != null && mFinishAnimation != null) {
            mRing.setAnimating(true);
            mAnimation.reset();
            mRing.storeOriginals();
            if (mRing.getStartingStartTrim() != 0) {
                mParent.startAnimation(mFinishAnimation);
            } else {
                mColorIndex = 0;
                mRing.setColorIndex(mColorIndex);
                mRing.resetOriginals();
                mParent.startAnimation(mAnimation);
            }
        }
    }

    @Override
    public void stop() {
        mRing.setAnimating(false);
        mParent.clearAnimation();
        setRotation(0);
        mColorIndex = 0;
        mRing.setColorIndex(mColorIndex);
        mRing.resetOriginals();
    }

    private void setupAnimators() {
        final Ring ring = mRing;
        final long duration = mRing.mShape == Shape.CIRCLE ? ANIMATION_DURATION : ROUNDED_RECTANGLE_ANIMATION_DURATION;
        final Animation finishRingAnimation = new Animation() {
            public void applyTransformation(float interpolatedTime, Transformation t) {
                // shrink back down and complete a full roation before starting other circles
                float targetRotation = (float) (Math.floor(ring.getStartingRotation() / .75f) + 1f);
                final float startTrim = ring.getStartingEndTrim() + (ring.getStartingStartTrim() - ring.getStartingEndTrim()) * interpolatedTime;
                ring.setEndTrim(startTrim);
                final float rotation = ring.getStartingRotation() + ((targetRotation - ring.getStartingRotation()) * interpolatedTime);
                ring.setRotation(rotation);
            }
        };
        finishRingAnimation.setInterpolator(LINEAR_INTERPOLATOR);
        finishRingAnimation.setDuration(duration / 2);
        finishRingAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mColorIndex = (mColorIndex + 1) % (mColors.length);
                ring.setColorIndex(mColorIndex);
                ring.resetOriginals();
                mParent.startAnimation(mAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        final Animation animation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                final float endTrim = 0.75f * START_CURVE_INTERPOLATOR.getInterpolation(interpolatedTime);
                ring.setEndTrim(endTrim);
                final float startTrim = 0.75f * END_CURVE_INTERPOLATOR.getInterpolation(interpolatedTime);
                ring.setStartTrim(startTrim);
                final float rotation = 0.25f * interpolatedTime;
                ring.setRotation(rotation);
                float groupRotation = ((720.0f / NUM_POINTS) * interpolatedTime) + (720.0f * (mRotationCount / NUM_POINTS));
                setRotation(groupRotation);
            }
        };
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.RESTART);
        animation.setInterpolator(LINEAR_INTERPOLATOR);
        animation.setDuration(duration);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mRotationCount = 0;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // do nothing
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                mColorIndex = (mColorIndex + 1) % (mColors.length);
                ring.setColorIndex(mColorIndex);
                ring.resetOriginals();
                mRotationCount = (mRotationCount + 1) % (NUM_POINTS);
            }
        });
        mFinishAnimation = finishRingAnimation;
        mAnimation = animation;
    }

    private final Callback mCallback = new Callback() {
        @Override
        public void invalidateDrawable(@NonNull Drawable d) {
            invalidateSelf();
        }

        @Override
        public void scheduleDrawable(@NonNull Drawable d, @NonNull Runnable what, long when) {
            scheduleSelf(what, when);
        }

        @Override
        public void unscheduleDrawable(@NonNull Drawable d, @NonNull Runnable what) {
            unscheduleSelf(what);
        }
    };

    @SuppressWarnings("WeakerAccess")
    private static class Ring {
        private final RectF mTempBounds = new RectF();
        private final Paint mPaint = new Paint();
        private final Paint mArrowPaint = new Paint();
        private final Callback mCallback;
        private float mStartTrim = 0.0f;
        private float mEndTrim = 0.0f;
        private float mRotation = 0.0f;
        private float mRotationExtra = 0.0f;
        private float mStrokeWidth = 5.0f;
        private float mStrokeInset = 2.5f;
        private int[] mColors;
        private int mColorIndex;
        private float mStartingStartTrim;
        private float mStartingEndTrim;
        private float mStartingRotation;
        private boolean mShowArrow;
        private Path mArrow;
        private Path mPath;
        private Path mRemainingPath;
        private Path mRoundRectPath;
        private float mRoundRectPathLength;
        private PathMeasure mProgressMeasure;
        private float mArrowScale;
        private double mRingInnerRadius;
        private Path mArrowCopy;
        private int mArrowWidth;
        private int mArrowHeight;
        private float mDensity;
        private Matrix mArrowScaleMatrix;
        private BlurMaskFilter mBlurMaskFilter;
        private boolean mAnimating = true;

        private float mShadowRadius = -1;
        private int mShadowColor = -1;

        private Shape mShape = Shape.CIRCLE;

        public Ring(Callback callback) {
            mCallback = callback;
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setAntiAlias(true);
            mPaint.setStyle(Paint.Style.STROKE);
            mArrowPaint.setStrokeWidth(4);
            mArrowPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mArrowPaint.setAntiAlias(true);
        }

        /**
         * Draw the progress spinner
         */
        public void draw(Canvas c, Rect bounds) {
            final RectF arcBounds = mTempBounds;
            arcBounds.set(bounds);
            arcBounds.inset(mStrokeInset, mStrokeInset);
            // Ensure the sweep angle isn't too small to draw.
            final float diameter = Math.min(arcBounds.width(), arcBounds.height());

            mPaint.setColor(mColors[mColorIndex]);
            if (mBlurMaskFilter != null) {
                mPaint.setMaskFilter(mBlurMaskFilter);
            }
            if (mShadowRadius > 0 && mShadowColor >= 0) {
                mPaint.setShadowLayer(mShadowRadius, 0, 0, mShadowColor);
            }

            //无动画效果
            if (!mAnimating) {
                if (mShape == Shape.ROUNDED_RECTANGLE) {
                    final float r = diameter / 2;
                    c.drawRoundRect(arcBounds, r, r, mPaint);
                } else {
                    c.drawArc(arcBounds, 0, 360, false, mPaint);
                }
                return;
            }

            mPaint.setStrokeJoin(Paint.Join.ROUND);
            final float startAngle = (mStartTrim + mRotation) * 360;
            final float endAngle = (mEndTrim + mRotation) * 360;
            float sweepAngle = endAngle - startAngle;
            final float minAngle = (float) (360.0 / (diameter * Math.PI));
            if (sweepAngle < minAngle && sweepAngle > -minAngle) {
                sweepAngle = Math.signum(sweepAngle) * minAngle;
            }

            if (mShape == Shape.ROUNDED_RECTANGLE) {
                final float cx = bounds.exactCenterX();
                final float cy = bounds.exactCenterY();
                final float maxR = Math.max(c.getWidth(), c.getHeight());
                final float minR = diameter / 2;
                /**
                 * 方案一，使用 clip path 裁剪画布
                 calculateSectorClip(mClipArc, cx, cy, maxR, startAngle + mRotationExtra, sweepAngle);
                 c.clipRect(0, 0, c.getWidth(), c.getHeight());
                 c.clipPath(mClipArc);
                 c.drawRoundRect(arcBounds, minR, minR, mPaint);
                 */

                if (mRoundRectPath.isEmpty()) {
                    mRoundRectPath.addRoundRect(arcBounds, minR, minR, Path.Direction.CW);
                    mProgressMeasure.setPath(mRoundRectPath, false);
                    mRoundRectPathLength = mProgressMeasure.getLength();
                }

                float angle = startAngle + mRotationExtra;
                while (angle >= 360) {
                    angle -= 360;
                }
                float startD = angle / 360 * mRoundRectPathLength;
                float stopD = startD + sweepAngle / 360 * mRoundRectPathLength;

                mPath.reset();
                //mClipArc.lineTo(0, 0);
                mProgressMeasure.getSegment(startD, stopD, mPath, true);
                c.drawPath(mPath, mPaint);
                float remainingAngle = angle + sweepAngle - 360;
                if (remainingAngle > 0) {
                    startD = 0;
                    stopD = remainingAngle / 360 * mRoundRectPathLength;
                    mRemainingPath.reset();
                    mProgressMeasure.getSegment(startD, stopD, mRemainingPath, true);
                    c.drawPath(mRemainingPath, mPaint);
                }
            } else {
                c.drawArc(arcBounds, startAngle, sweepAngle, false, mPaint);
            }
            if (mArrow == null) {
                mArrowWidth = (int) (ARROW_WIDTH * mDensity);
                mArrowHeight = (int) (ARROW_HEIGHT * mDensity);
                // Adjust the position of the triangle so that it is inset as much as the arc, but
                // also centered on the arc.
                int inset = (int) (mStrokeInset / 2 + (mArrowWidth / mStrokeWidth));
                double rad = Math.toRadians(startAngle + sweepAngle);
                float x = (float) (mRingInnerRadius * Math.cos(rad) + bounds.exactCenterX());
                float y = (float) (mRingInnerRadius * Math.sin(rad) + bounds.exactCenterY());
                Point a = new Point((int) (x - inset), (int) (y));
                Point b = new Point((int) (x - inset) + mArrowWidth, (int) (y));
                Point cPoint = new Point((int) (x - inset) + (mArrowWidth / 2), (int) (y) + mArrowHeight);
                mArrow = new Path();
                mArrow.setFillType(Path.FillType.EVEN_ODD);
                mArrow.moveTo(a.x, a.y);
                mArrow.lineTo(b.x, b.y);
                mArrow.lineTo(cPoint.x, cPoint.y);
                mArrow.lineTo(a.x, a.y);
                mArrow.close();
                mArrowCopy = new Path();
            }
            if (mShowArrow) {
                // draw a triangle
                if (mArrowScaleMatrix == null) {
                    mArrowScaleMatrix = new Matrix();
                }
                Matrix scaleMatrix = mArrowScaleMatrix;
                RectF arrowRect = mTempBounds;
                mArrow.computeBounds(arrowRect, true);
                scaleMatrix.setScale(mArrowScale, mArrowScale, arrowRect.centerX(), arrowRect.centerY());
                mArrow.transform(scaleMatrix, mArrowCopy);
                mArrowPaint.setColor(mColors[mColorIndex]);
                // Offset the arrow slightly so that it aligns with the cap on the arc
                c.rotate(startAngle + sweepAngle - ARROW_OFFSET_ANGLE, bounds.exactCenterX(), bounds.exactCenterY());
                c.drawPath(mArrowCopy, mArrowPaint);
            }
        }

        /**
         * 获取一个扇形的 Path
         *
         * @param path
         * @param centerX
         * @param centerY
         * @param r
         * @param startAngle
         * @param sweepAngle
         */
        private void calculateSectorClip(Path path, float centerX, float centerY, float r, float startAngle, float sweepAngle) {
            //获得一个三角形的剪裁区
            path.reset();
            path.moveTo(centerX, centerY);  //圆心
            path.lineTo((float) (centerX + r * Math.cos(startAngle / 180 * Math.PI)), (float) (centerY + r * Math.sin(startAngle / 180 * Math.PI)));
            path.lineTo((float) (centerX + r * Math.cos((startAngle + sweepAngle) / 180 * Math.PI)), (float) (centerY + r * Math.sin((startAngle + sweepAngle) / 180 * Math.PI)));
            path.close();
            //设置一个正方形，内切圆
            RectF rectF = new RectF(centerX - r, centerY - r, centerX + r, centerY + r);
            //获得弧形剪裁区的方法
            path.addArc(rectF, startAngle, sweepAngle);
        }

        private float[] calculateXY(RectF rect, float angle) {
            float cx = rect.centerX(); //圆角矩形中心点 X 坐标
            float cy = rect.centerY(); //圆角矩形中心点 Y 坐标
            double rx = rect.width() / 2;
            double ry = rect.height() / 2;
            double cc = (rect.width() - rect.height()) / 2d; //矩形中心点到两边的圆形中心点的距离，此处是 width > height 的情况下
            float[] result = new float[2];
            double radian = angle / 180 * Math.PI;

            double ra = Math.atan(ry / (rx - ry)) * 180 / Math.PI; //点刚好落在圆弧上时与水平方向的夹角

            if ((angle == 0)) { //右下方区域
                result[0] = rect.right;
                result[1] = cy;
            } else if ((angle > 0 && angle < ra)) { //右下方圆弧区域
                double d1 = cc * Math.cos(radian);
                double f = cc * Math.sin(radian);
                double d2 = Math.sqrt(ry * ry - f * f);
                double d = d1 + d2;
                result[0] = cx + (float) (d * Math.cos(radian));
                result[1] = cy + (float) (d * Math.sin(radian));
            } else if (angle >= ra && angle < 90) { //右下方
                result[0] = (float) (ry / Math.tan(radian)) + cx;
                result[1] = rect.bottom;
            } else if (angle == 90) {
                result[0] = cx;
                result[1] = rect.bottom;
            } else if (angle > 90 && angle <= (180 - ra)) { //左下方
                result[0] = cx - (float) (ry * Math.tan(radian - Math.PI / 2));
                result[1] = rect.bottom;
            } else if (angle > (180 - ra) && angle < 180) {  //左下方圆弧区域
                double d1 = cc * Math.cos(Math.PI - radian);
                double f = cc * Math.sin(Math.PI - radian);
                double d2 = Math.sqrt(ry * ry - f * f);
                double d = d1 + d2;
                result[0] = cx - (float) (d * Math.cos(Math.PI - radian));
                result[1] = cy + (float) (d * Math.sin(Math.PI - radian));
            } else if (angle == 180) {
                result[0] = rect.left;
                result[1] = cy;
            } else if (angle > 180 && angle < (180 + ra)) { //左上方圆弧区域
                double d1 = cc * Math.cos(radian - Math.PI);
                double f = cc * Math.sin(radian - Math.PI);
                double d2 = Math.sqrt(ry * ry - f * f);
                double d = d1 + d2;
                result[0] = cx - (float) (d * Math.cos(radian - Math.PI));
                result[1] = cy - (float) (d * Math.sin(radian - Math.PI));
            } else if (angle >= (180 + ra) && angle < 270) { //左上方
                result[0] = cx - (float) (ry * Math.tan(Math.PI * 3 / 2 - radian));
                result[1] = rect.top;
            } else if (angle == 270) {
                result[0] = cx;
                result[1] = rect.top;
            } else if (angle > 270 && angle <= (360 - ra)) { //右上方
                result[0] = cx + (float) (ry * Math.tan(radian - Math.PI * 3 / 2));
                result[1] = rect.top;
            } else { //右上方圆弧区域
                double d1 = cc * Math.cos(2 * Math.PI - radian);
                double f = cc * Math.sin(2 * Math.PI - radian);
                double d2 = Math.sqrt(ry * ry - f * f);
                double d = d1 + d2;
                result[0] = cx + (float) (d * Math.cos(2 * Math.PI - radian));
                result[1] = cy - (float) (d * Math.sin(2 * Math.PI - radian));
            }
            return result;
        }

        /**
         * Set the colors the progress spinner alternates between.
         *
         * @param colors Array of integers describing the colors. Must be non-<code>null</code>.
         */
        public void setColors(@NonNull int[] colors) {
            mColors = colors;
        }

        /**
         * @param animating
         */
        public void setAnimating(boolean animating) {
            mAnimating = animating;
        }

        /**
         * @param shape
         */
        public void setShape(Shape shape) {
            mShape = shape;
            if (mShape == Shape.ROUNDED_RECTANGLE) {
                mPath = new Path();
                mRoundRectPath = new Path();
                mRemainingPath = new Path();
                mProgressMeasure = new PathMeasure(mRoundRectPath, false);
            }
        }

        /**
         * Only works when hardware acceleration is turned off
         * {@link View#setLayerType(int, Paint)} setLayerType(View.LAYER_TYPE_SOFTWARE, null)
         *
         * @param radius
         * @param style
         */
        public void setBlurMaskFilter(float radius, BlurMaskFilter.Blur style) {
            mBlurMaskFilter = new BlurMaskFilter(radius, style);
        }

        /**
         * clear the BlurMaskFilter
         */
        public void clearBlurMaskFilter() {
            mBlurMaskFilter = null;
        }

        /**
         * @param radius
         * @param color
         */
        public void setShadowLayer(float radius, int color) {
            mShadowRadius = radius;
            mShadowColor = color;
        }

        /**
         * clear the ShadowLayer
         */
        public void clearShadowLayer() {
            mShadowRadius = -1;
            mShadowColor = -1;
        }

        /**
         * @param index Index into the color array of the color to display in
         *              the progress spinner.
         */
        public void setColorIndex(int index) {
            mColorIndex = index;
        }

        public void setColorFilter(ColorFilter filter) {
            mPaint.setColorFilter(filter);
            invalidateSelf();
        }

        /**
         * @param alpha Set the alpha of the progress spinner and associated arrowhead.
         */
        public void setAlpha(int alpha) {
            final int oldAlpha = mPaint.getAlpha();
            if (alpha != oldAlpha) {
                mPaint.setAlpha(alpha);
                invalidateSelf();
            }
        }

        /**
         * @return Current alpha of the progress spinner and arrowhead.
         */
        public int getAlpha() {
            return mPaint.getAlpha();
        }

        /**
         * @param strokeWidth Set the stroke width of the progress spinner in pixels.
         */
        public void setStrokeWidth(float strokeWidth) {
            mStrokeWidth = strokeWidth;
            mPaint.setStrokeWidth(strokeWidth);
            invalidateSelf();
        }

        @SuppressWarnings("unused")
        public float getStrokeWidth() {
            return mStrokeWidth;
        }

        @SuppressWarnings("unused")
        public void setStartTrim(float startTrim) {
            mStartTrim = startTrim;
            invalidateSelf();
        }

        @SuppressWarnings("unused")
        public float getStartTrim() {
            return mStartTrim;
        }

        public float getStartingStartTrim() {
            return mStartingStartTrim;
        }

        public float getStartingEndTrim() {
            return mStartingEndTrim;
        }

        @SuppressWarnings("unused")
        public void setEndTrim(float endTrim) {
            mEndTrim = endTrim;
            invalidateSelf();
        }

        @SuppressWarnings("unused")
        public float getEndTrim() {
            return mEndTrim;
        }

        @SuppressWarnings("unused")
        public void setRotation(float rotation) {
            mRotation = rotation;
            invalidateSelf();
        }

        @SuppressWarnings("unused")
        public float getRotation() {
            return mRotation;
        }

        public void setInsets(float insets) {
            mStrokeInset = insets;
        }

        @SuppressWarnings("unused")
        public float getInsets() {
            return mStrokeInset;
        }

        /**
         * @param screenDensity Logical screen density on which the progress
         *                      spinner is drawn.
         */
        public void setDensity(float screenDensity) {
            mDensity = screenDensity;
        }

        /**
         * @param innerRadius Inner radius in px of the circle the progress
         *                    spinner arc traces.
         */
        public void setInnerRadius(double innerRadius) {
            mRingInnerRadius = innerRadius;
        }

        /**
         * @param show Set to true to show the arrow head on the progress spinner.
         */
        public void setShowArrow(boolean show) {
            if (mShowArrow != show) {
                mShowArrow = show;
                invalidateSelf();
            }
        }

        /**
         * @param scale Set the scale of the arrowhead for the spinner.
         */
        public void setArrowScale(float scale) {
            if (scale != mArrowScale) {
                mArrowScale = scale;
                invalidateSelf();
            }
        }

        /**
         * @return The amount in degrees the progress spinner is currently rotated.
         */
        public float getStartingRotation() {
            return mStartingRotation;
        }

        /**
         * If the start / end trim are offset to begin with, store them so that
         * animation starts from that offset.
         */
        public void storeOriginals() {
            mStartingStartTrim = mStartTrim;
            mStartingEndTrim = mEndTrim;
            mStartingRotation = mRotation;
        }

        /**
         * Reset the progress spinner to default rotation, start and end angles.
         */
        public void resetOriginals() {
            mStartingStartTrim = 0;
            mStartingEndTrim = 0;
            mStartingRotation = 0;
            setStartTrim(0);
            setEndTrim(0);
            setRotation(0);
        }

        @SuppressWarnings("ConstantConditions")
        private void invalidateSelf() {
            mCallback.invalidateDrawable(null);
        }
    }

    /**
     * Squishes the interpolation curve into the second half of the animation.
     */
    private static class EndCurveInterpolator extends AccelerateDecelerateInterpolator {
        @Override
        public float getInterpolation(float input) {
            return super.getInterpolation(Math.max(0, (input - 0.5f) * 2.0f));
        }
    }

    /**
     * Squishes the interpolation curve into the first half of the animation.
     */
    private static class StartCurveInterpolator extends AccelerateDecelerateInterpolator {
        @Override
        public float getInterpolation(float input) {
            return super.getInterpolation(Math.min(1, input * 2.0f));
        }
    }
}
