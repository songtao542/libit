package com.liabit.test.nested

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class TestPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val titleArray = arrayOf("推荐", "视频", "直播", "图片", "精华", "热门")

    private var mData = MutableList(5) {
        return@MutableList it
    }

    override fun getCount(): Int {
        return mData.size
    }

    override fun getItem(position: Int): Fragment {
        return TestFragment()
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titleArray[position]
    }

}