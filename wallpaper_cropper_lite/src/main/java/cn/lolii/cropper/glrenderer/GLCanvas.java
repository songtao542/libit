package cn.lolii.cropper.glrenderer;

import android.graphics.Bitmap;
import android.graphics.RectF;

//
// GLCanvas gives a convenient interface to draw using OpenGL.
//
// When a rectangle is specified in this interface, it means the region
// [x, x+width) * [y, y+height)
//
public interface GLCanvas {

    GLId getGLId();

    // Tells GLCanvas the size of the underlying GL surface. This should be
    // called before first drawing and when the size of GL surface is changed.
    // This is called by GLRoot and should not be called by the clients
    // who only want to draw on the GLCanvas. Both width and height must be
    // nonnegative.
    void setSize(int width, int height);

    // Clear the drawing buffers. This should only be used by GLRoot.
    void clearBuffer();

    float getAlpha();

    void translate(float x, float y);

    void scale(float sx, float sy, float sz);

    void rotate(float angle, float x, float y, float z);

    // Same as save(), but only save those specified in saveFlags.
    void save(int saveFlags);

    int SAVE_FLAG_ALPHA = 0x01;
    int SAVE_FLAG_MATRIX = 0x02;

    // Pops from the top of the stack as current configuration state (matrix,
    // alpha, and clip). This call balances a previous call to save(), and is
    // used to remove all modifications to the configuration state since the
    // last save call.
    void restore();

    // Draws a texture to the specified rectangle.
    void drawTexture(
            BasicTexture texture, int x, int y, int width, int height);

    // Draws the source rectangle part of the texture to the target rectangle.
    void drawTexture(BasicTexture texture, RectF source, RectF target);

    // Unloads the specified texture from the canvas. The resource allocated
    // to draw the texture will be released. The specified texture will return
    // to the unloaded state. This function should be called only from
    // BasicTexture or its descendant
    boolean unloadTexture(BasicTexture texture);

    /**
     * Sets texture parameters to use GL_CLAMP_TO_EDGE for both
     * GL_TEXTURE_WRAP_S and GL_TEXTURE_WRAP_T. Sets texture parameters to be
     * GL_LINEAR for GL_TEXTURE_MIN_FILTER and GL_TEXTURE_MAG_FILTER.
     * bindTexture() must be called prior to this.
     *
     * @param texture The texture to set parameters on.
     */
    void setTextureParameters(BasicTexture texture);

    /**
     * Initializes the texture to a size by calling texImage2D on it.
     *
     * @param texture The texture to initialize the size.
     * @param format The texture format (e.g. GL_RGBA)
     * @param type The texture type (e.g. GL_UNSIGNED_BYTE)
     */
    void initializeTextureSize(BasicTexture texture, int format, int type);

    /**
     * Initializes the texture to a size by calling texImage2D on it.
     *
     * @param texture The texture to initialize the size.
     * @param bitmap The bitmap to initialize the bitmap with.
     */
    void initializeTexture(BasicTexture texture, Bitmap bitmap);

    /**
     * Calls glTexSubImage2D to upload a bitmap to the texture.
     *
     * @param texture The target texture to write to.
     * @param xOffset Specifies a texel offset in the x direction within the
     *            texture array.
     * @param yOffset Specifies a texel offset in the y direction within the
     *            texture array.
     * @param format The texture format (e.g. GL_RGBA)
     * @param type The texture type (e.g. GL_UNSIGNED_BYTE)
     */
    void texSubImage2D(BasicTexture texture, int xOffset, int yOffset,
                       Bitmap bitmap,
                       int format, int type);

    /**
     * Generates buffers and uploads the buffer data.
     *
     * @param buffer The buffer to upload
     * @return The buffer ID that was generated.
     */
    int uploadBuffer(java.nio.FloatBuffer buffer);

}
