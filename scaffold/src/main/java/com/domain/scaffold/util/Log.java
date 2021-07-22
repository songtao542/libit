package com.domain.scaffold.util;

import com.domain.scaffold.library.BuildConfig;

public final class Log {

    private Log() {
    }

    private static String buildTag(String tag) {
        if (BuildConfig.DEBUG) {
            return "TEST:" + tag;
        } else {
            return "TMST:" + tag;
        }
    }

    public static int v(String tag, String msg) {
        return android.util.Log.v(buildTag(tag), msg);
    }

    public static int v(String tag, String msg, Throwable tr) {
        return android.util.Log.v(buildTag(tag), msg, tr);
    }

    public static int d(String tag, String msg) {
        return android.util.Log.d(buildTag(tag), msg);
    }

    public static int d(String tag, String msg, Throwable tr) {
        return android.util.Log.d(buildTag(tag), msg, tr);
    }

    public static int i(String tag, String msg) {
        return android.util.Log.i(buildTag(tag), msg);
    }

    public static int i(String tag, String msg, Throwable tr) {
        return android.util.Log.i(buildTag(tag), msg, tr);
    }

    public static int w(String tag, String msg) {
        return android.util.Log.w(buildTag(tag), msg);
    }

    public static int w(String tag, String msg, Throwable tr) {
        return android.util.Log.w(buildTag(tag), msg, tr);
    }

    public static int w(String tag, Throwable tr) {
        return android.util.Log.w(buildTag(tag), tr);
    }

    public static int e(String tag, String msg) {
        return android.util.Log.e(buildTag(tag), msg);
    }

    public static int e(String tag, String msg, Throwable tr) {
        return android.util.Log.d(buildTag(tag), msg, tr);
    }
}
