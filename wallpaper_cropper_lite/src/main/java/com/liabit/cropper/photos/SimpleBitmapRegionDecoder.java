package com.liabit.cropper.photos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

interface SimpleBitmapRegionDecoder {
    int getWidth();

    int getHeight();

    Bitmap decodeRegion(Rect wantRegion, BitmapFactory.Options options);
}