package com.scaffold.ui.tab.discover

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.scaffold.base.BaseFragment
import com.scaffold.databinding.FragmentDiscoverBinding
import com.scaffold.util.Log
import com.scaffold.widget.EmptyView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiscoverFragment : BaseFragment<DiscoverViewModel, FragmentDiscoverBinding>() {

    companion object {
        private const val TAG = "DiscoverFragment"
    }

    override fun onInitialize(savedInstanceState: Bundle?) {

    }

    override fun onViewCreated(activity: FragmentActivity, savedInstanceState: Bundle?) {
        binding.emptyView.setOnClickListener {
            if (binding.emptyView.isEmpty()) {

            }
        }

        binding.emptyView.addState(EmptyView.LOADING)
        viewModel.liveEmpty.observe(viewLifecycleOwner) {
            binding.emptyView.beginTransaction()
                .clearState(EmptyView.LOADING)
                .addStateIf(!isNetworkAvailable, EmptyView.NETWORK)
                .addStateIf(it, EmptyView.EMPTY)
                .commit()
        }
        viewModel.liveDataList.observe(viewLifecycleOwner) {

        }

        binding.emptyView.beginTransaction().addStateIf(!isNetworkAvailable, EmptyView.NETWORK).commit()
    }

    override fun onNetworkStateChanged(isNetworkAvailable: Boolean) {
        Log.d(TAG, "onNetworkStateChanged, isNetworkAvailable: $isNetworkAvailable")
    }

}