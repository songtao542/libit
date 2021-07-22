package com.domain.scaffold.ui.tab.store

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.domain.scaffold.base.BaseFragment
import com.domain.scaffold.databinding.FragmentStoreBinding
import com.domain.scaffold.ui.tab.home.HomeViewModel
import com.domain.scaffold.widget.EmptyView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StoreFragment : BaseFragment<StoreViewModel, FragmentStoreBinding>() {


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

        viewModel.liveTimeError.observe(viewLifecycleOwner) {
            binding.emptyView.beginTransaction().addStateIf(it, EmptyView.TIME).commit()
        }
        binding.emptyView.beginTransaction().addStateIf(!isNetworkAvailable, EmptyView.NETWORK).commit()
    }

}