package com.liabit.cropper.glrenderer;


// Texture is a rectangular image which can be drawn on GLCanvas.
// The isOpaque() function gives a hint about whether the texture is opaque,
// so the drawing can be done faster.
public interface Texture {
    int getWidth();

    int getHeight();

    void draw(GLCanvas canvas, int x, int y, int w, int h);

    boolean isOpaque();
}
