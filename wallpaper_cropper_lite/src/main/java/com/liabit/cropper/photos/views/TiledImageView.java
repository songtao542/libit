package com.liabit.cropper.photos.views;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Choreographer;
import android.view.Choreographer.FrameCallback;
import android.view.View;
import android.widget.FrameLayout;

import com.liabit.cropper.glrenderer.BasicTexture;
import com.liabit.cropper.glrenderer.GLES20Canvas;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Shows an image using {@link TiledImageRenderer} using either {@link GLSurfaceView}
 * or {@link BlockingGLTextureView}.
 */
public class TiledImageView extends FrameLayout {

    private static final boolean USE_TEXTURE_VIEW = false;
    @SuppressLint("ObsoleteSdkInt")
    private static final boolean IS_SUPPORTED = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    @SuppressLint("ObsoleteSdkInt")
    private static final boolean USE_CHOREOGRAPHER = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;

    private BlockingGLTextureView mTextureView;
    private GLSurfaceView mGLSurfaceView;
    private boolean mInvalPending = false;
    private FrameCallback mFrameCallback;

    protected static class ImageRendererWrapper {
        // Guarded by locks
        public float scale;
        public int centerX, centerY;
        public int rotation;
        public TiledImageRenderer.TileSource source;
        Runnable isReadyCallback;

        // GL thread only
        TiledImageRenderer image;
    }

    // -------------------------
    // Guarded by mLock
    // -------------------------
    protected final Object mLock = new Object();
    protected ImageRendererWrapper mRenderer;

    public TiledImageView(Context context) {
        this(context, null);
    }

    public TiledImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!IS_SUPPORTED) {
            return;
        }

        mRenderer = new ImageRendererWrapper();
        mRenderer.image = new TiledImageRenderer(this);
        View view;
        if (USE_TEXTURE_VIEW) {
            mTextureView = new BlockingGLTextureView(context);
            mTextureView.setRenderer(new TileRenderer());
            view = mTextureView;
        } else {
            mGLSurfaceView = new GLSurfaceView(context);
            mGLSurfaceView.setEGLContextClientVersion(2);
            mGLSurfaceView.setRenderer(new TileRenderer());
            mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
            view = mGLSurfaceView;
        }
        addView(view, new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        //setTileSource(new ColoredTiles());
    }

    public void destroy() {
        if (!IS_SUPPORTED) {
            return;
        }
        if (USE_TEXTURE_VIEW) {
            mTextureView.destroy();
        } else {
            mGLSurfaceView.queueEvent(mFreeTextures);
        }
    }

    private final Runnable mFreeTextures = new Runnable() {

        @Override
        public void run() {
            mRenderer.image.freeTextures();
        }
    };

    public void setTileSource(TiledImageRenderer.TileSource source, Runnable isReadyCallback) {
        if (!IS_SUPPORTED) {
            return;
        }
        synchronized (mLock) {
            mRenderer.source = source;
            mRenderer.isReadyCallback = isReadyCallback;
            mRenderer.centerX = source != null ? source.getImageWidth() / 2 : 0;
            mRenderer.centerY = source != null ? source.getImageHeight() / 2 : 0;
            mRenderer.rotation = source != null ? source.getRotation() : 0;
            mRenderer.scale = 0;
            updateScaleIfNecessaryLocked(mRenderer);
        }
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!IS_SUPPORTED) {
            return;
        }
        synchronized (mLock) {
            updateScaleIfNecessaryLocked(mRenderer);
        }
    }

    private void updateScaleIfNecessaryLocked(ImageRendererWrapper renderer) {
        if (renderer == null || renderer.source == null
                || renderer.scale > 0 || getWidth() == 0) {
            return;
        }
        renderer.scale = Math.min(
                (float) getWidth() / (float) renderer.source.getImageWidth(),
                (float) getHeight() / (float) renderer.source.getImageHeight());
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (!IS_SUPPORTED) {
            return;
        }
        if (USE_TEXTURE_VIEW) {
            mTextureView.render();
        }
        super.dispatchDraw(canvas);
    }

    @SuppressLint("NewApi")
    @Override
    public void setTranslationX(float translationX) {
        if (!IS_SUPPORTED) {
            return;
        }
        super.setTranslationX(translationX);
    }

    @Override
    public void invalidate() {
        if (!IS_SUPPORTED) {
            return;
        }
        if (USE_TEXTURE_VIEW) {
            super.invalidate();
            mTextureView.invalidate();
        } else {
            if (USE_CHOREOGRAPHER) {
                invalOnVsync();
            } else {
                mGLSurfaceView.requestRender();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void invalOnVsync() {
        if (!mInvalPending) {
            mInvalPending = true;
            if (mFrameCallback == null) {
                mFrameCallback = frameTimeNanos -> {
                    mInvalPending = false;
                    mGLSurfaceView.requestRender();
                };
            }
            Choreographer.getInstance().postFrameCallback(mFrameCallback);
        }
    }

    private class TileRenderer implements Renderer {

        private GLES20Canvas mCanvas;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            mCanvas = new GLES20Canvas();
            BasicTexture.invalidateAllTextures();
            mRenderer.image.setModel(mRenderer.source, mRenderer.rotation);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            mCanvas.setSize(width, height);
            mRenderer.image.setViewSize(width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            mCanvas.clearBuffer();
            Runnable readyCallback;
            synchronized (mLock) {
                readyCallback = mRenderer.isReadyCallback;
                mRenderer.image.setModel(mRenderer.source, mRenderer.rotation);
                mRenderer.image.setPosition(mRenderer.centerX, mRenderer.centerY,
                        mRenderer.scale);
            }
            boolean complete = mRenderer.image.draw(mCanvas);
            if (complete && readyCallback != null) {
                synchronized (mLock) {
                    // Make sure we don't trample on a newly set callback/source
                    // if it changed while we were rendering
                    if (mRenderer.isReadyCallback == readyCallback) {
                        mRenderer.isReadyCallback = null;
                    }
                }
                post(readyCallback);
            }
        }

    }

}
