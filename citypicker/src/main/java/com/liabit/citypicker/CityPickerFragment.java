package com.liabit.citypicker;

import com.liabit.citypicker.adapter.OnRequestCitiesListener;
import com.liabit.citypicker.adapter.OnRequestLocationListener;
import com.liabit.citypicker.adapter.OnResultListener;
import com.liabit.citypicker.model.City;
import com.liabit.citypicker.model.HotCity;
import com.liabit.citypicker.model.LocatedCity;

import java.util.List;

import androidx.annotation.StyleRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class CityPickerFragment extends CityPickerDialogFragment {
    private static final String TAG = "CityPickerFragment";


    public static class Builder {
        private FragmentManager mFragmentManager;
        private Fragment mTargetFragment;

        private boolean mMultipleMode;
        private int mAnimStyle;
        private LocatedCity mLocation;
        private List<HotCity> mHotCities;
        private List<City> mCities;
        private boolean mEnableHotCities;
        private boolean mEnableLocation;
        private boolean mUserDefaultCities = true;
        private boolean mSingleMode = false;
        private OnResultListener mOnResultListener;
        private OnRequestLocationListener mOnRequestLocationListener;
        private OnRequestCitiesListener mOnRequestCitiesListener;

        public Builder() {
        }

        public CityPickerFragment.Builder fragmentManager(FragmentManager fm) {
            mFragmentManager = fm;
            return this;
        }

        public CityPickerFragment.Builder targetFragment(Fragment targetFragment) {
            mTargetFragment = targetFragment;
            return this;
        }

        /**
         * 设置动画效果
         *
         * @param animStyle
         * @return
         */
        public CityPickerFragment.Builder animationStyle(@StyleRes int animStyle) {
            mAnimStyle = animStyle;
            return this;
        }

        /**
         * 设置多选模式
         *
         * @param multipleMode
         * @return
         */
        public CityPickerFragment.Builder multipleMode(boolean multipleMode) {
            mMultipleMode = multipleMode;
            return this;
        }

        /**
         * 设置当前已经定位的城市
         *
         * @param location
         * @return
         */
        public CityPickerFragment.Builder locatedCity(LocatedCity location) {
            mLocation = location;
            return this;
        }

        public CityPickerFragment.Builder hotCities(List<HotCity> data) {
            mHotCities = data;
            return this;
        }

        public CityPickerFragment.Builder cities(List<City> data) {
            mCities = data;
            return this;
        }

        public CityPickerFragment.Builder enableHotCities(boolean enable) {
            mEnableHotCities = enable;
            return this;
        }

        public CityPickerFragment.Builder enableLocation(boolean enable) {
            mEnableLocation = enable;
            return this;
        }

        /**
         * 从自带的城市数据库中加载城市列表
         */
        public CityPickerFragment.Builder useDefaultCities(boolean use) {
            mUserDefaultCities = use;
            return this;
        }

        /**
         * 设置选择结果的监听器
         *
         * @param listener
         * @return
         */
        public CityPickerFragment.Builder resultListener(OnResultListener listener) {
            mOnResultListener = listener;
            return this;
        }

        /**
         * 设置选择结果的监听器
         *
         * @param listener
         * @return
         */
        public CityPickerFragment.Builder requestLocationListener(OnRequestLocationListener listener) {
            mOnRequestLocationListener = listener;
            return this;
        }

        /**
         * 设置选择结果的监听器
         *
         * @param listener
         * @return
         */
        public CityPickerFragment.Builder requestCitiesListener(OnRequestCitiesListener listener) {
            mOnRequestCitiesListener = listener;
            return this;
        }


        public CityPickerFragment show() {
            final CityPickerFragment cityPickerFragment = new CityPickerFragment();
            cityPickerFragment.setLocatedCity(mLocation);
            cityPickerFragment.setCities(mCities);
            cityPickerFragment.setHotCities(mHotCities);
            cityPickerFragment.enableHotCities(mEnableHotCities);
            cityPickerFragment.enableLocation(mEnableLocation);
            cityPickerFragment.setAnimationStyle(mAnimStyle);
            cityPickerFragment.useDefaultCities(mUserDefaultCities);
            cityPickerFragment.setMultipleMode(mMultipleMode);
            cityPickerFragment.setOnResultListener(mOnResultListener);
            cityPickerFragment.setOnRequestLocationListener(mOnRequestLocationListener);
            cityPickerFragment.setOnRequestCitiesListener(mOnRequestCitiesListener);
            if (mTargetFragment != null) {
                cityPickerFragment.setTargetFragment(mTargetFragment, 0);
            }

            if (mFragmentManager != null) {
                FragmentTransaction ft = mFragmentManager.beginTransaction();
                final Fragment prev = mFragmentManager.findFragmentByTag(TAG);
                if (prev != null) {
                    ft.remove(prev).commit();
                    ft = mFragmentManager.beginTransaction();
                }
                ft.addToBackStack(null);
                cityPickerFragment.show(ft, TAG);
            }
            return cityPickerFragment;
        }
    }
}
