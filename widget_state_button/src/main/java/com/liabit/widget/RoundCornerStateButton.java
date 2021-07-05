package com.liabit.widget;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.liabit.widget.MaterialProgressDrawable.Shape;
import com.liabit.widget.statebutton.R;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.core.content.ContextCompat;

@SuppressWarnings("unused")
public class RoundCornerStateButton extends StateButton {

    public RoundCornerStateButton(@NonNull Context context) {
        super(context);
        init(context);
    }

    public RoundCornerStateButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RoundCornerStateButton(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    public RoundCornerStateButton(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private MaterialProgressDrawable mAnimatorDrawable = null;

    private void init(Context context) {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        Drawable backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.round_corner_state_button_background);
        final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float layoutSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 64, metrics);
        float strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.2f, metrics);
        mAnimatorDrawable = new MaterialProgressDrawable(context, this, layoutSize, layoutSize, 0.0, strokeWidth, Shape.ROUNDED_RECTANGLE);
        int color = ContextCompat.getColor(context, R.color.progress_color);
        mAnimatorDrawable.setColorSchemeColors(color);
        mAnimatorDrawable.setBlurMaskFilter(20f, BlurMaskFilter.Blur.SOLID);
        //glowDrawable.setShadowLayer(20f, 0xffffff)
        if (backgroundDrawable instanceof LayerDrawable) {
            ((LayerDrawable) backgroundDrawable).setDrawableByLayerId(R.id.animator_layer, mAnimatorDrawable);
        }
        setBackground(backgroundDrawable);
    }

    public void handleState(State oldState, State newState) {
        if (mAnimatorDrawable != null) {
            switch (newState) {
                case OFF: {
                    mAnimatorDrawable.setVisible(false);
                }
                case ON: {
                    mAnimatorDrawable.setVisible(true);
                    mAnimatorDrawable.stop();
                }
                case LOADING: {
                    Log.d("TTTT", "handleState loading");
                    mAnimatorDrawable.setVisible(true);
                    mAnimatorDrawable.start();
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
