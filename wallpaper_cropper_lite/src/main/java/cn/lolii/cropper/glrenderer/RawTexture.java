package cn.lolii.cropper.glrenderer;

import android.util.Log;

import javax.microedition.khronos.opengles.GL11;

public class RawTexture extends BasicTexture {
    private static final String TAG = "RawTexture";

    private final boolean mOpaque;

    public RawTexture(int width, int height, boolean opaque) {
        mOpaque = opaque;
        setSize(width, height);
    }

    @Override
    public boolean isOpaque() {
        return mOpaque;
    }

    @Override
    public boolean isFlippedVertically() {
        return false;
    }

    @Override
    protected boolean onBind(GLCanvas canvas) {
        if (isLoaded()) return true;
        Log.w(TAG, "lost the content due to context change");
        return false;
    }

    @Override
    protected int getTarget() {
        return GL11.GL_TEXTURE_2D;
    }
}
