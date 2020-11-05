package com.liabit.cropper.glrenderer;

import com.liabit.cropper.util.Utils;

public class GLPaint {
    private float mLineWidth = 1f;
    private int mColor = 0;

    public void setColor(int color) {
        mColor = color;
    }

    public int getColor() {
        return mColor;
    }

    public void setLineWidth(float width) {
        Utils.assertTrue(width >= 0);
        mLineWidth = width;
    }

    public float getLineWidth() {
        return mLineWidth;
    }
}
