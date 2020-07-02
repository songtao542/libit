package cn.lolii.cropper.exif;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class stores the EXIF header in IFDs according to the JPEG
 * specification. It is the result produced by {@link ExifReader}.
 *
 * @see ExifReader
 * @see IfdData
 */
class ExifData {

    private final IfdData[] mIfdDatas = new IfdData[IfdId.TYPE_IFD_COUNT];
    private byte[] mThumbnail;
    private final ArrayList<byte[]> mStripBytes = new ArrayList<byte[]>();
    private final ByteOrder mByteOrder;

    ExifData(ByteOrder order) {
        mByteOrder = order;
    }

    /**
     * Sets the compressed thumbnail.
     */
    protected void setCompressedThumbnail(byte[] thumbnail) {
        mThumbnail = thumbnail;
    }

    /**
     * Adds an uncompressed strip.
     */
    protected void setStripBytes(int index, byte[] strip) {
        if (index < mStripBytes.size()) {
            mStripBytes.set(index, strip);
        } else {
            for (int i = mStripBytes.size(); i < index; i++) {
                mStripBytes.add(null);
            }
            mStripBytes.add(strip);
        }
    }

    /**
     * Returns the {@link IfdData} object corresponding to a given IFD if it
     * exists or null.
     */
    protected IfdData getIfdData(int ifdId) {
        if (ExifTag.isValidIfd(ifdId)) {
            return mIfdDatas[ifdId];
        }
        return null;
    }

    /**
     * Adds IFD data. If IFD data of the same type already exists, it will be
     * replaced by the new data.
     */
    protected void addIfdData(IfdData data) {
        mIfdDatas[data.getId()] = data;
    }

    /**
     * Returns the {@link IfdData} object corresponding to a given IFD or
     * generates one if none exist.
     */
    protected IfdData getOrCreateIfdData(int ifdId) {
        IfdData ifdData = mIfdDatas[ifdId];
        if (ifdData == null) {
            ifdData = new IfdData(ifdId);
            mIfdDatas[ifdId] = ifdData;
        }
        return ifdData;
    }

    /**
     * Returns the tag with a given TID in the given IFD if the tag exists.
     * Otherwise returns null.
     */
    protected ExifTag getTag(short tag, int ifd) {
        IfdData ifdData = mIfdDatas[ifd];
        return (ifdData == null) ? null : ifdData.getTag(tag);
    }

    /**
     * Adds the given ExifTag to its default IFD and returns an existing ExifTag
     * with the same TID or null if none exist.
     */
    protected ExifTag addTag(ExifTag tag) {
        if (tag != null) {
            int ifd = tag.getIfd();
            return addTag(tag, ifd);
        }
        return null;
    }

    /**
     * Adds the given ExifTag to the given IFD and returns an existing ExifTag
     * with the same TID or null if none exist.
     */
    protected ExifTag addTag(ExifTag tag, int ifdId) {
        if (tag != null && ExifTag.isValidIfd(ifdId)) {
            IfdData ifdData = getOrCreateIfdData(ifdId);
            return ifdData.setTag(tag);
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof ExifData) {
            ExifData data = (ExifData) obj;
            if (data.mByteOrder != mByteOrder ||
                    data.mStripBytes.size() != mStripBytes.size() ||
                    !Arrays.equals(data.mThumbnail, mThumbnail)) {
                return false;
            }
            for (int i = 0; i < mStripBytes.size(); i++) {
                if (!Arrays.equals(data.mStripBytes.get(i), mStripBytes.get(i))) {
                    return false;
                }
            }
            for (int i = 0; i < IfdId.TYPE_IFD_COUNT; i++) {
                IfdData ifd1 = data.getIfdData(i);
                IfdData ifd2 = getIfdData(i);
                if (ifd1 != ifd2 && ifd1 != null && !ifd1.equals(ifd2)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

}
