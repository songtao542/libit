package com.liabit.statebutton;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.AttrRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;

@SuppressWarnings("unused")
public abstract class StateButton extends FrameLayout {

    /**
     * 该命令是否支持 loading 状态
     */
    private boolean mSupportLoading = true;

    protected State state = State.OFF;

    private ImageView mIconView;

    enum State {
        OFF,
        ON,
        LOADING
    }


    public StateButton(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public StateButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public StateButton(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    public StateButton(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.state_button, this, true);
        setClickable(true);
        mIconView = findViewById(R.id.iconView);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.state_button, 0, 0);
            int icon = typedArray.getResourceId(R.styleable.state_button_icon, -1);
            if (icon != -1) {
                mIconView.setImageResource(icon);
            }
            float iconWidth = typedArray.getDimension(R.styleable.state_button_iconWidth, -1.0f);
            float iconHeight = typedArray.getDimension(R.styleable.state_button_iconHeight, -1.0f);
            if (iconWidth != -1.0f && iconHeight != -1.0f) {
                ViewGroup.LayoutParams layoutParams = mIconView.getLayoutParams();
                layoutParams.width = (int) iconWidth;
                layoutParams.height = (int) iconHeight;
                mIconView.setLayoutParams(layoutParams);
            }
            mSupportLoading = typedArray.getBoolean(R.styleable.state_button_supportLoading, true);
            typedArray.recycle();
        }
    }

    @Override
    public boolean performClick() {
        super.performClick();
        toggleState();
        return true;
    }

    private void toggleState() {
        //state == LOADING 表示命令发出了，必须等待命令的结果
        if (state == State.LOADING) {
            return;
        }
        State oldState = state;
        switch (state) {
            case OFF: {
                state = mSupportLoading ? State.LOADING : State.ON;
            }
            case ON: {
                state = mSupportLoading ? State.LOADING : State.OFF;
            }
        }
        handleState(oldState, state);
    }

    public void updateState(State newState) {
        State oldState = state;
        state = newState;
        handleState(oldState, state);
    }

    public void cancelLoading() {
        updateState(State.OFF);
    }

    public void setIcon(@DrawableRes int iconRes) {
        mIconView.setImageResource(iconRes);
    }

    public void setIconSize(float widthDp, float heightDp) {
        ViewGroup.LayoutParams layoutParams = mIconView.getLayoutParams();
        layoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widthDp, getResources().getDisplayMetrics());
        layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightDp, getResources().getDisplayMetrics());
        mIconView.setLayoutParams(layoutParams);
    }

    @SuppressWarnings("unused")
    public void setSupportLoading(boolean supportLoading) {
        mSupportLoading = supportLoading;
    }

    protected abstract void handleState(State oldState, State newState);

}
