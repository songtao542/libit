package com.liabit.cropper.exif;

/**
 * The constants of the IFD ID defined in EXIF spec.
 */
public interface IfdId {
    int TYPE_IFD_0 = 0;
    int TYPE_IFD_1 = 1;
    int TYPE_IFD_EXIF = 2;
    int TYPE_IFD_INTEROPERABILITY = 3;
    int TYPE_IFD_GPS = 4;
    /* This is used in ExifData to allocate enough IfdData */
    int TYPE_IFD_COUNT = 5;

}
