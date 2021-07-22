package com.scaffold.ui.tab.profile

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.scaffold.base.BaseFragment
import com.scaffold.databinding.FragmentProfileBinding
import com.scaffold.widget.EmptyView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment<ProfileViewModel, FragmentProfileBinding>() {


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