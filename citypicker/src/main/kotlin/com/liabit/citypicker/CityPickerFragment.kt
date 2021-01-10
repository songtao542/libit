package com.liabit.citypicker

import android.os.Bundle
import androidx.annotation.StyleRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.loader.app.LoaderManager
import androidx.loader.content.AsyncTaskLoader
import androidx.loader.content.Loader
import com.liabit.listpicker.OnPickerReadyListener
import com.liabit.listpicker.OnResultListener
import com.liabit.listpicker.PickerFragment
import com.liabit.listpicker.model.HotItem
import com.liabit.listpicker.model.VariableState
import java.util.*

class CityPickerFragment : PickerFragment<City>() {

    private var mLocatedCity: LocatedCity? = null
    private var mLocatedState: Int? = null
    private var mUserDefaultCities: Boolean = false
    private var mEnableDefaultCities: Boolean = false
    private var mEnableLocatedCity: Boolean = false

    private var cityProvider: CityProvider? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (mUserDefaultCities) {
            LoaderManager.getInstance(this).initLoader<List<City>>(1, null, object : LoaderManager.LoaderCallbacks<List<City>?> {
                override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<City>?> {
                    return object : AsyncTaskLoader<List<City>>(requireContext()) {
                        override fun onStartLoading() {
                            super.onStartLoading()
                            forceLoad()
                        }

                        override fun loadInBackground(): List<City>? {
                            cityProvider = CityProvider(context)
                            return cityProvider?.allCities
                        }
                    }
                }

                override fun onLoadFinished(loader: Loader<List<City>?>, data: List<City>?) {
                    if (mEnableLocatedCity) {
                        initLocatedCity()
                    }
                    setItem(mLocatedCity, getDefaultHotCities(), data)
                }

                override fun onLoaderReset(loader: Loader<List<City>?>) {}
            })
        }
    }

    private fun initLocatedCity() {
        if (mLocatedCity == null) {
            mLocatedCity = LocatedCity(getString(R.string.cp_located), getString(R.string.cp_locating), getString(R.string.cp_unknown), "0")
            mLocatedState = VariableState.FAILURE
        } else {
            mLocatedState = VariableState.SUCCESS
        }
    }

    private fun getDefaultHotCities(): HotItem<City>? {
        if (!mEnableDefaultCities) {
            return null
        }
        val hotCities = ArrayList<City>()
        hotCities.add(City("北京", "北京", "101010100", "beijing"))
        hotCities.add(City("上海", "上海", "101020100", "shanghai"))
        hotCities.add(City("广州", "广东", "101280101", "guangzhou"))
        hotCities.add(City("深圳", "广东", "101280601", "shenzhen"))
        hotCities.add(City("天津", "天津", "101030100", "tianjin"))
        hotCities.add(City("杭州", "浙江", "101210101", "zhejiang"))
        hotCities.add(City("南京", "江苏", "101190101", "nanjing"))
        hotCities.add(City("成都", "四川", "101270101", "sichuan"))
        hotCities.add(City("武汉", "湖北", "101200101", "wuhan"))
        return HotCities(getString(R.string.cp_hot), hotCities)
    }

    /**
     * 从自带的城市数据库中加载城市列表
     */
    fun setUseDefaultCities(use: Boolean) {
        mUserDefaultCities = use
    }

    fun enableDefaultHotCities(enable: Boolean) {
        mEnableDefaultCities = enable
    }

    fun enableLocatedCity(enable: Boolean) {
        mEnableLocatedCity = enable
    }

    class Builder {
        private var mUserDefaultCities = true
        private var mEnableDefaultCities: Boolean = false
        private var mEnableLocatedCity: Boolean = false

        private var mFragmentManager: FragmentManager? = null
        private var mTargetFragment: Fragment? = null
        private var mMultipleMode = false
        private var mAnimStyle = 0
        private var mTitle: String? = null
        private var mTitleResId = 0
        private var mEnableSearch = true
        private var mLocatedCity: LocatedCity? = null
        private var mHotCities: HotCities? = null
        private var mCities: List<City>? = null
        private var mOnResultListener: OnResultListener<City>? = null
        private var mOnRequestLocationListener: OnRequestLocationListener? = null
        private var mOnPickerReadyListener: OnPickerReadyListener<City>? = null

        /**
         * 从自带的城市数据库中加载城市列表
         */
        fun setUseDefaultCities(use: Boolean): Builder {
            mUserDefaultCities = use
            return this
        }

        fun setDefaultHotCitiesEnabled(enable: Boolean): Builder {
            mEnableDefaultCities = enable
            return this
        }

        fun setLocatedCityEnable(enable: Boolean): Builder {
            mEnableLocatedCity = enable
            return this
        }

        fun setFragmentManager(fm: FragmentManager?): Builder {
            mFragmentManager = fm
            return this
        }

        fun setTargetFragment(targetFragment: Fragment?): Builder {
            mTargetFragment = targetFragment
            return this
        }

        /**
         * 设置动画效果
         *
         * @param animStyle
         * @return
         */
        fun setAnimationStyle(@StyleRes animStyle: Int): Builder {
            mAnimStyle = animStyle
            return this
        }

        /**
         * 设置多选模式
         *
         * @param multipleMode
         * @return
         */
        fun setMultipleMode(multipleMode: Boolean): Builder {
            mMultipleMode = multipleMode
            return this
        }

        fun setHotCities(hotCities: HotCities?): Builder {
            mHotCities = hotCities
            return this
        }

        fun setCities(items: List<City>?): Builder {
            mCities = items
            return this
        }

        /**
         * 设置选择结果的监听器
         *
         * @param listener
         * @return
         */
        fun setOnResultListener(listener: OnResultListener<City>?): Builder {
            mOnResultListener = listener
            return this
        }

        /**
         * 设置选择结果的监听器
         *
         * @param listener
         * @return
         */
        fun setOnRequestLocationListener(listener: OnRequestLocationListener?): Builder {
            mOnRequestLocationListener = listener
            return this
        }

        /**
         * 设置选择结果的监听器
         *
         * @param listener
         * @return
         */
        fun setOnPickerReadyListener(listener: OnPickerReadyListener<City>?): Builder {
            mOnPickerReadyListener = listener
            return this
        }

        fun setTitle(title: String): Builder {
            mTitle = title
            return this
        }

        fun setTitle(resId: Int): Builder {
            mTitleResId = resId
            return this
        }

        fun setSearchEnabled(enable: Boolean): Builder {
            mEnableSearch = enable
            return this
        }

        fun show(): CityPickerFragment {
            val cityPickerFragment = CityPickerFragment()
            cityPickerFragment.setUseDefaultCities(mUserDefaultCities)
            cityPickerFragment.enableLocatedCity(mEnableLocatedCity)
            cityPickerFragment.enableDefaultHotCities(mEnableDefaultCities)
            cityPickerFragment.setItem(mLocatedCity, mHotCities, mCities)
            cityPickerFragment.setAnimationStyle(mAnimStyle)
            cityPickerFragment.setMultipleMode(mMultipleMode)
            cityPickerFragment.setSearchEnabled(mEnableSearch)
            cityPickerFragment.setTitle(mTitle)
            cityPickerFragment.setTitle(mTitleResId)
            cityPickerFragment.setOnResultListener(mOnResultListener)
            cityPickerFragment.setOnRequestLocationListener(mOnRequestLocationListener)
            cityPickerFragment.setOnRequestCitiesListener(mOnPickerReadyListener)
            if (mTargetFragment != null) {
                cityPickerFragment.setTargetFragment(mTargetFragment, 0)
            }
            mFragmentManager?.let {
                var ft = it.beginTransaction()
                val prev = it.findFragmentByTag(TAG)
                if (prev != null) {
                    ft.remove(prev).commit()
                    ft = it.beginTransaction()
                }
                ft.addToBackStack(TAG)
                cityPickerFragment.show(ft, TAG)
            }
            return cityPickerFragment
        }
    }

    companion object {
        private const val TAG = "CityPickerFragment"

        /**
         * 获取实例
         *
         * @param enable 是否启用动画效果
         * @return
         */
        fun newInstance(enable: Boolean): CityPickerFragment {
            return CityPickerFragment().apply {
                Bundle().apply {
                    putBoolean("cp_enable_anim", enable)
                }
            }
        }
    }
}