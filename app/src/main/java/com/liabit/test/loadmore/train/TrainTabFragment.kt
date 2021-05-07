package com.liabit.test.loadmore.train

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.liabit.test.R
import com.liabit.test.databinding.FragmentTrainBinding
import com.liabit.viewbinding.bind
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrainTabFragment : Fragment() {

    val binding by bind<FragmentTrainBinding> { requireView() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_train, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.trainViewPager.adapter = object : FragmentStatePagerAdapter(childFragmentManager) {
            val tabTitles = arrayOf(
                getString(R.string.primary),
                getString(R.string.intermediate),
                getString(R.string.advanced)
            )

            override fun getItem(position: Int): Fragment {
                val fragment = TrainSubFragment()
                fragment.level = position + 1
                return fragment
            }

            override fun getCount(): Int {
                return tabTitles.size
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return tabTitles[position]
            }
        }
        binding.trainTabs.setupWithViewPager(binding.trainViewPager)
    }


}