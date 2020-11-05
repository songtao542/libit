package com.liabit.cropper.exif;

import java.util.HashMap;
import java.util.Map;

/**
 * This class stores all the tags in an IFD.
 *
 * @see ExifData
 * @see ExifTag
 */
class IfdData {

    private final int mIfdId;
    private final Map<Short, ExifTag> mExifTags = new HashMap<Short, ExifTag>();
    private static final int[] sIfds = {
            IfdId.TYPE_IFD_0, IfdId.TYPE_IFD_1, IfdId.TYPE_IFD_EXIF,
            IfdId.TYPE_IFD_INTEROPERABILITY, IfdId.TYPE_IFD_GPS
    };
    /**
     * Creates an IfdData with given IFD ID.
     *
     * @see IfdId#TYPE_IFD_0
     * @see IfdId#TYPE_IFD_1
     * @see IfdId#TYPE_IFD_EXIF
     * @see IfdId#TYPE_IFD_GPS
     * @see IfdId#TYPE_IFD_INTEROPERABILITY
     */
    IfdData(int ifdId) {
        mIfdId = ifdId;
    }

    static protected int[] getIfds() {
        return sIfds;
    }

    /**
     * Get a array the contains all {@link ExifTag} in this IFD.
     */
    protected ExifTag[] getAllTags() {
        return mExifTags.values().toArray(new ExifTag[mExifTags.size()]);
    }

    /**
     * Gets the ID of this IFD.
     *
     * @see IfdId#TYPE_IFD_0
     * @see IfdId#TYPE_IFD_1
     * @see IfdId#TYPE_IFD_EXIF
     * @see IfdId#TYPE_IFD_GPS
     * @see IfdId#TYPE_IFD_INTEROPERABILITY
     */
    protected int getId() {
        return mIfdId;
    }

    /**
     * Gets the {@link ExifTag} with given tag id. Return null if there is no
     * such tag.
     */
    protected ExifTag getTag(short tagId) {
        return mExifTags.get(tagId);
    }

    /**
     * Adds or replaces a {@link ExifTag}.
     */
    protected ExifTag setTag(ExifTag tag) {
        tag.setIfd(mIfdId);
        return mExifTags.put(tag.getTagId(), tag);
    }

    /**
     * Gets the tags count in the IFD.
     */
    protected int getTagCount() {
        return mExifTags.size();
    }

    /**
     * Returns true if all tags in this two IFDs are equal. Note that tags of
     * IFDs offset or thumbnail offset will be ignored.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof IfdData) {
            IfdData data = (IfdData) obj;
            if (data.getId() == mIfdId && data.getTagCount() == getTagCount()) {
                ExifTag[] tags = data.getAllTags();
                for (ExifTag tag : tags) {
                    if (ExifInterface.isOffsetTag(tag.getTagId())) {
                        continue;
                    }
                    ExifTag tag2 = mExifTags.get(tag.getTagId());
                    if (!tag.equals(tag2)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
}
