package com.liabit.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.liabit.widget.MaterialProgressDrawable.Shape;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.core.content.ContextCompat;

import org.w3c.dom.Attr;

@SuppressWarnings("unused")
public class CircleStateButton extends StateButton {

    public CircleStateButton(@NonNull Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public CircleStateButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public CircleStateButton(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }


    public CircleStateButton(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private MaterialProgressDrawable mAnimatorDrawable = null;
    private GradientDrawable mGradientDrawable = null;
    private RippleDrawable mRippleDrawable = null;
    private float progressStrokeSize = 0;

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        Drawable backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.circle_state_button_background);
        int gradientStartColor = ContextCompat.getColor(context, R.color.state_button_gradient_start_color);
        int gradientEndColor = ContextCompat.getColor(context, R.color.state_button_gradient_end_color);
        int gradientAngle = 0;
        progressStrokeSize = context.getResources().getDimension(R.dimen.progress_stroke_size);
        int progressStrokeColor = ContextCompat.getColor(context, R.color.progress_color);
        float width = -2;
        float height = -2;
        if (attrs != null) {
            int[] wh = new int[]{android.R.attr.layout_width, android.R.attr.layout_height};
            TypedArray ta = context.obtainStyledAttributes(attrs, wh);
            width = ta.getLayoutDimension(0, -2);
            height = ta.getLayoutDimension(1, -2);
            ta.recycle();

            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleStateButton, defStyleAttr, defStyleRes);
            gradientStartColor = typedArray.getColor(R.styleable.CircleStateButton_gradientStartColor, gradientStartColor);
            gradientEndColor = typedArray.getColor(R.styleable.CircleStateButton_gradientEndColor, gradientEndColor);
            gradientAngle = typedArray.getInt(R.styleable.CircleStateButton_gradientAngle, gradientAngle);
            progressStrokeSize = typedArray.getDimension(R.styleable.CircleStateButton_progressStrokeSize, progressStrokeSize);
            progressStrokeColor = typedArray.getColor(R.styleable.CircleStateButton_progressStrokeColor, progressStrokeColor);
            typedArray.recycle();
        }
        float layoutSize = Math.min(width, height);
        final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        if (layoutSize < 0) {
            layoutSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64, metrics);
        }
        mAnimatorDrawable = new MaterialProgressDrawable(context, this, layoutSize, layoutSize, 0.0, progressStrokeSize, Shape.CIRCLE);
        mAnimatorDrawable.setColorSchemeColors(progressStrokeColor);
        mAnimatorDrawable.setBlurMaskFilter(20f, BlurMaskFilter.Blur.SOLID);
        //glowDrawable.setShadowLayer(20f, 0xffffff)
        if (backgroundDrawable instanceof LayerDrawable) {
            LayerDrawable layerDrawable = (LayerDrawable) backgroundDrawable;
            layerDrawable.setDrawableByLayerId(R.id.animator_layer, mAnimatorDrawable);
            for (int i = 0; i < layerDrawable.getNumberOfLayers(); i++) {
                Drawable layer = layerDrawable.getDrawable(i);
                if (layer instanceof GradientDrawable) {
                    mGradientDrawable = (GradientDrawable) layer;
                    mGradientDrawable.setColors(new int[]{gradientStartColor, gradientEndColor});
                    mGradientDrawable.setOrientation(getOrientation(gradientAngle));
                } else if (layer instanceof RippleDrawable) {
                    mRippleDrawable = (RippleDrawable) layer;
                }
            }
        }
        setClipToPadding(false);
        setClipChildren(false);
        setBackground(backgroundDrawable);
    }


    private GradientDrawable.Orientation getOrientation(int angle) {
        angle = ((angle % 360) + 360) % 360;
        switch (angle) {
            case 0:
                return GradientDrawable.Orientation.LEFT_RIGHT;
            case 45:
                return GradientDrawable.Orientation.BL_TR;
            case 90:
                return GradientDrawable.Orientation.BOTTOM_TOP;
            case 135:
                return GradientDrawable.Orientation.BR_TL;
            case 180:
                return GradientDrawable.Orientation.RIGHT_LEFT;
            case 225:
                return GradientDrawable.Orientation.TR_BL;
            case 270:
                return GradientDrawable.Orientation.TOP_BOTTOM;
            case 315:
                return GradientDrawable.Orientation.TL_BR;
        }
        return GradientDrawable.Orientation.TOP_BOTTOM;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int width = Math.abs(right - left);
        int height = Math.abs(bottom - top);
        setup(width, height);
    }

    private void setup(int width, int height) {
        if (mGradientDrawable != null) {
            int layoutSize = Math.min(width, height);
            double size = layoutSize - progressStrokeSize - 46;
            int gradientSize = (int) (size - progressStrokeSize * 1.5);
            if (width > 0 && height > 0) {
                mGradientDrawable.setSize(gradientSize, gradientSize);
            }
            mAnimatorDrawable.setup(size, size, 0.0, progressStrokeSize);
            if (mRippleDrawable != null) {
                LayerDrawable layerDrawable = mRippleDrawable;
                for (int i = 0; i < layerDrawable.getNumberOfLayers(); i++) {
                    Drawable layer = layerDrawable.getDrawable(i);
                    if (layer instanceof GradientDrawable) {
                        GradientDrawable gradient = (GradientDrawable) layer;
                        gradient.setSize(gradientSize, gradientSize);
                    }
                }
            }
        }
    }

    public void handleState(State oldState, State newState) {
        if (mAnimatorDrawable != null) {
            switch (newState) {
                case OFF: {
                    mAnimatorDrawable.setVisible(false);
                    break;
                }
                case ON: {
                    mAnimatorDrawable.setVisible(true);
                    mAnimatorDrawable.stop();
                    break;
                }
                case LOADING: {
                    mAnimatorDrawable.setVisible(true);
                    mAnimatorDrawable.start();
                    break;
                }
            }
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (state == State.LOADING && mAnimatorDrawable != null) {
            mAnimatorDrawable.start();
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (state == State.LOADING && mAnimatorDrawable != null) {
            mAnimatorDrawable.stop();
        }
    }

}
