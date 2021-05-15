package com.liabit.compress;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 图片压缩工具
 */
public class Compressor {
    private static final String DEFAULT_DISK_CACHE_DIR = "compress_cache";
    private static final String TAG = "Compressor";

    private final InputStreamOpener source;
    private final File target;
    private final boolean focusAlpha;
    private int srcWidth;
    private int srcHeight;

    Compressor(InputStreamOpener source, File target, boolean focusAlpha) throws IOException {
        this.target = target;
        this.source = source;
        this.focusAlpha = focusAlpha;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 1;

        BitmapFactory.decodeStream(source.open(), null, options);
        this.srcWidth = options.outWidth;
        this.srcHeight = options.outHeight;
    }

    private int computeSize() {
        srcWidth = srcWidth % 2 == 1 ? srcWidth + 1 : srcWidth;
        srcHeight = srcHeight % 2 == 1 ? srcHeight + 1 : srcHeight;

        int longSide = Math.max(srcWidth, srcHeight);
        int shortSide = Math.min(srcWidth, srcHeight);

        float scale = ((float) shortSide / longSide);
        if (scale <= 1 && scale > 0.5625) {
            if (longSide < 1664) {
                return 1;
            } else if (longSide < 4990) {
                return 2;
            } else if (longSide > 4990 && longSide < 10240) {
                return 4;
            } else {
                return longSide / 1280 == 0 ? 1 : longSide / 1280;
            }
        } else if (scale <= 0.5625 && scale > 0.5) {
            return longSide / 1280 == 0 ? 1 : longSide / 1280;
        } else {
            return (int) Math.ceil(longSide / (1280.0 / scale));
        }
    }

    private Bitmap rotatingImage(Bitmap bitmap, int angle) {
        Matrix matrix = new Matrix();

        matrix.postRotate(angle);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public File compress() throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = computeSize();

        Bitmap tagBitmap = BitmapFactory.decodeStream(source.open(), null, options);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        if (Checker.SINGLE.isJPG(source.open())) {
            tagBitmap = rotatingImage(tagBitmap, Checker.SINGLE.getOrientation(source.open()));
        }
        tagBitmap.compress(focusAlpha ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG, 60, stream);
        tagBitmap.recycle();

        FileOutputStream fos = new FileOutputStream(target);
        fos.write(stream.toByteArray());
        fos.flush();
        fos.close();
        stream.close();

        return target;
    }

    public interface InputStreamOpener {
        InputStream open() throws IOException;
    }

    private static File getImageCacheFile(Context context, String cacheDir, String suffix) {
        String targetDir = cacheDir;
        if (TextUtils.isEmpty(targetDir)) {
            targetDir = getDefaultImageCacheDir(context).getAbsolutePath();
        }
        String cacheBuilder = targetDir + "/" +
                System.currentTimeMillis() +
                (int) (Math.random() * 1000) +
                (TextUtils.isEmpty(suffix) ? ".jpg" : suffix);
        return new File(cacheBuilder);
    }

    private static File getDefaultImageCacheDir(Context context) {
        File cacheDir = context.getExternalCacheDir();
        if (cacheDir != null) {
            File result = new File(cacheDir, Compressor.DEFAULT_DISK_CACHE_DIR);
            if (!result.mkdirs() && (!result.exists() || !result.isDirectory())) {
                // File wasn't able to create a directory, or the result exists but not a directory
                return null;
            }
            return result;
        }
        if (Log.isLoggable(TAG, Log.ERROR)) {
            Log.e(TAG, "default disk cache dir is null");
        }
        return null;
    }


    @SuppressWarnings({"unused", "RedundantSuppression"})
    public static class Builder {

        private final Context mContext;
        private String mCacheDir;
        private InputStreamOpener mOpener;
        private Uri mUri;
        private boolean mFocusAlpha;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder setCacheDir(File cacheDir) {
            mCacheDir = cacheDir.getAbsolutePath();
            return this;
        }

        public Builder setCacheDir(String cacheDir) {
            mCacheDir = cacheDir;
            return this;
        }

        public Builder setInputStreamOpener(InputStreamOpener opener) {
            mOpener = opener;
            return this;
        }

        public Builder setUri(Uri uri) {
            mUri = uri;
            return this;
        }

        public Builder setFocusAlpha(boolean focusAlpha) {
            mFocusAlpha = focusAlpha;
            return this;
        }

        public Compressor build() throws IOException {
            if (mOpener == null && mUri != null) {
                mOpener = () -> mContext.getContentResolver().openInputStream(mUri);
            }
            if (mOpener == null) {
                throw new IllegalStateException("No compress source.");
            }
            File cacheFile = getImageCacheFile(mContext, mCacheDir, Checker.SINGLE.extSuffix(mOpener));
            return new Compressor(mOpener, cacheFile, mFocusAlpha);
        }

        public File compress() throws IOException {
            return build().compress();
        }
    }
}
