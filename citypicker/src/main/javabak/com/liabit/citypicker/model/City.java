package com.liabit.citypicker.model;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;

/**
 * author Bro0cL on 2016/1/26.
 */
public class City {
    private String name;
    private String code;
    private String province;
    private String provinceCode;
    private String pinyin;

    private boolean selected = false;

    public City(String name, String province, String pinyin, String code) {
        this.name = name;
        this.province = province;
        this.pinyin = pinyin;
        this.code = code;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getProvinceCode() {
        return this.provinceCode;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    /***
     * 获取悬浮栏文本，（#、定位、热门 需要特殊处理）
     * @return
     */
    public String getSection() {
        if (TextUtils.isEmpty(pinyin)) {
            return "#";
        } else {
            String c = pinyin.substring(0, 1);
            Pattern p = Pattern.compile("[a-zA-Z]");
            Matcher m = p.matcher(c);
            if (m.matches()) {
                return c.toUpperCase();
            }
            //在添加定位和热门数据时设置的section就是‘定’、’热‘开头
            else if (TextUtils.equals(c, "定") || TextUtils.equals(c, "热"))
                return pinyin;
            else
                return "#";
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    @NonNull
    @Override
    public String toString() {
        return "{name:" + name + ",province:" + province + ",pinyin:" + pinyin + ",code:" + code + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof com.liabit.listpicker.model.Item) {
            com.liabit.listpicker.model.Item other = (com.liabit.listpicker.model.Item) obj;
            if (name.equals(other.name) &&
                    province.equals(other.province) &&
                    pinyin.equals(other.pinyin) &&
                    code.equals(other.code)) {
                return true;
            }
        }
        return false;
    }
}
