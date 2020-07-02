package cn.lolii.cropper.glrenderer;


// Texture is a rectangular image which can be drawn on GLCanvas.
// The isOpaque() function gives a hint about whether the texture is opaque,
// so the drawing can be done faster.
//
// This is the current texture hierarchy:
//
// Texture
// -- ColorTexture
// -- FadeInTexture
// -- BasicTexture
//    -- UploadedTexture
//       -- BitmapTexture
//       -- Tile
//       -- ResourceTexture
//          -- NinePatchTexture
//       -- CanvasTexture
//          -- StringTexture
//
public interface Texture {
    int getWidth();

    int getHeight();

    void draw(GLCanvas canvas, int x, int y);

    void draw(GLCanvas canvas, int x, int y, int w, int h);

    boolean isOpaque();
}
