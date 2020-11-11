package com.liabit.test

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import kotlinx.android.synthetic.main.activity_test_fragment_visible.*

class TestFragmentVisibleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_fragment_visible)
        viewPager.adapter = FAdapter(supportFragmentManager)
        tabLayout.setupWithViewPager(viewPager)
    }

    class FAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getPageTitle(position: Int): CharSequence? {
            return "$position"
        }

        override fun getCount(): Int {
            return 10
        }

        override fun getItem(position: Int): Fragment {
            return TestFragment(position)
        }

    }


    class TestFragment(val position: Int) : Fragment(), View.OnAttachStateChangeListener {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val view = inflater.inflate(R.layout.activity_test_fragment_visible, container, false)
            view.addOnAttachStateChangeListener(this)
            return view
        }

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            Log.d("TTTT", "$position onActivityCreated isVisible: $isVisible")
        }

        override fun onAttach(context: Context) {
            super.onAttach(context)
            Log.d("TTTT", "$position onAttach isVisible: $isVisible")
        }

        override fun onDetach() {
            super.onDetach()
            Log.d("TTTT", "$position onDetach isVisible: $isVisible")
        }

        override fun onViewAttachedToWindow(v: View?) {
            Log.d("TTTT", "$position attached to window")
        }

        override fun onViewDetachedFromWindow(v: View?) {
            Log.d("TTTT", "$position detached to window")
        }
    }
}