package com.liabit.util

import android.mtp.MtpConstants
import java.util.*

@Suppress("MemberVisibilityCanBePrivate", "unused")
object MediaFile {

    // maps mime type to MTP format code
    private val mMimeTypeToFormatMap = HashMap<String, Int>()

    // maps MTP format code to mime type
    private val mFormatToMimeTypeMap = HashMap<Int, String>()

    private fun addFileType(mtpFormatCode: Int, mimeType: String) {
        if (!mMimeTypeToFormatMap.containsKey(mimeType)) {
            mMimeTypeToFormatMap[mimeType] = mtpFormatCode
        }
        if (!mFormatToMimeTypeMap.containsKey(mtpFormatCode)) {
            mFormatToMimeTypeMap[mtpFormatCode] = mimeType
        }
    }

    /**
     * The MIME type for data whose type is otherwise unknown.
     *
     *
     * Per RFC 2046, the "application" media type is to be used for discrete
     * data which do not fit in any of the other categories, and the
     * "octet-stream" subtype is used to indicate that a body contains arbitrary
     * binary data.
     */
    private const val MIMETYPE_UNKNOWN = "application/octet-stream"

    /**
     * Format code for HEIF files
     */
    private const val FORMAT_HEIF = 0x3812

    /**
     * Format code for DNG files
     */
    private const val FORMAT_DNG = 0x3811

    /**
     * Format code for unknown image files.
     *
     *
     * Will be used for the formats which are not specified in PTP specification.
     * For instance, WEBP and WBMP.
     */
    private const val FORMAT_DEFINED = 0x3800

    init {
        addFileType(MtpConstants.FORMAT_MP3, "audio/mpeg")
        addFileType(MtpConstants.FORMAT_WAV, "audio/x-wav")
        addFileType(MtpConstants.FORMAT_WMA, "audio/x-ms-wma")
        addFileType(MtpConstants.FORMAT_OGG, "audio/ogg")
        addFileType(MtpConstants.FORMAT_AAC, "audio/aac")
        addFileType(MtpConstants.FORMAT_FLAC, "audio/flac")
        addFileType(MtpConstants.FORMAT_AIFF, "audio/x-aiff")
        addFileType(MtpConstants.FORMAT_MP2, "audio/mpeg")
        addFileType(MtpConstants.FORMAT_MPEG, "video/mpeg")
        addFileType(MtpConstants.FORMAT_MP4_CONTAINER, "video/mp4")
        addFileType(MtpConstants.FORMAT_3GP_CONTAINER, "video/3gpp")
        addFileType(MtpConstants.FORMAT_3GP_CONTAINER, "video/3gpp2")
        addFileType(MtpConstants.FORMAT_AVI, "video/avi")
        addFileType(MtpConstants.FORMAT_WMV, "video/x-ms-wmv")
        addFileType(MtpConstants.FORMAT_ASF, "video/x-ms-asf")
        addFileType(MtpConstants.FORMAT_EXIF_JPEG, "image/jpeg")
        addFileType(MtpConstants.FORMAT_GIF, "image/gif")
        addFileType(MtpConstants.FORMAT_PNG, "image/png")
        addFileType(MtpConstants.FORMAT_BMP, "image/x-ms-bmp")
        addFileType(FORMAT_HEIF, "image/heif")
        addFileType(FORMAT_DNG, "image/x-adobe-dng")
        addFileType(MtpConstants.FORMAT_TIFF, "image/tiff")
        addFileType(MtpConstants.FORMAT_TIFF, "image/x-canon-cr2")
        addFileType(MtpConstants.FORMAT_TIFF, "image/x-nikon-nrw")
        addFileType(MtpConstants.FORMAT_TIFF, "image/x-sony-arw")
        addFileType(MtpConstants.FORMAT_TIFF, "image/x-panasonic-rw2")
        addFileType(MtpConstants.FORMAT_TIFF, "image/x-olympus-orf")
        addFileType(MtpConstants.FORMAT_TIFF, "image/x-pentax-pef")
        addFileType(MtpConstants.FORMAT_TIFF, "image/x-samsung-srw")
        addFileType(MtpConstants.FORMAT_TIFF_EP, "image/tiff")
        addFileType(MtpConstants.FORMAT_TIFF_EP, "image/x-nikon-nef")
        addFileType(MtpConstants.FORMAT_JP2, "image/jp2")
        addFileType(MtpConstants.FORMAT_JPX, "image/jpx")
        addFileType(MtpConstants.FORMAT_M3U_PLAYLIST, "audio/x-mpegurl")
        addFileType(MtpConstants.FORMAT_PLS_PLAYLIST, "audio/x-scpls")
        addFileType(MtpConstants.FORMAT_WPL_PLAYLIST, "application/vnd.ms-wpl")
        addFileType(MtpConstants.FORMAT_ASX_PLAYLIST, "video/x-ms-asf")
        addFileType(MtpConstants.FORMAT_TEXT, "text/plain")
        addFileType(MtpConstants.FORMAT_HTML, "text/html")
        addFileType(MtpConstants.FORMAT_XML_DOCUMENT, "text/xml")
        addFileType(MtpConstants.FORMAT_MS_WORD_DOCUMENT, "application/msword")
        addFileType(MtpConstants.FORMAT_MS_WORD_DOCUMENT, "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
        addFileType(MtpConstants.FORMAT_MS_EXCEL_SPREADSHEET, "application/vnd.ms-excel")
        addFileType(MtpConstants.FORMAT_MS_EXCEL_SPREADSHEET, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        addFileType(MtpConstants.FORMAT_MS_POWERPOINT_PRESENTATION, "application/vnd.ms-powerpoint")
        addFileType(MtpConstants.FORMAT_MS_POWERPOINT_PRESENTATION, "application/vnd.openxmlformats-officedocument.presentationml.presentation")
    }

    /**
     * Check whether the mime type is document or not.
     *
     * @param mimeType the mime type to check
     * @return true, if the mimeType is matched. Otherwise, false.
     */
    fun isDocumentMimeType(mimeType: String?): Boolean {
        if (mimeType == null) {
            return false
        }
        val normalizedMimeType = normalizeMimeType(mimeType)
        return if (normalizedMimeType.startsWith("text/")) {
            true
        } else when (normalizedMimeType.toLowerCase(Locale.ROOT)) {
            "application/epub+zip",
            "application/msword",
            "application/pdf",
            "application/rtf",
            "application/vnd.ms-excel",
            "application/vnd.ms-excel.addin.macroenabled.12",
            "application/vnd.ms-excel.sheet.binary.macroenabled.12",
            "application/vnd.ms-excel.sheet.macroenabled.12",
            "application/vnd.ms-excel.template.macroenabled.12",
            "application/vnd.ms-powerpoint",
            "application/vnd.ms-powerpoint.addin.macroenabled.12",
            "application/vnd.ms-powerpoint.presentation.macroenabled.12",
            "application/vnd.ms-powerpoint.slideshow.macroenabled.12",
            "application/vnd.ms-powerpoint.template.macroenabled.12",
            "application/vnd.ms-word.document.macroenabled.12",
            "application/vnd.ms-word.template.macroenabled.12",
            "application/vnd.oasis.opendocument.chart",
            "application/vnd.oasis.opendocument.database",
            "application/vnd.oasis.opendocument.formula",
            "application/vnd.oasis.opendocument.graphics",
            "application/vnd.oasis.opendocument.graphics-template",
            "application/vnd.oasis.opendocument.presentation",
            "application/vnd.oasis.opendocument.presentation-template",
            "application/vnd.oasis.opendocument.spreadsheet",
            "application/vnd.oasis.opendocument.spreadsheet-template",
            "application/vnd.oasis.opendocument.text",
            "application/vnd.oasis.opendocument.text-master",
            "application/vnd.oasis.opendocument.text-template",
            "application/vnd.oasis.opendocument.text-web",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "application/vnd.openxmlformats-officedocument.presentationml.slideshow",
            "application/vnd.openxmlformats-officedocument.presentationml.template",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.template",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.template",
            "application/vnd.stardivision.calc",
            "application/vnd.stardivision.chart",
            "application/vnd.stardivision.draw",
            "application/vnd.stardivision.impress",
            "application/vnd.stardivision.impress-packed", "application/vnd.stardivision.mail",
            "application/vnd.stardivision.math",
            "application/vnd.stardivision.writer",
            "application/vnd.stardivision.writer-global", "application/vnd.sun.xml.calc",
            "application/vnd.sun.xml.calc.template",
            "application/vnd.sun.xml.draw",
            "application/vnd.sun.xml.draw.template",
            "application/vnd.sun.xml.impress",
            "application/vnd.sun.xml.impress.template",
            "application/vnd.sun.xml.math",
            "application/vnd.sun.xml.writer",
            "application/vnd.sun.xml.writer.global",
            "application/vnd.sun.xml.writer.template",
            "application/x-mspublisher" -> true
            else -> false
        }
    }

    fun isExifMimeType(mimeType: String?): Boolean {
        // For simplicity, assume that all image files might have EXIF data
        return isImageMimeType(mimeType)
    }

    fun isAudioMimeType(mimeType: String?): Boolean {
        return normalizeMimeType(mimeType).startsWith("audio/")
    }

    fun isVideoMimeType(mimeType: String?): Boolean {
        return normalizeMimeType(mimeType).startsWith("video/")
    }

    fun isImageMimeType(mimeType: String?): Boolean {
        return normalizeMimeType(mimeType).startsWith("image/")
    }

    fun isPlayListMimeType(mimeType: String?): Boolean {
        return when (normalizeMimeType(mimeType)) {
            "application/vnd.ms-wpl",
            "audio/x-mpegurl",
            "audio/mpegurl",
            "application/x-mpegurl",
            "application/vnd.apple.mpegurl",
            "audio/x-scpls" -> true
            else -> false
        }
    }

    fun isDrmMimeType(mimeType: String?): Boolean {
        return normalizeMimeType(mimeType) == "application/x-android-drm-fl"
    }

    // generates a title based on file name
    fun getFileTitle(path: String): String {
        // extract file name after last slash
        var title = path
        var lastSlash = title.lastIndexOf('/')
        if (lastSlash >= 0) {
            lastSlash++
            if (lastSlash < title.length) {
                title = title.substring(lastSlash)
            }
        }
        // truncate the file extension (if any)
        val lastDot = title.lastIndexOf('.')
        if (lastDot > 0) {
            title = title.substring(0, lastDot)
        }
        return title
    }

    fun getFileExtension(path: String?): String? {
        if (path == null) {
            return null
        }
        val lastDot = path.lastIndexOf('.')
        return if (lastDot >= 0) path.substring(lastDot + 1) else null
    }

    /**
     * Find the best MIME type for the given item. Prefers mappings from file
     * extensions, since they're more accurate than format codes.
     */
    fun getMimeType(path: String?, formatCode: Int): String {
        // First look for extension mapping
        val mimeType = getMimeTypeForFile(path)
        // Otherwise look for format mapping
        return if (MIMETYPE_UNKNOWN != mimeType) mimeType else getMimeTypeForFormatCode(formatCode)
    }

    fun getMimeTypeForFile(path: String?): String {
        val ext = getFileExtension(path)
        val mimeType = MimeMap.guessMimeTypeFromExtension(ext)
        return mimeType ?: MIMETYPE_UNKNOWN
    }

    fun getMimeTypeForFormatCode(formatCode: Int): String {
        val mimeType = mFormatToMimeTypeMap[formatCode]
        return mimeType ?: MIMETYPE_UNKNOWN
    }

    /**
     * Find the best MTP format code mapping for the given item. Prefers
     * mappings from MIME types, since they're more accurate than file
     * extensions.
     */
    fun getFormatCode(path: String?, mimeType: String?): Int {
        // First look for MIME type mapping
        val formatCode = getFormatCodeForMimeType(mimeType)
        // Otherwise look for extension mapping
        return if (formatCode != MtpConstants.FORMAT_UNDEFINED) formatCode else getFormatCodeForFile(path)
    }

    fun getFormatCodeForFile(path: String?): Int {
        return getFormatCodeForMimeType(getMimeTypeForFile(path))
    }

    fun getFormatCodeForMimeType(mimeType: String?): Int {
        var mime = mimeType
        if (mime == null) {
            return MtpConstants.FORMAT_UNDEFINED
        }
        // First look for direct mapping
        var value = mMimeTypeToFormatMap[mime]
        if (value != null) {
            return value
        }
        // Otherwise look for indirect mapping
        mime = normalizeMimeType(mime)
        value = mMimeTypeToFormatMap[mime]
        return value ?: when {
            mime.startsWith("audio/") -> MtpConstants.FORMAT_UNDEFINED_AUDIO
            mime.startsWith("video/") -> MtpConstants.FORMAT_UNDEFINED_VIDEO
            mime.startsWith("image/") -> FORMAT_DEFINED
            else -> MtpConstants.FORMAT_UNDEFINED
        }
    }

    /**
     * Normalize the given MIME type by bouncing through a default file
     * extension, if defined. This handles cases like "application/x-flac" to
     * ".flac" to "audio/flac".
     */
    private fun normalizeMimeType(mimeType: String?): String {
        val extension = MimeMap.guessExtensionFromMimeType(mimeType)
        if (extension != null) {
            val extensionMimeType = MimeMap.guessMimeTypeFromExtension(extension)
            if (extensionMimeType != null) {
                return extensionMimeType
            }
        }
        return mimeType ?: MIMETYPE_UNKNOWN
    }

}