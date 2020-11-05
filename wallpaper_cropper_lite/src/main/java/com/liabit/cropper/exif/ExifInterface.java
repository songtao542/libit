package com.liabit.cropper.exif;

import android.util.SparseIntArray;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.TimeZone;

/**
 * This class provides methods and constants for reading and writing jpeg file
 * metadata. It contains a collection of ExifTags, and a collection of
 * definitions for creating valid ExifTags. The collection of ExifTags can be
 * updated by: reading new ones from a file, deleting or adding existing ones,
 * or building new ExifTags from a tag definition. These ExifTags can be written
 * to a valid jpeg image as exif metadata.
 * <p>
 * Each ExifTag has a tag ID (TID) and is stored in a specific image file
 * directory (IFD) as specified by the exif standard. A tag definition can be
 * looked up with a constant that is a combination of TID and IFD. This
 * definition has information about the type, number of components, and valid
 * IFDs for a tag.
 *
 * @see ExifTag
 */
public class ExifInterface {
    public static final int TAG_NULL = -1;
    public static final int IFD_NULL = -1;
    public static final int DEFINITION_NULL = 0;

    /**
     * Tag constants for Jeita EXIF 2.2
     */

    // IFD 0
    public static final int TAG_IMAGE_WIDTH = defineTag(IfdId.TYPE_IFD_0, (short) 0x0100);
    public static final int TAG_IMAGE_LENGTH = defineTag(IfdId.TYPE_IFD_0, (short) 0x0101); // Image height
    public static final int TAG_BITS_PER_SAMPLE = defineTag(IfdId.TYPE_IFD_0, (short) 0x0102);
    public static final int TAG_COMPRESSION = defineTag(IfdId.TYPE_IFD_0, (short) 0x0103);
    public static final int TAG_PHOTOMETRIC_INTERPRETATION = defineTag(IfdId.TYPE_IFD_0, (short) 0x0106);
    public static final int TAG_IMAGE_DESCRIPTION = defineTag(IfdId.TYPE_IFD_0, (short) 0x010E);
    public static final int TAG_MAKE = defineTag(IfdId.TYPE_IFD_0, (short) 0x010F);
    public static final int TAG_MODEL = defineTag(IfdId.TYPE_IFD_0, (short) 0x0110);
    public static final int TAG_STRIP_OFFSETS = defineTag(IfdId.TYPE_IFD_0, (short) 0x0111);
    public static final int TAG_ORIENTATION = defineTag(IfdId.TYPE_IFD_0, (short) 0x0112);
    public static final int TAG_SAMPLES_PER_PIXEL = defineTag(IfdId.TYPE_IFD_0, (short) 0x0115);
    public static final int TAG_ROWS_PER_STRIP = defineTag(IfdId.TYPE_IFD_0, (short) 0x0116);
    public static final int TAG_STRIP_BYTE_COUNTS = defineTag(IfdId.TYPE_IFD_0, (short) 0x0117);
    public static final int TAG_X_RESOLUTION = defineTag(IfdId.TYPE_IFD_0, (short) 0x011A);
    public static final int TAG_Y_RESOLUTION = defineTag(IfdId.TYPE_IFD_0, (short) 0x011B);
    public static final int TAG_PLANAR_CONFIGURATION = defineTag(IfdId.TYPE_IFD_0, (short) 0x011C);
    public static final int TAG_RESOLUTION_UNIT = defineTag(IfdId.TYPE_IFD_0, (short) 0x0128);
    public static final int TAG_TRANSFER_FUNCTION = defineTag(IfdId.TYPE_IFD_0, (short) 0x012D);
    public static final int TAG_SOFTWARE = defineTag(IfdId.TYPE_IFD_0, (short) 0x0131);
    public static final int TAG_DATE_TIME = defineTag(IfdId.TYPE_IFD_0, (short) 0x0132);
    public static final int TAG_ARTIST = defineTag(IfdId.TYPE_IFD_0, (short) 0x013B);
    public static final int TAG_WHITE_POINT = defineTag(IfdId.TYPE_IFD_0, (short) 0x013E);
    public static final int TAG_PRIMARY_CHROMATICITIES = defineTag(IfdId.TYPE_IFD_0, (short) 0x013F);
    public static final int TAG_Y_CB_CR_COEFFICIENTS = defineTag(IfdId.TYPE_IFD_0, (short) 0x0211);
    public static final int TAG_Y_CB_CR_SUB_SAMPLING = defineTag(IfdId.TYPE_IFD_0, (short) 0x0212);
    public static final int TAG_Y_CB_CR_POSITIONING = defineTag(IfdId.TYPE_IFD_0, (short) 0x0213);
    public static final int TAG_REFERENCE_BLACK_WHITE = defineTag(IfdId.TYPE_IFD_0, (short) 0x0214);
    public static final int TAG_COPYRIGHT = defineTag(IfdId.TYPE_IFD_0, (short) 0x8298);
    public static final int TAG_EXIF_IFD = defineTag(IfdId.TYPE_IFD_0, (short) 0x8769);
    public static final int TAG_GPS_IFD = defineTag(IfdId.TYPE_IFD_0, (short) 0x8825);
    // IFD 1
    public static final int TAG_JPEG_INTERCHANGE_FORMAT = defineTag(IfdId.TYPE_IFD_1, (short) 0x0201);
    public static final int TAG_JPEG_INTERCHANGE_FORMAT_LENGTH = defineTag(IfdId.TYPE_IFD_1, (short) 0x0202);
    // IFD Exif Tags
    public static final int TAG_EXPOSURE_TIME = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0x829A);
    public static final int TAG_F_NUMBER = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0x829D);
    public static final int TAG_EXPOSURE_PROGRAM = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0x8822);
    public static final int TAG_SPECTRAL_SENSITIVITY = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0x8824);
    public static final int TAG_ISO_SPEED_RATINGS = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0x8827);
    public static final int TAG_OECF = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0x8828);
    public static final int TAG_EXIF_VERSION = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0x9000);
    public static final int TAG_DATE_TIME_ORIGINAL = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0x9003);
    public static final int TAG_DATE_TIME_DIGITIZED = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0x9004);
    public static final int TAG_COMPONENTS_CONFIGURATION = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0x9101);
    public static final int TAG_COMPRESSED_BITS_PER_PIXEL = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0x9102);
    public static final int TAG_SHUTTER_SPEED_VALUE = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0x9201);
    public static final int TAG_APERTURE_VALUE = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0x9202);
    public static final int TAG_BRIGHTNESS_VALUE = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0x9203);
    public static final int TAG_EXPOSURE_BIAS_VALUE = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0x9204);
    public static final int TAG_MAX_APERTURE_VALUE = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0x9205);
    public static final int TAG_SUBJECT_DISTANCE = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0x9206);
    public static final int TAG_METERING_MODE = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0x9207);
    public static final int TAG_LIGHT_SOURCE = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0x9208);
    public static final int TAG_FLASH = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0x9209);
    public static final int TAG_FOCAL_LENGTH = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0x920A);
    public static final int TAG_SUBJECT_AREA = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0x9214);
    public static final int TAG_MAKER_NOTE = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0x927C);
    public static final int TAG_USER_COMMENT = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0x9286);
    public static final int TAG_SUB_SEC_TIME = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0x9290);
    public static final int TAG_SUB_SEC_TIME_ORIGINAL = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0x9291);
    public static final int TAG_SUB_SEC_TIME_DIGITIZED = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0x9292);
    public static final int TAG_FLASHPIX_VERSION = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0xA000);
    public static final int TAG_COLOR_SPACE = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0xA001);
    public static final int TAG_PIXEL_X_DIMENSION = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0xA002);
    public static final int TAG_PIXEL_Y_DIMENSION = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0xA003);
    public static final int TAG_RELATED_SOUND_FILE = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0xA004);
    public static final int TAG_INTEROPERABILITY_IFD = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0xA005);
    public static final int TAG_FLASH_ENERGY = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0xA20B);
    public static final int TAG_SPATIAL_FREQUENCY_RESPONSE = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0xA20C);
    public static final int TAG_FOCAL_PLANE_X_RESOLUTION = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0xA20E);
    public static final int TAG_FOCAL_PLANE_Y_RESOLUTION = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0xA20F);
    public static final int TAG_FOCAL_PLANE_RESOLUTION_UNIT = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0xA210);
    public static final int TAG_SUBJECT_LOCATION = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0xA214);
    public static final int TAG_EXPOSURE_INDEX = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0xA215);
    public static final int TAG_SENSING_METHOD = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0xA217);
    public static final int TAG_FILE_SOURCE = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0xA300);
    public static final int TAG_SCENE_TYPE = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0xA301);
    public static final int TAG_CFA_PATTERN = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0xA302);
    public static final int TAG_CUSTOM_RENDERED = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0xA401);
    public static final int TAG_EXPOSURE_MODE = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0xA402);
    public static final int TAG_WHITE_BALANCE = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0xA403);
    public static final int TAG_DIGITAL_ZOOM_RATIO = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0xA404);
    public static final int TAG_FOCAL_LENGTH_IN_35_MM_FILE = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0xA405);
    public static final int TAG_SCENE_CAPTURE_TYPE = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0xA406);
    public static final int TAG_GAIN_CONTROL = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0xA407);
    public static final int TAG_CONTRAST = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0xA408);
    public static final int TAG_SATURATION = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0xA409);
    public static final int TAG_SHARPNESS = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0xA40A);
    public static final int TAG_DEVICE_SETTING_DESCRIPTION = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0xA40B);
    public static final int TAG_SUBJECT_DISTANCE_RANGE = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0xA40C);
    public static final int TAG_IMAGE_UNIQUE_ID = defineTag(IfdId.TYPE_IFD_EXIF, (short) 0xA420);
    // IFD GPS tags
    public static final int TAG_GPS_VERSION_ID = defineTag(IfdId.TYPE_IFD_GPS, (short) 0);
    public static final int TAG_GPS_LATITUDE_REF = defineTag(IfdId.TYPE_IFD_GPS, (short) 1);
    public static final int TAG_GPS_LATITUDE = defineTag(IfdId.TYPE_IFD_GPS, (short) 2);
    public static final int TAG_GPS_LONGITUDE_REF = defineTag(IfdId.TYPE_IFD_GPS, (short) 3);
    public static final int TAG_GPS_LONGITUDE = defineTag(IfdId.TYPE_IFD_GPS, (short) 4);
    public static final int TAG_GPS_ALTITUDE_REF = defineTag(IfdId.TYPE_IFD_GPS, (short) 5);
    public static final int TAG_GPS_ALTITUDE = defineTag(IfdId.TYPE_IFD_GPS, (short) 6);
    public static final int TAG_GPS_TIME_STAMP = defineTag(IfdId.TYPE_IFD_GPS, (short) 7);
    public static final int TAG_GPS_SATTELLITES = defineTag(IfdId.TYPE_IFD_GPS, (short) 8);
    public static final int TAG_GPS_STATUS = defineTag(IfdId.TYPE_IFD_GPS, (short) 9);
    public static final int TAG_GPS_MEASURE_MODE = defineTag(IfdId.TYPE_IFD_GPS, (short) 10);
    public static final int TAG_GPS_DOP = defineTag(IfdId.TYPE_IFD_GPS, (short) 11);
    public static final int TAG_GPS_SPEED_REF = defineTag(IfdId.TYPE_IFD_GPS, (short) 12);
    public static final int TAG_GPS_SPEED = defineTag(IfdId.TYPE_IFD_GPS, (short) 13);
    public static final int TAG_GPS_TRACK_REF = defineTag(IfdId.TYPE_IFD_GPS, (short) 14);
    public static final int TAG_GPS_TRACK = defineTag(IfdId.TYPE_IFD_GPS, (short) 15);
    public static final int TAG_GPS_IMG_DIRECTION_REF = defineTag(IfdId.TYPE_IFD_GPS, (short) 16);
    public static final int TAG_GPS_IMG_DIRECTION = defineTag(IfdId.TYPE_IFD_GPS, (short) 17);
    public static final int TAG_GPS_MAP_DATUM = defineTag(IfdId.TYPE_IFD_GPS, (short) 18);
    public static final int TAG_GPS_DEST_LATITUDE_REF = defineTag(IfdId.TYPE_IFD_GPS, (short) 19);
    public static final int TAG_GPS_DEST_LATITUDE = defineTag(IfdId.TYPE_IFD_GPS, (short) 20);
    public static final int TAG_GPS_DEST_BEARING_REF = defineTag(IfdId.TYPE_IFD_GPS, (short) 23);
    public static final int TAG_GPS_DEST_BEARING = defineTag(IfdId.TYPE_IFD_GPS, (short) 24);
    public static final int TAG_GPS_DEST_DISTANCE_REF = defineTag(IfdId.TYPE_IFD_GPS, (short) 25);
    public static final int TAG_GPS_DEST_DISTANCE = defineTag(IfdId.TYPE_IFD_GPS, (short) 26);
    public static final int TAG_GPS_PROCESSING_METHOD = defineTag(IfdId.TYPE_IFD_GPS, (short) 27);
    public static final int TAG_GPS_AREA_INFORMATION = defineTag(IfdId.TYPE_IFD_GPS, (short) 28);
    public static final int TAG_GPS_DATE_STAMP = defineTag(IfdId.TYPE_IFD_GPS, (short) 29);
    public static final int TAG_GPS_DIFFERENTIAL = defineTag(IfdId.TYPE_IFD_GPS, (short) 30);
    // IFD Interoperability tags
    public static final int TAG_INTEROPERABILITY_INDEX = defineTag(IfdId.TYPE_IFD_INTEROPERABILITY, (short) 1);

    /**
     * Tags that contain offset markers. These are included in the banned
     * defines.
     */
    private static final HashSet<Short> sOffsetTags = new HashSet<>();

    static {
        sOffsetTags.add(getTrueTagKey(TAG_GPS_IFD));
        sOffsetTags.add(getTrueTagKey(TAG_EXIF_IFD));
        sOffsetTags.add(getTrueTagKey(TAG_JPEG_INTERCHANGE_FORMAT));
        sOffsetTags.add(getTrueTagKey(TAG_INTEROPERABILITY_IFD));
        sOffsetTags.add(getTrueTagKey(TAG_STRIP_OFFSETS));
    }

    /**
     * Tags with definitions that cannot be overridden (banned defines).
     */
    protected static HashSet<Short> sBannedDefines = new HashSet<>(sOffsetTags);

    static {
        sBannedDefines.add(getTrueTagKey(TAG_NULL));
        sBannedDefines.add(getTrueTagKey(TAG_JPEG_INTERCHANGE_FORMAT_LENGTH));
        sBannedDefines.add(getTrueTagKey(TAG_STRIP_BYTE_COUNTS));
    }

    /**
     * Returns the constant representing a tag with a given TID and default IFD.
     */
    public static int defineTag(int ifdId, short tagId) {
        return (tagId & 0x0000ffff) | (ifdId << 16);
    }

    /**
     * Returns the TID for a tag constant.
     */
    public static short getTrueTagKey(int tag) {
        // Truncate
        return (short) tag;
    }

    /**
     * Returns the default IFD for a tag constant.
     */
    public static int getTrueIfd(int tag) {
        return tag >>> 16;
    }

    /**
     * follows:
     * <ul>
     * <li>TOP_LEFT is the normal orientation.</li>
     * <li>TOP_RIGHT is a left-right mirror.</li>
     * <li>BOTTOM_LEFT is a 180 degree rotation.</li>
     * <li>BOTTOM_RIGHT is a top-bottom mirror.</li>
     * <li>LEFT_TOP is mirrored about the top-left<->bottom-right axis.</li>
     * <li>RIGHT_TOP is a 90 degree clockwise rotation.</li>
     * <li>LEFT_BOTTOM is mirrored about the top-right<->bottom-left axis.</li>
     * <li>RIGHT_BOTTOM is a 270 degree clockwise rotation.</li>
     * </ul>
     */
    public interface Orientation {
        short TOP_LEFT = 1;
        short BOTTOM_LEFT = 3;
        short RIGHT_TOP = 6;
        short RIGHT_BOTTOM = 8;
    }

    private static final String NULL_ARGUMENT_STRING = "Argument is null";
    private ExifData mData = new ExifData(DEFAULT_BYTE_ORDER);
    public static final ByteOrder DEFAULT_BYTE_ORDER = ByteOrder.BIG_ENDIAN;

    public ExifInterface() {
        mGPSDateStampFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    /**
     * Reads the exif tags from an InputStream, clearing this ExifInterface
     * object's existing exif tags.
     *
     * @param inStream an InputStream containing a jpeg compressed image.
     * @throws IOException
     */
    public void readExif(InputStream inStream) throws IOException {
        if (inStream == null) {
            throw new IllegalArgumentException(NULL_ARGUMENT_STRING);
        }
        ExifData d;
        try {
            d = new ExifReader(this).read(inStream);
        } catch (ExifInvalidFormatException e) {
            throw new IOException("Invalid exif format : " + e);
        }
        mData = d;
    }

    /**
     * Reads the exif tags from a file, clearing this ExifInterface object's
     * existing exif tags.
     *
     * @param inFileName a string representing the filepath to jpeg file.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void readExif(String inFileName) throws FileNotFoundException, IOException {
        if (inFileName == null) {
            throw new IllegalArgumentException(NULL_ARGUMENT_STRING);
        }
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(inFileName));
            readExif(is);
        } catch (IOException e) {
            closeSilently(is);
            throw e;
        }
        is.close();
    }

    /**
     * Sets the exif tags, clearing this ExifInterface object's existing exif
     * tags.
     *
     * @param tags a collection of exif tags to set.
     */
    public void setExif(Collection<ExifTag> tags) {
        clearExif();
        setTags(tags);
    }

    /**
     * Clears this ExifInterface object's existing exif tags.
     */
    public void clearExif() {
        mData = new ExifData(DEFAULT_BYTE_ORDER);
    }

    /**
     * Gets an ExifTag for an IFD other than the tag's default.
     */
    public ExifTag getTag(int tagId, int ifdId) {
        if (!ExifTag.isValidIfd(ifdId)) {
            return null;
        }
        return mData.getTag(getTrueTagKey(tagId), ifdId);
    }

    public Integer getTagIntValue(int tagId, int ifdId) {
        int[] l = getTagIntValues(tagId, ifdId);
        if (l == null || l.length <= 0) {
            return null;
        }
        return l[0];
    }

    public Integer getTagIntValue(int tagId) {
        int ifdId = getDefinedTagDefaultIfd(tagId);
        return getTagIntValue(tagId, ifdId);
    }

    public int[] getTagIntValues(int tagId, int ifdId) {
        ExifTag t = getTag(tagId, ifdId);
        if (t == null) {
            return null;
        }
        return t.getValueAsInts();
    }

    /**
     * Gets the default IFD for a tag.
     *
     * @param tagId a defined tag constant, e.g. {@link #TAG_IMAGE_WIDTH}.
     * @return the default IFD for a tag definition or {@link #IFD_NULL} if no
     * definition exists.
     */
    public int getDefinedTagDefaultIfd(int tagId) {
        int info = getTagInfo().get(tagId);
        if (info == DEFINITION_NULL) {
            return IFD_NULL;
        }
        return getTrueIfd(tagId);
    }

    /**
     * Note: defining tags with these TID's is disallowed.
     *
     * @param tag a tag's TID (can be obtained from a defined tag constant with
     *            {@link #getTrueTagKey}).
     * @return true if the TID is that of an offset tag.
     */
    protected static boolean isOffsetTag(short tag) {
        return sOffsetTags.contains(tag);
    }

    /**
     * Creates a tag for a defined tag constant in a given IFD if that IFD is
     * allowed for the tag.  This method will fail anytime the appropriate
     * {@link ExifTag#setValue} for this tag's datatype would fail.
     *
     * @param tagId a tag constant, e.g. {@link #TAG_IMAGE_WIDTH}.
     * @param ifdId the IFD that the tag should be in.
     * @param val   the value of the tag to set.
     * @return an ExifTag object or null if one could not be constructed.
     * @see #buildTag
     */
    public ExifTag buildTag(int tagId, int ifdId, Object val) {
        int info = getTagInfo().get(tagId);
        if (info == 0 || val == null) {
            return null;
        }
        short type = getTypeFromInfo(info);
        int definedCount = getComponentCountFromInfo(info);
        boolean hasDefinedCount = (definedCount != ExifTag.SIZE_UNDEFINED);
        if (!ExifInterface.isIfdAllowed(info, ifdId)) {
            return null;
        }
        ExifTag t = new ExifTag(getTrueTagKey(tagId), type, definedCount, ifdId, hasDefinedCount);
        if (!t.setValue(val)) {
            return null;
        }
        return t;
    }

    /**
     * Creates a tag for a defined tag constant in the tag's default IFD.
     *
     * @param tagId a tag constant, e.g. {@link #TAG_IMAGE_WIDTH}.
     * @param val   the tag's value.
     * @return an ExifTag object.
     */
    public ExifTag buildTag(int tagId, Object val) {
        int ifdId = getTrueIfd(tagId);
        return buildTag(tagId, ifdId, val);
    }

    protected ExifTag buildUninitializedTag(int tagId) {
        int info = getTagInfo().get(tagId);
        if (info == 0) {
            return null;
        }
        short type = getTypeFromInfo(info);
        int definedCount = getComponentCountFromInfo(info);
        boolean hasDefinedCount = (definedCount != ExifTag.SIZE_UNDEFINED);
        int ifdId = getTrueIfd(tagId);
        ExifTag t = new ExifTag(getTrueTagKey(tagId), type, definedCount, ifdId, hasDefinedCount);
        return t;
    }

    /**
     * Puts an ExifTag into this ExifInterface object's tags, removing a
     * previous ExifTag with the same TID and IFD. The IFD it is put into will
     * be the one the tag was created with in {@link #buildTag}.
     *
     * @param tag an ExifTag to put into this ExifInterface's tags.
     * @return the previous ExifTag with the same TID and IFD or null if none
     * exists.
     */
    public ExifTag setTag(ExifTag tag) {
        return mData.addTag(tag);
    }

    /**
     * Puts a collection of ExifTags into this ExifInterface objects's tags. Any
     * previous ExifTags with the same TID and IFDs will be removed.
     *
     * @param tags a Collection of ExifTags.
     * @see #setTag
     */
    public void setTags(Collection<ExifTag> tags) {
        for (ExifTag t : tags) {
            setTag(t);
        }
    }

    /**
     * Returns the rotation degrees corresponding to an ExifTag Orientation
     * value.
     *
     * @param orientation the ExifTag Orientation value.
     */
    public static int getRotationForOrientationValue(short orientation) {
        switch (orientation) {
            case Orientation.RIGHT_TOP:
                return 90;
            case Orientation.BOTTOM_LEFT:
                return 180;
            case Orientation.RIGHT_BOTTOM:
                return 270;
            default:
                return 0;
        }
    }

    private static final String GPS_DATE_FORMAT_STR = "yyyy:MM:dd";
    private final DateFormat mGPSDateStampFormat = new SimpleDateFormat(GPS_DATE_FORMAT_STR);

    protected static void closeSilently(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (Throwable e) {
                // ignored
            }
        }
    }

    private SparseIntArray mTagInfo = null;

    protected SparseIntArray getTagInfo() {
        if (mTagInfo == null) {
            mTagInfo = new SparseIntArray();
            initTagInfo();
        }
        return mTagInfo;
    }

    private void initTagInfo() {
        /**
         * We put tag information in a 4-bytes integer. The first byte a bitmask
         * representing the allowed IFDs of the tag, the second byte is the data
         * type, and the last two byte are a short value indicating the default
         * component count of this tag.
         */
        // IFD0 tags
        int[] ifdAllowedIfds = {
                IfdId.TYPE_IFD_0,
                IfdId.TYPE_IFD_1
        };
        int ifdFlags = getFlagsFromAllowedIfds(ifdAllowedIfds) << 24;
        mTagInfo.put(ExifInterface.TAG_MAKE, ifdFlags | ExifTag.TYPE_ASCII << 16 | ExifTag.SIZE_UNDEFINED);
        mTagInfo.put(ExifInterface.TAG_IMAGE_WIDTH, ifdFlags | ExifTag.TYPE_UNSIGNED_LONG << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_IMAGE_LENGTH, ifdFlags | ExifTag.TYPE_UNSIGNED_LONG << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_BITS_PER_SAMPLE, ifdFlags | ExifTag.TYPE_UNSIGNED_SHORT << 16 | 3);
        mTagInfo.put(ExifInterface.TAG_COMPRESSION, ifdFlags | ExifTag.TYPE_UNSIGNED_SHORT << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_PHOTOMETRIC_INTERPRETATION, ifdFlags | ExifTag.TYPE_UNSIGNED_SHORT << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_ORIENTATION, ifdFlags | ExifTag.TYPE_UNSIGNED_SHORT << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_SAMPLES_PER_PIXEL, ifdFlags | ExifTag.TYPE_UNSIGNED_SHORT << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_PLANAR_CONFIGURATION, ifdFlags | ExifTag.TYPE_UNSIGNED_SHORT << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_Y_CB_CR_SUB_SAMPLING, ifdFlags | ExifTag.TYPE_UNSIGNED_SHORT << 16 | 2);
        mTagInfo.put(ExifInterface.TAG_Y_CB_CR_POSITIONING, ifdFlags | ExifTag.TYPE_UNSIGNED_SHORT << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_X_RESOLUTION, ifdFlags | ExifTag.TYPE_UNSIGNED_RATIONAL << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_Y_RESOLUTION, ifdFlags | ExifTag.TYPE_UNSIGNED_RATIONAL << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_RESOLUTION_UNIT, ifdFlags | ExifTag.TYPE_UNSIGNED_SHORT << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_STRIP_OFFSETS, ifdFlags | ExifTag.TYPE_UNSIGNED_LONG << 16 | ExifTag.SIZE_UNDEFINED);
        mTagInfo.put(ExifInterface.TAG_ROWS_PER_STRIP, ifdFlags | ExifTag.TYPE_UNSIGNED_LONG << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_STRIP_BYTE_COUNTS, ifdFlags | ExifTag.TYPE_UNSIGNED_LONG << 16 | ExifTag.SIZE_UNDEFINED);
        mTagInfo.put(ExifInterface.TAG_TRANSFER_FUNCTION, ifdFlags | ExifTag.TYPE_UNSIGNED_SHORT << 16 | 3 * 256);
        mTagInfo.put(ExifInterface.TAG_WHITE_POINT, ifdFlags | ExifTag.TYPE_UNSIGNED_RATIONAL << 16 | 2);
        mTagInfo.put(ExifInterface.TAG_PRIMARY_CHROMATICITIES, ifdFlags | ExifTag.TYPE_UNSIGNED_RATIONAL << 16 | 6);
        mTagInfo.put(ExifInterface.TAG_Y_CB_CR_COEFFICIENTS, ifdFlags | ExifTag.TYPE_UNSIGNED_RATIONAL << 16 | 3);
        mTagInfo.put(ExifInterface.TAG_REFERENCE_BLACK_WHITE, ifdFlags | ExifTag.TYPE_UNSIGNED_RATIONAL << 16 | 6);
        mTagInfo.put(ExifInterface.TAG_DATE_TIME, ifdFlags | ExifTag.TYPE_ASCII << 16 | 20);
        mTagInfo.put(ExifInterface.TAG_IMAGE_DESCRIPTION, ifdFlags | ExifTag.TYPE_ASCII << 16 | ExifTag.SIZE_UNDEFINED);
        mTagInfo.put(ExifInterface.TAG_MAKE, ifdFlags | ExifTag.TYPE_ASCII << 16 | ExifTag.SIZE_UNDEFINED);
        mTagInfo.put(ExifInterface.TAG_MODEL, ifdFlags | ExifTag.TYPE_ASCII << 16 | ExifTag.SIZE_UNDEFINED);
        mTagInfo.put(ExifInterface.TAG_SOFTWARE, ifdFlags | ExifTag.TYPE_ASCII << 16 | ExifTag.SIZE_UNDEFINED);
        mTagInfo.put(ExifInterface.TAG_ARTIST, ifdFlags | ExifTag.TYPE_ASCII << 16 | ExifTag.SIZE_UNDEFINED);
        mTagInfo.put(ExifInterface.TAG_COPYRIGHT, ifdFlags | ExifTag.TYPE_ASCII << 16 | ExifTag.SIZE_UNDEFINED);
        mTagInfo.put(ExifInterface.TAG_EXIF_IFD, ifdFlags | ExifTag.TYPE_UNSIGNED_LONG << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_GPS_IFD, ifdFlags | ExifTag.TYPE_UNSIGNED_LONG << 16 | 1);
        // IFD1 tags
        int[] ifd1AllowedIfds = {
                IfdId.TYPE_IFD_1
        };
        int ifdFlags1 = getFlagsFromAllowedIfds(ifd1AllowedIfds) << 24;
        mTagInfo.put(ExifInterface.TAG_JPEG_INTERCHANGE_FORMAT, ifdFlags1 | ExifTag.TYPE_UNSIGNED_LONG << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_JPEG_INTERCHANGE_FORMAT_LENGTH, ifdFlags1 | ExifTag.TYPE_UNSIGNED_LONG << 16 | 1);
        // Exif tags
        int[] exifAllowedIfds = {
                IfdId.TYPE_IFD_EXIF
        };
        int exifFlags = getFlagsFromAllowedIfds(exifAllowedIfds) << 24;
        mTagInfo.put(ExifInterface.TAG_EXIF_VERSION, exifFlags | ExifTag.TYPE_UNDEFINED << 16 | 4);
        mTagInfo.put(ExifInterface.TAG_FLASHPIX_VERSION, exifFlags | ExifTag.TYPE_UNDEFINED << 16 | 4);
        mTagInfo.put(ExifInterface.TAG_COLOR_SPACE, exifFlags | ExifTag.TYPE_UNSIGNED_SHORT << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_COMPONENTS_CONFIGURATION, exifFlags | ExifTag.TYPE_UNDEFINED << 16 | 4);
        mTagInfo.put(ExifInterface.TAG_COMPRESSED_BITS_PER_PIXEL, exifFlags | ExifTag.TYPE_UNSIGNED_RATIONAL << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_PIXEL_X_DIMENSION, exifFlags | ExifTag.TYPE_UNSIGNED_LONG << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_PIXEL_Y_DIMENSION, exifFlags | ExifTag.TYPE_UNSIGNED_LONG << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_MAKER_NOTE, exifFlags | ExifTag.TYPE_UNDEFINED << 16 | ExifTag.SIZE_UNDEFINED);
        mTagInfo.put(ExifInterface.TAG_USER_COMMENT, exifFlags | ExifTag.TYPE_UNDEFINED << 16 | ExifTag.SIZE_UNDEFINED);
        mTagInfo.put(ExifInterface.TAG_RELATED_SOUND_FILE, exifFlags | ExifTag.TYPE_ASCII << 16 | 13);
        mTagInfo.put(ExifInterface.TAG_DATE_TIME_ORIGINAL, exifFlags | ExifTag.TYPE_ASCII << 16 | 20);
        mTagInfo.put(ExifInterface.TAG_DATE_TIME_DIGITIZED, exifFlags | ExifTag.TYPE_ASCII << 16 | 20);
        mTagInfo.put(ExifInterface.TAG_SUB_SEC_TIME, exifFlags | ExifTag.TYPE_ASCII << 16 | ExifTag.SIZE_UNDEFINED);
        mTagInfo.put(ExifInterface.TAG_SUB_SEC_TIME_ORIGINAL, exifFlags | ExifTag.TYPE_ASCII << 16 | ExifTag.SIZE_UNDEFINED);
        mTagInfo.put(ExifInterface.TAG_SUB_SEC_TIME_DIGITIZED, exifFlags | ExifTag.TYPE_ASCII << 16 | ExifTag.SIZE_UNDEFINED);
        mTagInfo.put(ExifInterface.TAG_IMAGE_UNIQUE_ID, exifFlags | ExifTag.TYPE_ASCII << 16 | 33);
        mTagInfo.put(ExifInterface.TAG_EXPOSURE_TIME, exifFlags | ExifTag.TYPE_UNSIGNED_RATIONAL << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_F_NUMBER, exifFlags | ExifTag.TYPE_UNSIGNED_RATIONAL << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_EXPOSURE_PROGRAM, exifFlags | ExifTag.TYPE_UNSIGNED_SHORT << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_SPECTRAL_SENSITIVITY, exifFlags | ExifTag.TYPE_ASCII << 16 | ExifTag.SIZE_UNDEFINED);
        mTagInfo.put(ExifInterface.TAG_ISO_SPEED_RATINGS, exifFlags | ExifTag.TYPE_UNSIGNED_SHORT << 16 | ExifTag.SIZE_UNDEFINED);
        mTagInfo.put(ExifInterface.TAG_OECF, exifFlags | ExifTag.TYPE_UNDEFINED << 16 | ExifTag.SIZE_UNDEFINED);
        mTagInfo.put(ExifInterface.TAG_SHUTTER_SPEED_VALUE, exifFlags | ExifTag.TYPE_RATIONAL << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_APERTURE_VALUE, exifFlags | ExifTag.TYPE_UNSIGNED_RATIONAL << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_BRIGHTNESS_VALUE, exifFlags | ExifTag.TYPE_RATIONAL << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_EXPOSURE_BIAS_VALUE, exifFlags | ExifTag.TYPE_RATIONAL << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_MAX_APERTURE_VALUE, exifFlags | ExifTag.TYPE_UNSIGNED_RATIONAL << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_SUBJECT_DISTANCE, exifFlags | ExifTag.TYPE_UNSIGNED_RATIONAL << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_METERING_MODE, exifFlags | ExifTag.TYPE_UNSIGNED_SHORT << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_LIGHT_SOURCE, exifFlags | ExifTag.TYPE_UNSIGNED_SHORT << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_FLASH, exifFlags | ExifTag.TYPE_UNSIGNED_SHORT << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_FOCAL_LENGTH, exifFlags | ExifTag.TYPE_UNSIGNED_RATIONAL << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_SUBJECT_AREA, exifFlags | ExifTag.TYPE_UNSIGNED_SHORT << 16 | ExifTag.SIZE_UNDEFINED);
        mTagInfo.put(ExifInterface.TAG_FLASH_ENERGY, exifFlags | ExifTag.TYPE_UNSIGNED_RATIONAL << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_SPATIAL_FREQUENCY_RESPONSE, exifFlags | ExifTag.TYPE_UNDEFINED << 16 | ExifTag.SIZE_UNDEFINED);
        mTagInfo.put(ExifInterface.TAG_FOCAL_PLANE_X_RESOLUTION, exifFlags | ExifTag.TYPE_UNSIGNED_RATIONAL << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_FOCAL_PLANE_Y_RESOLUTION, exifFlags | ExifTag.TYPE_UNSIGNED_RATIONAL << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_FOCAL_PLANE_RESOLUTION_UNIT, exifFlags | ExifTag.TYPE_UNSIGNED_SHORT << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_SUBJECT_LOCATION, exifFlags | ExifTag.TYPE_UNSIGNED_SHORT << 16 | 2);
        mTagInfo.put(ExifInterface.TAG_EXPOSURE_INDEX, exifFlags | ExifTag.TYPE_UNSIGNED_RATIONAL << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_SENSING_METHOD, exifFlags | ExifTag.TYPE_UNSIGNED_SHORT << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_FILE_SOURCE, exifFlags | ExifTag.TYPE_UNDEFINED << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_SCENE_TYPE, exifFlags | ExifTag.TYPE_UNDEFINED << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_CFA_PATTERN, exifFlags | ExifTag.TYPE_UNDEFINED << 16 | ExifTag.SIZE_UNDEFINED);
        mTagInfo.put(ExifInterface.TAG_CUSTOM_RENDERED, exifFlags | ExifTag.TYPE_UNSIGNED_SHORT << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_EXPOSURE_MODE, exifFlags | ExifTag.TYPE_UNSIGNED_SHORT << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_WHITE_BALANCE, exifFlags | ExifTag.TYPE_UNSIGNED_SHORT << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_DIGITAL_ZOOM_RATIO, exifFlags | ExifTag.TYPE_UNSIGNED_RATIONAL << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_FOCAL_LENGTH_IN_35_MM_FILE, exifFlags | ExifTag.TYPE_UNSIGNED_SHORT << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_SCENE_CAPTURE_TYPE, exifFlags | ExifTag.TYPE_UNSIGNED_SHORT << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_GAIN_CONTROL, exifFlags | ExifTag.TYPE_UNSIGNED_RATIONAL << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_CONTRAST, exifFlags | ExifTag.TYPE_UNSIGNED_SHORT << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_SATURATION, exifFlags | ExifTag.TYPE_UNSIGNED_SHORT << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_SHARPNESS, exifFlags | ExifTag.TYPE_UNSIGNED_SHORT << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_DEVICE_SETTING_DESCRIPTION, exifFlags | ExifTag.TYPE_UNDEFINED << 16 | ExifTag.SIZE_UNDEFINED);
        mTagInfo.put(ExifInterface.TAG_SUBJECT_DISTANCE_RANGE, exifFlags | ExifTag.TYPE_UNSIGNED_SHORT << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_INTEROPERABILITY_IFD, exifFlags | ExifTag.TYPE_UNSIGNED_LONG << 16 | 1);
        // GPS tag
        int[] gpsAllowedIfds = {
                IfdId.TYPE_IFD_GPS
        };
        int gpsFlags = getFlagsFromAllowedIfds(gpsAllowedIfds) << 24;
        mTagInfo.put(ExifInterface.TAG_GPS_VERSION_ID, gpsFlags | ExifTag.TYPE_UNSIGNED_BYTE << 16 | 4);
        mTagInfo.put(ExifInterface.TAG_GPS_LATITUDE_REF, gpsFlags | ExifTag.TYPE_ASCII << 16 | 2);
        mTagInfo.put(ExifInterface.TAG_GPS_LONGITUDE_REF, gpsFlags | ExifTag.TYPE_ASCII << 16 | 2);
        mTagInfo.put(ExifInterface.TAG_GPS_LATITUDE, gpsFlags | ExifTag.TYPE_RATIONAL << 16 | 3);
        mTagInfo.put(ExifInterface.TAG_GPS_LONGITUDE, gpsFlags | ExifTag.TYPE_RATIONAL << 16 | 3);
        mTagInfo.put(ExifInterface.TAG_GPS_ALTITUDE_REF, gpsFlags | ExifTag.TYPE_UNSIGNED_BYTE << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_GPS_ALTITUDE, gpsFlags | ExifTag.TYPE_UNSIGNED_RATIONAL << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_GPS_TIME_STAMP, gpsFlags | ExifTag.TYPE_UNSIGNED_RATIONAL << 16 | 3);
        mTagInfo.put(ExifInterface.TAG_GPS_SATTELLITES, gpsFlags | ExifTag.TYPE_ASCII << 16 | ExifTag.SIZE_UNDEFINED);
        mTagInfo.put(ExifInterface.TAG_GPS_STATUS, gpsFlags | ExifTag.TYPE_ASCII << 16 | 2);
        mTagInfo.put(ExifInterface.TAG_GPS_MEASURE_MODE, gpsFlags | ExifTag.TYPE_ASCII << 16 | 2);
        mTagInfo.put(ExifInterface.TAG_GPS_DOP, gpsFlags | ExifTag.TYPE_UNSIGNED_RATIONAL << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_GPS_SPEED_REF, gpsFlags | ExifTag.TYPE_ASCII << 16 | 2);
        mTagInfo.put(ExifInterface.TAG_GPS_SPEED, gpsFlags | ExifTag.TYPE_UNSIGNED_RATIONAL << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_GPS_TRACK_REF, gpsFlags | ExifTag.TYPE_ASCII << 16 | 2);
        mTagInfo.put(ExifInterface.TAG_GPS_TRACK, gpsFlags | ExifTag.TYPE_UNSIGNED_RATIONAL << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_GPS_IMG_DIRECTION_REF, gpsFlags | ExifTag.TYPE_ASCII << 16 | 2);
        mTagInfo.put(ExifInterface.TAG_GPS_IMG_DIRECTION, gpsFlags | ExifTag.TYPE_UNSIGNED_RATIONAL << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_GPS_MAP_DATUM, gpsFlags | ExifTag.TYPE_ASCII << 16 | ExifTag.SIZE_UNDEFINED);
        mTagInfo.put(ExifInterface.TAG_GPS_DEST_LATITUDE_REF, gpsFlags | ExifTag.TYPE_ASCII << 16 | 2);
        mTagInfo.put(ExifInterface.TAG_GPS_DEST_LATITUDE, gpsFlags | ExifTag.TYPE_UNSIGNED_RATIONAL << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_GPS_DEST_BEARING_REF, gpsFlags | ExifTag.TYPE_ASCII << 16 | 2);
        mTagInfo.put(ExifInterface.TAG_GPS_DEST_BEARING, gpsFlags | ExifTag.TYPE_UNSIGNED_RATIONAL << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_GPS_DEST_DISTANCE_REF, gpsFlags | ExifTag.TYPE_ASCII << 16 | 2);
        mTagInfo.put(ExifInterface.TAG_GPS_DEST_DISTANCE, gpsFlags | ExifTag.TYPE_UNSIGNED_RATIONAL << 16 | 1);
        mTagInfo.put(ExifInterface.TAG_GPS_PROCESSING_METHOD, gpsFlags | ExifTag.TYPE_UNDEFINED << 16 | ExifTag.SIZE_UNDEFINED);
        mTagInfo.put(ExifInterface.TAG_GPS_AREA_INFORMATION, gpsFlags | ExifTag.TYPE_UNDEFINED << 16 | ExifTag.SIZE_UNDEFINED);
        mTagInfo.put(ExifInterface.TAG_GPS_DATE_STAMP, gpsFlags | ExifTag.TYPE_ASCII << 16 | 11);
        mTagInfo.put(ExifInterface.TAG_GPS_DIFFERENTIAL, gpsFlags | ExifTag.TYPE_UNSIGNED_SHORT << 16 | 11);
        // Interoperability tag
        int[] interopAllowedIfds = {
                IfdId.TYPE_IFD_INTEROPERABILITY
        };
        int interopFlags = getFlagsFromAllowedIfds(interopAllowedIfds) << 24;
        mTagInfo.put(TAG_INTEROPERABILITY_INDEX, interopFlags | ExifTag.TYPE_ASCII << 16 | ExifTag.SIZE_UNDEFINED);
    }

    protected static int getAllowedIfdFlagsFromInfo(int info) {
        return info >>> 24;
    }

    protected static boolean isIfdAllowed(int info, int ifd) {
        int[] ifds = IfdData.getIfds();
        int ifdFlags = getAllowedIfdFlagsFromInfo(info);
        for (int i = 0; i < ifds.length; i++) {
            if (ifd == ifds[i] && ((ifdFlags >> i) & 1) == 1) {
                return true;
            }
        }
        return false;
    }

    protected static int getFlagsFromAllowedIfds(int[] allowedIfds) {
        if (allowedIfds == null || allowedIfds.length == 0) {
            return 0;
        }
        int flags = 0;
        int[] ifds = IfdData.getIfds();
        for (int i = 0; i < IfdId.TYPE_IFD_COUNT; i++) {
            for (int j : allowedIfds) {
                if (ifds[i] == j) {
                    flags |= 1 << i;
                    break;
                }
            }
        }
        return flags;
    }

    protected static short getTypeFromInfo(int info) {
        return (short) ((info >> 16) & 0x0ff);
    }

    protected static int getComponentCountFromInfo(int info) {
        return info & 0x0ffff;
    }

}
