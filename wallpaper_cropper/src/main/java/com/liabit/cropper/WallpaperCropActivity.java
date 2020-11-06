package com.liabit.cropper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.liabit.cropper.util.Utils;
import com.liabit.cropper.exif.ExifInterface;
import com.liabit.cropper.photos.BitmapRegionTileSource;
import com.liabit.cropper.photos.BitmapRegionTileSource.BitmapSource;
import com.liabit.cropper.R;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static android.app.WallpaperManager.FLAG_LOCK;
import static android.app.WallpaperManager.FLAG_SYSTEM;

public class WallpaperCropActivity extends AppCompatActivity {
    private static final String TAG = "WallpaperCropActivity";

    protected static final String WALLPAPER_WIDTH_KEY = "wallpaper.width";
    protected static final String WALLPAPER_HEIGHT_KEY = "wallpaper.height";
    private static final int DEFAULT_COMPRESS_QUALITY = 90;
    /**
     * The maximum bitmap size we allow to be returned through the intent.
     * Intents have a maximum of 1MB in total size. However, the Bitmap seems to
     * have some overhead to hit so that we go way below the limit here to make
     * sure the intent stays below 1MB.We should consider just returning a byte
     * array instead of a Bitmap instance to avoid overhead.
     */
    public static final int MAX_BMAP_IN_INTENT = 750000;
    private static final float WALLPAPER_SCREENS_SPAN = 1f;

    protected static Point sDefaultWallpaperSize;

    protected CropView mCropView;
    protected Uri mUri;
    private View mSetWallpaperButton;

    private boolean mApplyMenuVisible = false;
    private boolean mOptionViewVisible = true;

    private FrameLayout mToolbarWrapper;
    private Toolbar mToolbar;
    private LinearLayout mApplyMenuWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        if (!enableRotation()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    protected void init() {
        setContentView(R.layout.wallpaper_cropper);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }

        mCropView = findViewById(R.id.cropView);

        mToolbarWrapper = findViewById(R.id.toolbarWrapper);
        mToolbar = findViewById(R.id.toolbar);
        mApplyMenuWrapper = findViewById(R.id.applyMenuWrapper);

        showSystemUI(false, null);

        Intent cropIntent = getIntent();
        final Uri imageUri = cropIntent.getData();

        if (imageUri == null) {
            Log.e(TAG, "No URI passed in intent, exiting WallpaperCropActivity");
            finish();
            return;
        }

        setSupportActionBar(mToolbar);

        findViewById(R.id.cancel).setOnClickListener(v -> toggleApplyMenu());
        mCropView.setTouchCallback(new CropView.TouchCallback() {
            @Override
            public void onTouchDown() {
            }

            @Override
            public void onTap() {
                toggleOptionView();
            }

            @Override
            public void onTouchUp() {
            }
        });

        findViewById(R.id.applyLock).setOnClickListener(v -> {
            toggleApplyMenu();
            cropImageAndSetWallpaper(imageUri, null, true, FLAG_LOCK);
        });
        findViewById(R.id.applyHomeScreen).setOnClickListener(v -> {
            toggleApplyMenu();
            cropImageAndSetWallpaper(imageUri, null, true, FLAG_SYSTEM);
        });
        findViewById(R.id.applyAll).setOnClickListener(v -> {
            toggleApplyMenu();
            cropImageAndSetWallpaper(imageUri, null, true, FLAG_SYSTEM | FLAG_LOCK);
        });

        mSetWallpaperButton = findViewById(R.id.set_wallpaper_button);
        int navigationBarHeight = getNavigationBarHeight(WallpaperCropActivity.this);
        FrameLayout.LayoutParams btnLayoutParams = (FrameLayout.LayoutParams) mSetWallpaperButton.getLayoutParams();
        btnLayoutParams.setMargins(0, 0, 0, dp2px(43) + navigationBarHeight);
        mSetWallpaperButton.setOnClickListener(v -> toggleApplyMenu());

        // Load image in background
        final BitmapRegionTileSource.UriBitmapSource bitmapSource =
                new BitmapRegionTileSource.UriBitmapSource(this, imageUri, 1024);
        mSetWallpaperButton.setVisibility(View.INVISIBLE);
        Runnable onLoad = () -> {
            if (bitmapSource.getLoadingState() != BitmapSource.State.LOADED) {
                Toast.makeText(WallpaperCropActivity.this,
                        getString(R.string.wallpaper_load_fail),
                        Toast.LENGTH_LONG).show();
                finish();
            } else {
                mSetWallpaperButton.setVisibility(View.VISIBLE);
                if (navigationBarHeight == 0) {
                    mSetWallpaperButton.requestLayout();
                }
            }
        };
        setCropViewTileSource(bitmapSource, true, false, onLoad);
    }

    private void hideSystemUI(Boolean lightStatusBar, Boolean lightNavigationBar) {
        Window window = getWindow();
        int flag = window.getDecorView().getSystemUiVisibility();
        boolean lightStatus = lightStatusBar != null ? lightStatusBar : (flag & View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) != 0;
        boolean lightNavigation = lightNavigationBar != null ? lightNavigationBar : (flag & 16) != 0;
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        if (lightStatus) {
            flags = flags | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        if (lightNavigation) {
            flags = flags | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        }
        window.setNavigationBarColor(Color.TRANSPARENT);
        window.getDecorView().setSystemUiVisibility(flags);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        window.setAttributes(lp);
    }

    private void showSystemUI(Boolean lightStatusBar, Boolean lightNavigationBar) {
        Window window = getWindow();
        int flag = window.getDecorView().getSystemUiVisibility();
        boolean lightStatus = lightStatusBar != null ? lightStatusBar : (flag & View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) != 0;
        boolean lightNavigation = lightNavigationBar != null ? lightNavigationBar : (flag & 16) != 0;
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        if (lightStatus) {
            flags = flags | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        if (lightNavigation) {
            flags = flags | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        }
        window.setNavigationBarColor(Color.TRANSPARENT);
        window.getDecorView().setSystemUiVisibility(flags);
    }

    private void toggleOptionView() {
        if (mApplyMenuVisible) {
            toggleApplyMenu();
        } else if (mOptionViewVisible) {
            mOptionViewVisible = false;
            hideSystemUI(null, null);
            mToolbarWrapper.animate().translationY(-mToolbarWrapper.getHeight()).start();
            if (mApplyMenuVisible) {
                toggleApplyMenu();
            } else {
                float marginBottom = getResources().getDimension(R.dimen.apply_wallpaper_button_margin_bottom);
                mSetWallpaperButton.animate().translationY(mSetWallpaperButton.getHeight() + marginBottom)
                        .start();
            }
        } else {
            mOptionViewVisible = true;
            showSystemUI(false, null);
            mToolbarWrapper.animate().translationY(0f).start();
            mSetWallpaperButton.animate().translationY(0f).start();
        }
    }

    private void toggleApplyMenu() {
        mApplyMenuWrapper.setVisibility(View.VISIBLE);
        if (mApplyMenuVisible) {
            mApplyMenuVisible = false;
            ViewGroup.LayoutParams lp = mApplyMenuWrapper.getLayoutParams();
            float bottomMargin = 0;
            if (lp instanceof ViewGroup.MarginLayoutParams) {
                bottomMargin = ((ViewGroup.MarginLayoutParams) lp).bottomMargin;
            }
            mApplyMenuWrapper.animate().translationY(mApplyMenuWrapper.getHeight() + bottomMargin).start();
        } else {
            mApplyMenuVisible = true;
            float navigationBarHeight = 0;
            if (isNavigationBarShow()) {
                navigationBarHeight = getNavigationBarHeight(this);
            }
            float bottomMargin = navigationBarHeight + dp2px(13);
            ViewGroup.LayoutParams lp = mApplyMenuWrapper.getLayoutParams();
            if (lp instanceof ViewGroup.MarginLayoutParams) {
                ((ViewGroup.MarginLayoutParams) lp).bottomMargin = (int) bottomMargin;
                mApplyMenuWrapper.setLayoutParams(lp);
            }
            mApplyMenuWrapper.animate().translationY(0f).start();
        }
    }

    private boolean isNavigationBarShow() {
        boolean hasNavigationBar;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            WindowManager windowManager = getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            hasNavigationBar = realSize.y != size.y;
        } else {
            hasNavigationBar = !(ViewConfiguration.get(this).hasPermanentMenuKey()
                    || KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK));
        }
        int systemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
        boolean show = (systemUiVisibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0;
        Log.d(TAG, "has navigation bar=$hasNavigationBar   navigation bar is show=$show");
        return hasNavigationBar && show;
    }

    @Override
    protected void onDestroy() {
        if (mCropView != null) {
            mCropView.destroy();
        }
        super.onDestroy();
    }

    public void setCropViewTileSource(
            final BitmapRegionTileSource.BitmapSource bitmapSource, final boolean touchEnabled,
            final boolean moveToLeft, final Runnable postExecute) {
        final Context context = WallpaperCropActivity.this;
        final View progressView = findViewById(R.id.loading);
        final AsyncTask<Void, Void, Void> loadBitmapTask = new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... args) {
                if (!isCancelled()) {
                    try {
                        bitmapSource.loadInBackground();
                    } catch (SecurityException securityException) {
                        if (isDestroyed()) {
                            // Temporarily granted permissions are revoked when the activity
                            // finishes, potentially resulting in a SecurityException here.
                            // Even though {@link #isDestroyed} might also return true in different
                            // situations where the configuration changes, we are fine with
                            // catching these cases here as well.
                            cancel(false);
                        } else {
                            // otherwise it had a different cause and we throw it further
                            throw securityException;
                        }
                    }
                }
                return null;
            }

            protected void onPostExecute(Void arg) {
                if (!isCancelled()) {
                    progressView.setVisibility(View.INVISIBLE);
                    if (bitmapSource.getLoadingState() == BitmapSource.State.LOADED) {
                        mCropView.setTileSource(
                                new BitmapRegionTileSource(context, bitmapSource), null);
                        mCropView.setTouchEnabled(touchEnabled);
                        if (moveToLeft) {
                            mCropView.moveToLeft();
                        }
                    }
                }
                if (postExecute != null) {
                    postExecute.run();
                }
            }
        };
        // We don't want to show the spinner every time we load an image, because that would be
        // annoying; instead, only start showing the spinner if loading the image has taken
        // longer than 1 sec (ie 1000 ms)
        progressView.postDelayed(new Runnable() {
            public void run() {
                if (loadBitmapTask.getStatus() != AsyncTask.Status.FINISHED) {
                    progressView.setVisibility(View.VISIBLE);
                }
            }
        }, 1000);
        loadBitmapTask.execute();
    }

    public boolean enableRotation() {
        return getResources().getBoolean(R.bool.allow_rotation);
    }

    public static String getSharedPreferencesKey() {
        return WallpaperCropActivity.class.getName();
    }

    // As a ratio of screen height, the total distance we want the parallax effect to span
    // horizontally
    private static float wallpaperTravelToScreenWidthRatio(int width, int height) {
        float aspectRatio = width / (float) height;

        // At an aspect ratio of 16/10, the wallpaper parallax effect should span 1.5 * screen width
        // At an aspect ratio of 10/16, the wallpaper parallax effect should span 1.2 * screen width
        // We will use these two data points to extrapolate how much the wallpaper parallax effect
        // to span (ie travel) at any aspect ratio:

        final float ASPECT_RATIO_LANDSCAPE = 16 / 10f;
        final float ASPECT_RATIO_PORTRAIT = 10 / 16f;
        final float WALLPAPER_WIDTH_TO_SCREEN_RATIO_LANDSCAPE = 1.5f;
        final float WALLPAPER_WIDTH_TO_SCREEN_RATIO_PORTRAIT = 1.2f;

        // To find out the desired width at different aspect ratios, we use the following two
        // formulas, where the coefficient on x is the aspect ratio (width/height):
        //   (16/10)x + y = 1.5
        //   (10/16)x + y = 1.2
        // We solve for x and y and end up with a final formula:
        final float x =
                (WALLPAPER_WIDTH_TO_SCREEN_RATIO_LANDSCAPE - WALLPAPER_WIDTH_TO_SCREEN_RATIO_PORTRAIT) /
                        (ASPECT_RATIO_LANDSCAPE - ASPECT_RATIO_PORTRAIT);
        final float y = WALLPAPER_WIDTH_TO_SCREEN_RATIO_PORTRAIT - x * ASPECT_RATIO_PORTRAIT;
        return x * aspectRatio + y;
    }

    static protected Point getDefaultWallpaperSize(Resources res, WindowManager windowManager) {
        if (sDefaultWallpaperSize == null) {
            Point minDims = new Point();
            Point maxDims = new Point();
            windowManager.getDefaultDisplay().getCurrentSizeRange(minDims, maxDims);

            int maxDim = Math.max(maxDims.x, maxDims.y);
            int minDim = Math.max(minDims.x, minDims.y);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                Point realSize = new Point();
                windowManager.getDefaultDisplay().getRealSize(realSize);
                maxDim = Math.max(realSize.x, realSize.y);
                minDim = Math.min(realSize.x, realSize.y);
            }

            // We need to ensure that there is enough extra space in the wallpaper
            // for the intended parallax effects
            final int defaultWidth, defaultHeight;
            if (isScreenLarge(res)) {
                defaultWidth = (int) (maxDim * wallpaperTravelToScreenWidthRatio(maxDim, minDim));
                defaultHeight = maxDim;
            } else {
                defaultWidth = Math.min((int) (minDim * WALLPAPER_SCREENS_SPAN), maxDim);
                defaultHeight = maxDim;
            }
            sDefaultWallpaperSize = new Point(defaultWidth, defaultHeight);
        }
        return sDefaultWallpaperSize;
    }

    public static int getRotationFromExif(String path) {
        return getRotationFromExifHelper(path, null, 0, null, null);
    }

    public static int getRotationFromExif(Context context, Uri uri) {
        return getRotationFromExifHelper(null, null, 0, context, uri);
    }

    public static int getRotationFromExif(Resources res, int resId) {
        return getRotationFromExifHelper(null, res, resId, null, null);
    }

    private static int getRotationFromExifHelper(
            String path, Resources res, int resId, Context context, Uri uri) {
        ExifInterface ei = new ExifInterface();
        InputStream is = null;
        BufferedInputStream bis = null;
        try {
            if (path != null) {
                ei.readExif(path);
            } else if (uri != null) {
                is = context.getContentResolver().openInputStream(uri);
                bis = new BufferedInputStream(is);
                ei.readExif(bis);
            } else {
                is = res.openRawResource(resId);
                bis = new BufferedInputStream(is);
                ei.readExif(bis);
            }
            Integer ori = ei.getTagIntValue(ExifInterface.TAG_ORIENTATION);
            if (ori != null) {
                return ExifInterface.getRotationForOrientationValue(ori.shortValue());
            }
        } catch (IOException | NullPointerException e) { // Sometimes the ExifInterface has an internal NPE if Exif data isn't valid
            Log.w(TAG, "Getting exif data failed", e);
        } finally {
            Utils.closeSilently(bis);
            Utils.closeSilently(is);
        }
        return 0;
    }

    private static boolean isScreenLarge(Resources res) {
        Configuration config = res.getConfiguration();
        return config.smallestScreenWidthDp >= 720;
    }

    protected void cropImageAndSetWallpaper(Uri uri, OnBitmapCroppedHandler onBitmapCroppedHandler,
                                            final boolean finishActivityWhenDone, int setType) {
        boolean centerCrop = getResources().getBoolean(R.bool.center_crop);
        boolean ltr = mCropView.getLayoutDirection() == View.LAYOUT_DIRECTION_LTR;

        Display d = getWindowManager().getDefaultDisplay();

        Point displaySize = new Point();
        d.getSize(displaySize);
        boolean isPortrait = displaySize.x < displaySize.y;

        Point defaultWallpaperSize = getDefaultWallpaperSize(getResources(),
                getWindowManager());
        // Get the crop
        RectF cropRect = mCropView.getCrop();

        Point inSize = mCropView.getSourceDimensions();

        int cropRotation = mCropView.getImageRotation();
        float cropScale = mCropView.getWidth() / cropRect.width();

        Matrix rotateMatrix = new Matrix();
        rotateMatrix.setRotate(cropRotation);
        float[] rotatedInSize = new float[]{inSize.x, inSize.y};
        rotateMatrix.mapPoints(rotatedInSize);
        rotatedInSize[0] = Math.abs(rotatedInSize[0]);
        rotatedInSize[1] = Math.abs(rotatedInSize[1]);

        // Due to rounding errors in the cropview renderer the edges can be slightly offset
        // therefore we ensure that the boundaries are sanely defined
        cropRect.left = Math.max(0, cropRect.left);
        cropRect.right = Math.min(rotatedInSize[0], cropRect.right);
        cropRect.top = Math.max(0, cropRect.top);
        cropRect.bottom = Math.min(rotatedInSize[1], cropRect.bottom);

        // ADJUST CROP WIDTH
        // Extend the crop all the way to the right, for parallax
        // (or all the way to the left, in RTL)
        float extraSpace;
        if (centerCrop) {
            extraSpace = 2f * Math.min(rotatedInSize[0] - cropRect.right, cropRect.left);
        } else {
            extraSpace = ltr ? rotatedInSize[0] - cropRect.right : cropRect.left;
        }
        // Cap the amount of extra width
        float maxExtraSpace = defaultWallpaperSize.x / cropScale - cropRect.width();
        extraSpace = Math.min(extraSpace, maxExtraSpace);

        if (centerCrop) {
            cropRect.left -= extraSpace / 2f;
            cropRect.right += extraSpace / 2f;
        } else {
            if (ltr) {
                cropRect.right += extraSpace;
            } else {
                cropRect.left -= extraSpace;
            }
        }

        // ADJUST CROP HEIGHT
        if (isPortrait) {
            cropRect.bottom = cropRect.top + defaultWallpaperSize.y / cropScale;
        } else { // LANDSCAPE
            float extraPortraitHeight =
                    defaultWallpaperSize.y / cropScale - cropRect.height();
            float expandHeight =
                    Math.min(Math.min(rotatedInSize[1] - cropRect.bottom, cropRect.top),
                            extraPortraitHeight / 2);
            cropRect.top -= expandHeight;
            cropRect.bottom += expandHeight;
        }
        final int outWidth = Math.round(cropRect.width() * cropScale);
        final int outHeight = Math.round(cropRect.height() * cropScale);

        Runnable onEndCrop = () -> {
            if (FLAG_SYSTEM == setType) {
                Toast.makeText(WallpaperCropActivity.this,
                        getString(R.string.desktop_set_success),
                        Toast.LENGTH_SHORT).show();
            } else if (FLAG_LOCK == setType) {
                Toast.makeText(WallpaperCropActivity.this,
                        getString(R.string.lock_set_success),
                        Toast.LENGTH_SHORT).show();
            } else if ((FLAG_SYSTEM | FLAG_LOCK) == setType) {
                Toast.makeText(WallpaperCropActivity.this,
                        getString(R.string.simultaneous_set_success),
                        Toast.LENGTH_SHORT).show();
            }
            if (finishActivityWhenDone) {
                Log.d(TAG, "run: setFlag= " + setType);
                setResult(Activity.RESULT_OK);
                finish();
            }
        };
        BitmapCropTask cropTask = new BitmapCropTask(this, uri,
                cropRect, cropRotation, outWidth, outHeight, true, false, onEndCrop, setType);
        if (onBitmapCroppedHandler != null) {
            cropTask.setOnBitmapCropped(onBitmapCroppedHandler);
        }
        cropTask.execute();
    }

    public interface OnBitmapCroppedHandler {
        void onBitmapCropped(byte[] imageBytes);
    }

    protected static class BitmapCropTask extends AsyncTask<Void, Void, Boolean> {
        Uri mInUri = null;
        Context mContext;
        String mInFilePath;
        byte[] mInImageBytes;
        int mInResId = 0;
        RectF mCropBounds = null;
        int mOutWidth, mOutHeight;
        int mRotation;
        String mOutputFormat = "jpg"; // for now
        boolean mSetWallpaper;
        boolean mSaveCroppedBitmap;
        Bitmap mCroppedBitmap;
        Runnable mOnEndRunnable;
        Resources mResources;
        OnBitmapCroppedHandler mOnBitmapCroppedHandler;
        boolean mNoCrop;
        int mSetType;

        public BitmapCropTask(Context c, String filePath,
                              RectF cropBounds, int rotation, int outWidth, int outHeight,
                              boolean setWallpaper, boolean saveCroppedBitmap, Runnable onEndRunnable, int setType) {
            mContext = c;
            mInFilePath = filePath;
            init(cropBounds, rotation,
                    outWidth, outHeight, setWallpaper, saveCroppedBitmap, onEndRunnable, setType);
        }

        public BitmapCropTask(byte[] imageBytes,
                              RectF cropBounds, int rotation, int outWidth, int outHeight,
                              boolean setWallpaper, boolean saveCroppedBitmap, Runnable onEndRunnable, int setType) {
            mInImageBytes = imageBytes;
            init(cropBounds, rotation,
                    outWidth, outHeight, setWallpaper, saveCroppedBitmap, onEndRunnable, setType);
        }

        public BitmapCropTask(Context c, Uri inUri,
                              RectF cropBounds, int rotation, int outWidth, int outHeight,
                              boolean setWallpaper, boolean saveCroppedBitmap, Runnable onEndRunnable, int setType) {
            mContext = c;
            mInUri = inUri;
            init(cropBounds, rotation,
                    outWidth, outHeight, setWallpaper, saveCroppedBitmap, onEndRunnable, setType);
        }

        public BitmapCropTask(Context c, Resources res, int inResId,
                              RectF cropBounds, int rotation, int outWidth, int outHeight,
                              boolean setWallpaper, boolean saveCroppedBitmap, Runnable onEndRunnable, int setType) {
            mContext = c;
            mInResId = inResId;
            mResources = res;
            init(cropBounds, rotation,
                    outWidth, outHeight, setWallpaper, saveCroppedBitmap, onEndRunnable, setType);
        }

        private void init(RectF cropBounds, int rotation, int outWidth, int outHeight,
                          boolean setWallpaper, boolean saveCroppedBitmap, Runnable onEndRunnable, int setType) {
            mCropBounds = cropBounds;
            mRotation = rotation;
            mOutWidth = outWidth;
            mOutHeight = outHeight;
            mSetWallpaper = setWallpaper;
            mSaveCroppedBitmap = saveCroppedBitmap;
            mOnEndRunnable = onEndRunnable;
            mSetType = setType;
        }

        public void setOnBitmapCropped(OnBitmapCroppedHandler handler) {
            mOnBitmapCroppedHandler = handler;
        }

        public void setNoCrop(boolean value) {
            mNoCrop = value;
        }

        public void setOnEndRunnable(Runnable onEndRunnable) {
            mOnEndRunnable = onEndRunnable;
        }

        // Helper to setup input stream
        private InputStream regenerateInputStream() {
            if (mInUri == null && mInResId == 0 && mInFilePath == null && mInImageBytes == null) {
                Log.w(TAG, "cannot read original file, no input URI, resource ID, or " +
                        "image byte array given");
            } else {
                try {
                    if (mInUri != null) {
                        return new BufferedInputStream(
                                mContext.getContentResolver().openInputStream(mInUri));
                    } else if (mInFilePath != null) {
                        return mContext.openFileInput(mInFilePath);
                    } else if (mInImageBytes != null) {
                        return new BufferedInputStream(new ByteArrayInputStream(mInImageBytes));
                    } else {
                        return new BufferedInputStream(mResources.openRawResource(mInResId));
                    }
                } catch (FileNotFoundException e) {
                    Log.w(TAG, "cannot read file: " + mInUri.toString(), e);
                } catch (SecurityException e) {
                    Log.e(TAG, "cannot read file: " + mInUri.toString(), e);
                } catch (Exception e) {
                    Log.e(TAG, "cannot read file: " + mInUri.toString(), e);
                }
            }
            return null;
        }

        public Point getImageBounds() {
            InputStream is = regenerateInputStream();
            if (is != null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(is, null, options);
                Utils.closeSilently(is);
                if (options.outWidth != 0 && options.outHeight != 0) {
                    return new Point(options.outWidth, options.outHeight);
                }
            }
            return null;
        }

        public void setCropBounds(RectF cropBounds) {
            mCropBounds = cropBounds;
        }

        public Bitmap getCroppedBitmap() {
            return mCroppedBitmap;
        }

        public boolean cropBitmap() {
            boolean failure = false;


            WallpaperManager wallpaperManager = null;
            if (mSetWallpaper) {
                wallpaperManager = WallpaperManager.getInstance(mContext.getApplicationContext());
            }


            if (mSetWallpaper && mNoCrop) {
                try {
                    InputStream is = regenerateInputStream();
                    if (is != null) {
                        wallpaperManager.setStream(is, null, true, mSetType);
                        Utils.closeSilently(is);
                    }
                } catch (IOException e) {
                    Log.w(TAG, "cannot write stream to wallpaper", e);
                    failure = true;
                }
                return !failure;
            } else {
                // Find crop bounds (scaled to original image size)
                Rect roundedTrueCrop = new Rect();
                Matrix rotateMatrix = new Matrix();
                Matrix inverseRotateMatrix = new Matrix();

                Point bounds = getImageBounds();
                if (mRotation > 0) {
                    rotateMatrix.setRotate(mRotation);
                    inverseRotateMatrix.setRotate(-mRotation);

                    mCropBounds.roundOut(roundedTrueCrop);
                    mCropBounds = new RectF(roundedTrueCrop);

                    if (bounds == null) {
                        Log.w(TAG, "cannot get bounds for image");
                        failure = true;
                        return false;
                    }

                    float[] rotatedBounds = new float[]{bounds.x, bounds.y};
                    rotateMatrix.mapPoints(rotatedBounds);
                    rotatedBounds[0] = Math.abs(rotatedBounds[0]);
                    rotatedBounds[1] = Math.abs(rotatedBounds[1]);

                    mCropBounds.offset(-rotatedBounds[0] / 2, -rotatedBounds[1] / 2);
                    inverseRotateMatrix.mapRect(mCropBounds);
                    mCropBounds.offset(bounds.x / 2, bounds.y / 2);

                }

                mCropBounds.roundOut(roundedTrueCrop);

                if (roundedTrueCrop.width() <= 0 || roundedTrueCrop.height() <= 0) {
                    Log.w(TAG, "crop has bad values for full size image");
                    failure = true;
                    return false;
                }

                // See how much we're reducing the size of the image
                int scaleDownSampleSize = Math.max(1, Math.min(roundedTrueCrop.width() / mOutWidth,
                        roundedTrueCrop.height() / mOutHeight));
                // Attempt to open a region decoder
                BitmapRegionDecoder decoder = null;
                InputStream is = null;
                try {
                    is = regenerateInputStream();
                    if (is == null) {
                        Log.w(TAG, "cannot get input stream for uri=" + mInUri.toString());
                        failure = true;
                        return false;
                    }
                    decoder = BitmapRegionDecoder.newInstance(is, false);
                    Utils.closeSilently(is);
                } catch (IOException e) {
                    Log.w(TAG, "cannot open region decoder for file: " + mInUri.toString(), e);
                } finally {
                    Utils.closeSilently(is);
                    is = null;
                }

                Bitmap crop = null;
                if (decoder != null) {
                    // Do region decoding to get crop bitmap
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    if (scaleDownSampleSize > 1) {
                        options.inSampleSize = scaleDownSampleSize;
                    }
                    crop = decoder.decodeRegion(roundedTrueCrop, options);
                    decoder.recycle();
                }

                if (crop == null) {
                    // BitmapRegionDecoder has failed, try to crop in-memory
                    is = regenerateInputStream();
                    Bitmap fullSize = null;
                    if (is != null) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        if (scaleDownSampleSize > 1) {
                            options.inSampleSize = scaleDownSampleSize;
                        }
                        fullSize = BitmapFactory.decodeStream(is, null, options);
                        Utils.closeSilently(is);
                    }
                    if (fullSize != null) {
                        // Find out the true sample size that was used by the decoder
                        scaleDownSampleSize = bounds.x / fullSize.getWidth();
                        mCropBounds.left /= scaleDownSampleSize;
                        mCropBounds.top /= scaleDownSampleSize;
                        mCropBounds.bottom /= scaleDownSampleSize;
                        mCropBounds.right /= scaleDownSampleSize;
                        mCropBounds.roundOut(roundedTrueCrop);

                        // Adjust values to account for issues related to rounding
                        if (roundedTrueCrop.width() > fullSize.getWidth()) {
                            // Adjust the width
                            roundedTrueCrop.right = roundedTrueCrop.left + fullSize.getWidth();
                        }
                        if (roundedTrueCrop.right > fullSize.getWidth()) {
                            // Adjust the left value
                            int adjustment = roundedTrueCrop.left -
                                    Math.max(0, roundedTrueCrop.right - roundedTrueCrop.width());
                            roundedTrueCrop.left -= adjustment;
                            roundedTrueCrop.right -= adjustment;
                        }
                        if (roundedTrueCrop.height() > fullSize.getHeight()) {
                            // Adjust the height
                            roundedTrueCrop.bottom = roundedTrueCrop.top + fullSize.getHeight();
                        }
                        if (roundedTrueCrop.bottom > fullSize.getHeight()) {
                            // Adjust the top value
                            int adjustment = roundedTrueCrop.top -
                                    Math.max(0, roundedTrueCrop.bottom - roundedTrueCrop.height());
                            roundedTrueCrop.top -= adjustment;
                            roundedTrueCrop.bottom -= adjustment;
                        }

                        try {
                            crop = Bitmap.createBitmap(fullSize, roundedTrueCrop.left,
                                    roundedTrueCrop.top, roundedTrueCrop.width(),
                                    roundedTrueCrop.height());
                        } catch (Exception e) {
                            Log.w(TAG, "createBitmap: roundedTrueCrop(" + roundedTrueCrop + "),fullSize(" + fullSize.getWidth() + "," + fullSize.getHeight() + ")", e);
                            crop = null;
                        }
                    }
                }

                if (crop == null) {
                    Log.w(TAG, "cannot decode file: " + mInUri.toString());
                    failure = true;
                    return false;
                }
                if (mOutWidth > 0 && mOutHeight > 0 || mRotation > 0) {
                    float[] dimsAfter = new float[]{crop.getWidth(), crop.getHeight()};
                    rotateMatrix.mapPoints(dimsAfter);
                    dimsAfter[0] = Math.abs(dimsAfter[0]);
                    dimsAfter[1] = Math.abs(dimsAfter[1]);

                    if (!(mOutWidth > 0 && mOutHeight > 0)) {
                        mOutWidth = Math.round(dimsAfter[0]);
                        mOutHeight = Math.round(dimsAfter[1]);
                    }

                    RectF cropRect = new RectF(0, 0, dimsAfter[0], dimsAfter[1]);
                    RectF returnRect = new RectF(0, 0, mOutWidth, mOutHeight);

                    Matrix m = new Matrix();
                    if (mRotation == 0) {
                        m.setRectToRect(cropRect, returnRect, Matrix.ScaleToFit.FILL);
                    } else {
                        Matrix m1 = new Matrix();
                        m1.setTranslate(-crop.getWidth() / 2f, -crop.getHeight() / 2f);
                        Matrix m2 = new Matrix();
                        m2.setRotate(mRotation);
                        Matrix m3 = new Matrix();
                        m3.setTranslate(dimsAfter[0] / 2f, dimsAfter[1] / 2f);
                        Matrix m4 = new Matrix();
                        m4.setRectToRect(cropRect, returnRect, Matrix.ScaleToFit.FILL);

                        Matrix c1 = new Matrix();
                        c1.setConcat(m2, m1);
                        Matrix c2 = new Matrix();
                        c2.setConcat(m4, m3);
                        m.setConcat(c2, c1);
                    }

                    Bitmap tmp = Bitmap.createBitmap((int) returnRect.width(),
                            (int) returnRect.height(), Bitmap.Config.ARGB_8888);
                    if (tmp != null) {
                        Canvas c = new Canvas(tmp);
                        Paint p = new Paint();
                        p.setFilterBitmap(true);
                        c.drawBitmap(crop, m, p);
                        crop = tmp;
                    }
                }

                if (mSaveCroppedBitmap) {
                    mCroppedBitmap = crop;
                }

                // Get output compression format
                CompressFormat cf =
                        convertExtensionToCompressFormat(getFileExtension(mOutputFormat));

                // Compress to byte array
                ByteArrayOutputStream tmpOut = new ByteArrayOutputStream(2048);
                if (crop.compress(cf, DEFAULT_COMPRESS_QUALITY, tmpOut)) {
                    // If we need to set to the wallpaper, set it
                    if (mSetWallpaper && wallpaperManager != null) {
                        try {
                            byte[] outByteArray = tmpOut.toByteArray();
                            wallpaperManager.setStream(new ByteArrayInputStream(outByteArray), null, true, mSetType);
                            Utils.closeSilently(is);
                            if (mOnBitmapCroppedHandler != null) {
                                mOnBitmapCroppedHandler.onBitmapCropped(outByteArray);
                            }
                        } catch (IOException e) {
                            Log.w(TAG, "cannot write stream to wallpaper", e);
                            failure = true;
                        }
                    }
                } else {
                    Log.w(TAG, "cannot compress bitmap");
                    failure = true;
                }
            }
            return !failure; // True if any of the operations failed
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return cropBitmap();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (mOnEndRunnable != null) {
                mOnEndRunnable.run();
            }
        }
    }

    protected static RectF getMaxCropRect(
            int inWidth, int inHeight, int outWidth, int outHeight, boolean leftAligned) {
        RectF cropRect = new RectF();
        // Get a crop rect that will fit this
        if (inWidth / (float) inHeight > outWidth / (float) outHeight) {
            cropRect.top = 0;
            cropRect.bottom = inHeight;
            cropRect.left = (inWidth - (outWidth / (float) outHeight) * inHeight) / 2;
            cropRect.right = inWidth - cropRect.left;
            if (leftAligned) {
                cropRect.right -= cropRect.left;
                cropRect.left = 0;
            }
        } else {
            cropRect.left = 0;
            cropRect.right = inWidth;
            cropRect.top = (inHeight - (outHeight / (float) outWidth) * inWidth) / 2;
            cropRect.bottom = inHeight - cropRect.top;
        }
        return cropRect;
    }

    protected static CompressFormat convertExtensionToCompressFormat(String extension) {
        return extension.equals("png") ? CompressFormat.PNG : CompressFormat.JPEG;
    }

    protected static String getFileExtension(String requestFormat) {
        String outputFormat = (requestFormat == null)
                ? "jpg"
                : requestFormat;
        outputFormat = outputFormat.toLowerCase();
        return (outputFormat.equals("png") || outputFormat.equals("gif"))
                ? "png" // We don't support gif compression.
                : "jpg";
    }

    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    private static int getNavigationBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        int height = 0;
        if (resourceId > 0) {
            height = context.getResources().getDimensionPixelSize(resourceId);
        }
        return height;
    }

    public static int dp2px(float dpValue) {
        return (int) (0.5f + dpValue * Resources.getSystem().getDisplayMetrics().density);
    }
}
