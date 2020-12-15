package com.liabit.color;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

public class Color {
    private final float[] mComponents;

    private Color(float r, float g, float b, float a) {
        mComponents = new float[]{r, g, b, a};
    }

    @NonNull
    public static Color valueOf(float r, float g, float b, float a) {
        return new Color(saturate(r), saturate(g), saturate(b), saturate(a));
    }

    @NonNull
    public static Color valueOf(@ColorInt int color) {
        float r = ((color >> 16) & 0xff) / 255.0f;
        float g = ((color >> 8) & 0xff) / 255.0f;
        float b = ((color) & 0xff) / 255.0f;
        float a = ((color >> 24) & 0xff) / 255.0f;
        return new Color(r, g, b, a);
    }

    private static float saturate(float v) {
        return v <= 0.0f ? 0.0f : (Math.min(v, 1.0f));
    }

    public float red() {
        return mComponents[0];
    }

    public float green() {
        return mComponents[1];
    }

    public float blue() {
        return mComponents[2];
    }

    public float alpha() {
        return mComponents[3];
    }

    public int toArgb() {
        return ((int) (mComponents[3] * 255.0f + 0.5f) << 24) |
                ((int) (mComponents[0] * 255.0f + 0.5f) << 16) |
                ((int) (mComponents[1] * 255.0f + 0.5f) << 8) |
                (int) (mComponents[2] * 255.0f + 0.5f);
    }


    public static float red(int color) {
        return ((color >> 16) & 0xff) / 255.0f;
    }

    public static float green(int color) {
        return ((color >> 8) & 0xff) / 255.0f;
    }

    public static float blue(int color) {
        return ((color) & 0xff) / 255.0f;
    }

    public static float alpha(int color) {
        return ((color >> 24) & 0xff) / 255.0f;
    }

    public static int toArgb(float r, float g, float b, float a) {
        return ((int) (a * 255.0f + 0.5f) << 24) |
                ((int) (r * 255.0f + 0.5f) << 16) |
                ((int) (g * 255.0f + 0.5f) << 8) |
                (int) (b * 255.0f + 0.5f);
    }

}