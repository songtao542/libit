package com.liabit.util

import java.util.*
import kotlin.collections.HashMap

@Suppress("MemberVisibilityCanBePrivate", "unused")
object MimeMap {
    // Contain only lowercase, valid keys/values.
    private val mMimeToExt: MutableMap<String, String> = HashMap()
    private val mExtToMime: MutableMap<String, String> = HashMap()

    init {
        //data from libcore/luni/src/main/java/libcore/net/mime.android.types
        //data from libcore/luni/src/main/java/libcore/net/mime.types
        addMimeMapping("application/epub+zip", "epub")
        addMimeMapping("application/lrc", "lrc")
        addMimeMapping("application/pkix-cert", "cer")
        addMimeMapping("application/rss+xml", "rss")
        addMimeMapping("application/sdp", "sdp")
        addMimeMapping("application/smil+xml", "smil")
        addMimeMapping("application/ttml+xml", listOf("ttml", "dfxp"))
        addMimeMapping("application/vnd.android.ota", "ota")
        addMimeMapping("application/vnd.apple.mpegurl", "m3u8")
        addMimeMapping("application/vnd.ms-pki.stl", "stl")
        addMimeMapping("application/vnd.ms-powerpoint", "pot")
        addMimeMapping("application/vnd.ms-wpl", "wpl")
        addMimeMapping("application/vnd.stardivision.impress", "sdp")
        addMimeMapping("application/vnd.stardivision.writer", "vor")
        addMimeMapping("application/vnd.youtube.yt", "yt")
        addMimeMapping("application/x-android-drm-fl", "fl")
        addMimeMapping("application/x-flac", "flac")
        addMimeMapping("application/x-font", "pcf")
        addMimeMapping("application/x-mobipocket-ebook", listOf("prc", "mobi"))
        addMimeMapping("application/x-mpegurl", listOf("m3u", "m3u8"))
        addMimeMapping("application/x-pem-file", "pem")
        addMimeMapping("application/x-pkcs12", listOf("p12", "pfx"))
        addMimeMapping("application/x-subrip", "srt")
        addMimeMapping("application/x-webarchive", "webarchive")
        addMimeMapping("application/x-webarchive-xml", "webarchivexml")
        addMimeMapping("application/x-x509-server-cert", "crt")
        addMimeMapping("application/x-x509-user-cert", "crt")
        addMimeMapping("application/x-wifi-config", "xml")
        addMimeMapping("application/pgp-signature", "pgp")
        addMimeMapping("application/x-x509-ca-cert", "crt")

        addMimeMapping("audio/3gpp", listOf("3ga", "3gpp"))
        addMimeMapping("audio/aac-adts", "aac")
        addMimeMapping("audio/ac3", listOf("ac3", "a52"))
        addMimeMapping("audio/amr", "amr")
        addMimeMapping("audio/imelody", "imy")
        addMimeMapping("audio/midi", listOf("rtttl", "xmf"))
        addMimeMapping("audio/mobile-xmf", "mxmf")
        addMimeMapping("audio/mp4", listOf("m4a", "m4b", "m4p", "f4a", "f4b", "f4p"))
        addMimeMapping("audio/mpegurl", "m3u")
        addMimeMapping("audio/sp-midi", "smf")
        addMimeMapping("audio/x-matroska", "mka")
        addMimeMapping("audio/x-pn-realaudio", "ra")
        addMimeMapping("audio/x-mpeg", "mp3")
        addMimeMapping("audio/aac", listOf("aac", "adts", "adt"))
        addMimeMapping("audio/basic", "snd")
        addMimeMapping("audio/flac", "flac")
        addMimeMapping("audio/midi", "rtx")
        addMimeMapping("audio/mpeg", listOf("mp3", "mp2", "mp1", "mpa", "m4a", "m4r"))
        addMimeMapping("audio/x-mpegurl", listOf("m3u", "m3u8"))

        addMimeMapping("image/jpeg", listOf("jpg", "jpeg", "jpe"))
        addMimeMapping("image/png", "png")
        addMimeMapping("image/bmp", listOf("bmp", "dib"))
        addMimeMapping("image/gif", "gif")
        addMimeMapping("image/vnd.wap.wbmp", "wbmp")
        addMimeMapping("image/svg+xml", listOf("svg", "svgz"))
        addMimeMapping("image/vnd.adobe.photoshop", "psd")
        addMimeMapping("image/heic", "heic")
        addMimeMapping("image/heic-sequence", "heics")
        addMimeMapping("image/heif", listOf("heif", "hif"))
        addMimeMapping("image/heif-sequence", "heifs")
        addMimeMapping("image/ico", "cur")
        addMimeMapping("image/webp", "webp")
        addMimeMapping("image/x-adobe-dng", "dng")
        addMimeMapping("image/x-fuji-raf", "raf")
        addMimeMapping("image/x-icon", "ico")
        addMimeMapping("image/x-nikon-nrw", "nrw")
        addMimeMapping("image/x-panasonic-rw2", "rw2")
        addMimeMapping("image/x-pentax-pef", "pef")
        addMimeMapping("image/x-samsung-srw", "srw")
        addMimeMapping("image/x-sony-arw", "arw")
        addMimeMapping("image/x-ms-bmp", "bmp")

        addMimeMapping("text/comma-separated-values", "csv")
        addMimeMapping("text/csv", "csv")
        addMimeMapping("text/rtf", "rtf")
        addMimeMapping("text/plain", listOf("txt", "diff", "po"))
        addMimeMapping("text/text", "phps")
        addMimeMapping("text/xml", "xml")
        addMimeMapping("text/x-vcard", "vcf")
        addMimeMapping("text/x-c++hdr", "hpp")
        addMimeMapping("text/x-c++src", listOf("cpp", "c++", "cxx", "cc"))
        addMimeMapping("text/html", listOf("htm", "html", "shtml"))
        addMimeMapping("text/css", "css")
        addMimeMapping("text/x-java", "java")
        addMimeMapping("text/x-java-source", "java")

        addMimeMapping("video/3gpp2", listOf("3gpp2", "3gp2", "3g2"))
        addMimeMapping("video/3gpp", "3gpp")
        addMimeMapping("video/avi", "avi")
        addMimeMapping("video/m4v", "m4v")
        addMimeMapping("video/mp4", listOf("m4v", "f4v", "mp4v", "mpeg4", "mp4"))
        addMimeMapping("video/mp2p", "mpeg")
        addMimeMapping("video/mp2t", listOf("m2ts", "mts"))
        addMimeMapping("video/mp2ts", "ts")
        addMimeMapping("video/vnd.youtube.yt", "yt")
        addMimeMapping("video/x-webex", "wrf")
        addMimeMapping("video/mpeg", listOf("mpeg", "mpeg2", "mpv2", "mp2v", "m2v", "m2t", "mpeg1", "mpv1", "mp1v", "m1v"))
        addMimeMapping("video/quicktime", "mov")
        addMimeMapping("video/x-matroska", "mkv")
    }

    private fun addMimeMapping(mimeSpec: String, extensionSpecs: List<String>) {
        if (extensionSpecs.isEmpty()) return
        val mime = toLowerCase(mimeSpec)
        mMimeToExt[mime] = toLowerCase(extensionSpecs[0])
        for (ext in extensionSpecs) {
            mExtToMime[toLowerCase(ext)] = mime
        }
    }

    /**
     * Convenience method.
     */
    private fun addMimeMapping(mimeSpec: String, extensionSpec: String) {
        return addMimeMapping(mimeSpec, listOf(extensionSpec))
    }

    /**
     * Returns whether the given case insensitive extension has a registered MIME type.
     *
     * @param extension A file extension without the leading '.'
     * @return Whether a MIME type has been registered for the given case insensitive file extension.
     */
    fun hasExtension(extension: String?): Boolean {
        return guessMimeTypeFromExtension(extension) != null
    }

    /**
     * Returns the MIME type for the given case insensitive file extension, or null
     * if the extension isn't mapped to any.
     *
     * @param extension A file extension without the leading '.'
     * @return The lower-case MIME type registered for the given case insensitive file extension,
     * or null if there is none.
     */
    fun guessMimeTypeFromExtension(extension: String?): String? {
        val ext = extension ?: return null
        return mExtToMime[toLowerCase(ext)]
    }

    /**
     * Returns whether given case insensitive MIME type is mapped to a file extension.
     *
     * @param mimeType A MIME type (i.e. `"text/plain")
     * Whether the given case insensitive MIME type is
     * { #guessMimeTypeFromExtension(String) mapped} to a file extension.`
     */
    fun hasMimeType(mimeType: String?): Boolean {
        return guessExtensionFromMimeType(mimeType) != null
    }

    /**
     * Returns the registered extension for the given case insensitive MIME type. Note that some
     * MIME types map to multiple extensions. This call will return the most
     * common extension for the given MIME type.
     *
     * @param mimeType A MIME type (i.e. text/plain)
     * @return The lower-case file extension (without the leading "." that has been registered for
     * the given case insensitive MIME type, or null if there is none.
     */
    fun guessExtensionFromMimeType(mimeType: String?): String? {
        val mime = mimeType ?: return null
        return mMimeToExt[toLowerCase(mime)]
    }

    /**
     * Returns the set of MIME types that this [MimeMap]
     * [maps to some extension][.hasMimeType]. Note that the reverse mapping might not exist.
     *
     * @return unmodifiable [Set] of MIME types mapped to some extension
     */
    fun mimeTypes(): Set<String> {
        return Collections.unmodifiableSet(mMimeToExt.keys)
    }

    /**
     * Returns the set of extensions that this [MimeMap]
     * [maps to some MIME type][.hasExtension]. Note that the reverse mapping might not exist.
     *
     * @return unmodifiable [Set] of extensions that this [MimeMap] maps to some MIME type
     */
    fun extensions(): Set<String> {
        return Collections.unmodifiableSet(mExtToMime.keys)
    }

    /**
     * Returns the canonical (lowercase) form of the given extension or MIME type.
     */
    private fun toLowerCase(s: String): String {
        return s.toLowerCase(Locale.ROOT)
    }

    override fun toString(): String {
        return "MimeMap[$mMimeToExt, $mExtToMime]"
    }
}