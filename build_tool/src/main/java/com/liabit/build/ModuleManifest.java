package com.liabit.build;

import java.util.HashMap;

public class ModuleManifest {
    public static final HashMap<String, String> moduleMap = new HashMap<String, String>() {
        {
            put("wallpaper_cropper", "../wallpaper_cropper/src/main");
            put("wallpaper_cropper_lite", "../wallpaper_cropper_lite/src/main");
            put("color_util", "../color_util/src/main");
            put("settings", "../settings/src/main");
            put("gesture", "../gesture/src/main");
            put("swipeback", "../swipeback/src/main");
            put("screenrecord", "../screenrecord/src/main");
            put("screencapture", "../screencapture/src/main");
            put("location_picker", "../location_picker/src/main");
            put("tablayout", "../tablayout/src/main");
            put("util", "../util/src/main");
            put("shimmer", "../shimmer/src/main");
            put("addsub", "../addsub/src/main");
            put("utils", "../utils/src/main");
            put("autoclear", "../autoclear/src/main");
            put("photoview", "../photoview/src/main");
            put("picker_integrate", "../picker_integrate/src/main");
            put("photopicker", "../photopicker/src/main");
            put("matisse", "../matisse/src/main");
            put("matisse_crop", "../matisse/src_crop/main");
            put("citypicker", "../citypicker/src/main");
            put("picker", "../picker/src/main");
            put("numberpicker", "../numberpicker/src/main");
            put("tagview", "../tagview/src/main");
            put("filterlayout", "../filterlayout/src/main");
            put("dialog", "../dialog/src/main");
            put("timerview", "../timerview/src/main");
            put("viewbinding", "../viewbinding/src/main");
            put("viewmodel", "../viewmodel/src/main");
            put("viewmodel_inject", "../viewmodel_inject/src/main");
            put("recyclerview", "../recyclerview/src/main");
            put("popup", "../popup/src/main");
            put("widget", "../widget/src/main");
            put("widget_empty_view", "../widget_empty_view/src/main");
            put("widget_label_view", "../widget_label_view/src/main");
            put("widget_progress_button", "../widget_progress_button/src/main");
            put("widget_spring_button", "../widget_spring_button/src/main");
            put("widget_state_button", "../widget_state_button/src/main");
            put("ext", "../ext/src/main");
            put("livedata-ktx", "../livedata-ktx/src/main");
            put("base", "../base/src/main");
            put("base_with_viewbinding", "../base_with_viewbinding/src/main");
            put("base_with_viewmodel", "../base_with_viewmodel/src/main");
            put("base_with_vb_vm", "../base_with_vb_vm/src/main");
            put("third-auth", "../third-auth/src/main");
            put("compressor", "../compressor/src/main");
            put("retrofit-ext", "../retrofit-ext/src/main");
            put("flag", "../flag/src/main");
            put("flag-square", "../flag-square/src/main");
            put("imageloader", "../imageloader/src/main");
        }
    };

    public static final String[] sportMergeList = {
            /*moduleMap.get("wallpaper_cropper"),
            moduleMap.get("wallpaper_cropper_lite"),
            moduleMap.get("color_util"),
            moduleMap.get("settings"),
            moduleMap.get("gesture"),
            moduleMap.get("swipeback"),
            moduleMap.get("screenrecord"),
            moduleMap.get("screencapture"),
            moduleMap.get("location_picker"),
            moduleMap.get("tablayout"),
            moduleMap.get("util"),
            moduleMap.get("shimmer"),*/
            moduleMap.get("addsub"),
            moduleMap.get("utils"),
            moduleMap.get("autoclear"),
            moduleMap.get("photoview"),
            moduleMap.get("picker_integrate"),
            moduleMap.get("photopicker"),
            moduleMap.get("matisse"),
            moduleMap.get("matisse"),
            moduleMap.get("citypicker"),
            moduleMap.get("picker"),
            moduleMap.get("numberpicker"),
            moduleMap.get("tagview"),
            moduleMap.get("filterlayout"),
            moduleMap.get("dialog"),
            moduleMap.get("timerview"),
            moduleMap.get("viewbinding"),
            moduleMap.get("viewmodel"),
            moduleMap.get("viewmodel_inject"),
            moduleMap.get("recyclerview"),
            moduleMap.get("popup"),
            moduleMap.get("widget"),
            moduleMap.get("widget_empty_view"),
            moduleMap.get("widget_label_view"),
            moduleMap.get("widget_progress_button"),
            moduleMap.get("widget_spring_button"),
            moduleMap.get("widget_state_button"),
            moduleMap.get("ext"),
            moduleMap.get("livedata-ktx"),
            moduleMap.get("base"),
            moduleMap.get("base_with_viewbinding"),
            moduleMap.get("base_with_viewmodel"),
            moduleMap.get("third-auth"),
            moduleMap.get("compressor"),
            moduleMap.get("retrofit-ext"),
    };

    public static final String[] themeStoreMergeList = {
            /*moduleMap.get("wallpaper_cropper"),
            moduleMap.get("wallpaper_cropper_lite"),
            moduleMap.get("color_util"),
            moduleMap.get("settings"),
            moduleMap.get("gesture"),
            moduleMap.get("swipeback"),
            moduleMap.get("screenrecord"),
            moduleMap.get("screencapture"),
            moduleMap.get("location_picker"),
            moduleMap.get("tablayout"),
            moduleMap.get("util"),
            moduleMap.get("addsub"),
            moduleMap.get("picker_integrate"),
            moduleMap.get("citypicker"),
            moduleMap.get("picker"),
            moduleMap.get("numberpicker"),
            moduleMap.get("tagview"),
            moduleMap.get("filterlayout"),
            moduleMap.get("compressor"),
            moduleMap.get("third-auth"),
            moduleMap.get("shimmer"),*/
            moduleMap.get("utils"),
            moduleMap.get("timerview"),
            moduleMap.get("photoview"),
            moduleMap.get("autoclear"),
            moduleMap.get("dialog"),
            moduleMap.get("viewbinding"),
            moduleMap.get("viewmodel"),
            moduleMap.get("viewmodel_inject"),
            moduleMap.get("recyclerview"),
            moduleMap.get("popup"),
            moduleMap.get("widget"),
            moduleMap.get("widget_empty_view"),
            moduleMap.get("widget_label_view"),
            //moduleMap.get("widget_progress_button"),
            //moduleMap.get("widget_spring_button"),
            //moduleMap.get("widget_state_button"),
            moduleMap.get("ext"),
            moduleMap.get("livedata-ktx"),
            moduleMap.get("base"),
            moduleMap.get("base_with_viewbinding"),
            moduleMap.get("base_with_viewmodel"),
            moduleMap.get("base_with_vb_vm"),
            moduleMap.get("retrofit-ext"),
    };

    public static final String[] scaffoldMergeList = themeStoreMergeList;

    public static final String[] vpnMergeList = {
            /*moduleMap.get("wallpaper_cropper"),
            moduleMap.get("wallpaper_cropper_lite"),
            moduleMap.get("color_util"),
            moduleMap.get("settings"),
            moduleMap.get("gesture"),
            moduleMap.get("swipeback"),
            moduleMap.get("screenrecord"),
            moduleMap.get("screencapture"),
            moduleMap.get("tablayout"),
            moduleMap.get("util"),
            moduleMap.get("addsub"),
            moduleMap.get("picker_integrate"),
            moduleMap.get("citypicker"),
            moduleMap.get("picker"),
            moduleMap.get("numberpicker"),
            moduleMap.get("tagview"),
            moduleMap.get("filterlayout"),
            moduleMap.get("compressor"),
            moduleMap.get("third-auth"),
            moduleMap.get("photoview"),
            moduleMap.get("recyclerview"),
            moduleMap.get("popup"),
            moduleMap.get("livedata-ktx"),
            moduleMap.get("base_with_viewbinding"),
            moduleMap.get("base_with_viewmodel"),
            moduleMap.get("location_picker"),
            moduleMap.get("viewbinding"),
            moduleMap.get("viewmodel"),
            moduleMap.get("viewmodel_inject"),
            moduleMap.get("widget_label_view"),
            moduleMap.get("widget_progress_button"),
            moduleMap.get("widget_spring_button"),
            moduleMap.get("widget_state_button"),
            moduleMap.get("shimmer"),*/
            moduleMap.get("widget"),
            moduleMap.get("widget_empty_view"),
            moduleMap.get("timerview"),
            moduleMap.get("autoclear"),
            moduleMap.get("flag"),
            moduleMap.get("ext"),
            moduleMap.get("dialog"),
            moduleMap.get("utils"),
            moduleMap.get("retrofit-ext"),
            moduleMap.get("imageloader"),
            moduleMap.get("base"),
    };


    public static final String[] refreshMergeList = {
            /*moduleMap.get("wallpaper_cropper"),
            moduleMap.get("wallpaper_cropper_lite"),
            moduleMap.get("color_util"),
            moduleMap.get("settings"),
            moduleMap.get("gesture"),
            moduleMap.get("swipeback"),
            moduleMap.get("screenrecord"),
            moduleMap.get("screencapture"),
            moduleMap.get("tablayout"),
            moduleMap.get("util"),
            moduleMap.get("addsub"),
            moduleMap.get("picker_integrate"),
            moduleMap.get("citypicker"),
            moduleMap.get("picker"),
            moduleMap.get("numberpicker"),
            moduleMap.get("tagview"),
            moduleMap.get("filterlayout"),
            moduleMap.get("compressor"),
            moduleMap.get("third-auth"),
            moduleMap.get("photoview"),
            moduleMap.get("dialog"),
            moduleMap.get("recyclerview"),
            moduleMap.get("popup"),
            moduleMap.get("livedata-ktx"),
            moduleMap.get("location_picker"),
            moduleMap.get("widget_progress_button"),
            moduleMap.get("widget_spring_button"),
            moduleMap.get("widget_state_button"),
            moduleMap.get("shimmer"),*/
            moduleMap.get("widget"),
            moduleMap.get("widget_empty_view"),
            moduleMap.get("timerview"),
            moduleMap.get("autoclear"),
            moduleMap.get("flag"),
            moduleMap.get("ext"),
            moduleMap.get("utils"),
            moduleMap.get("retrofit-ext"),
            moduleMap.get("imageloader"),
            moduleMap.get("viewbinding"),
            moduleMap.get("widget_label_view"),
            //moduleMap.get("viewmodel"),
            //moduleMap.get("base_with_viewmodel"),
            //moduleMap.get("viewmodel_inject"),
            moduleMap.get("base"),
            moduleMap.get("base_with_viewbinding"),
    };
}
