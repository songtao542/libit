package com.liabit.test.loadmore.train

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.liabit.autoclear.autoClear
import com.liabit.recyclerview.loadmore.LoadMoreAdapter
import com.liabit.test.R
import com.liabit.test.databinding.FragmentTrainSubBinding
import com.liabit.viewbinding.bind
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrainSubFragment : Fragment() {
    private var mVideoAdapter by autoClear { VideoListAdapter() }

    /**
     * 1：初级 2：中级 3：高级
     */
    var level = 0

    val viewModel by viewModels<TrainViewModel>()

    val binding by bind<FragmentTrainSubBinding> { requireView() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_train_sub, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.trainVideos.layoutManager = GridLayoutManager(context, 2)

       val loadMoreAdapter  = LoadMoreAdapter.wrap(mVideoAdapter)

        binding.trainVideos.adapter = loadMoreAdapter
        loadMoreAdapter.setLoadMoreListener {
            viewModel.listVideo(false, level)
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.listVideo(true, level)
        }
        viewModel.livePageEnd.observe(viewLifecycleOwner) {
            //mVideoAdapter.isEnabled = !it
            loadMoreAdapter.isLoadMoreEnabled = !it
        }

        viewModel.liveVideoList.observe(viewLifecycleOwner) {
            binding.swipeRefresh.isRefreshing = false
            if (!it.isNullOrEmpty()) {
                binding.emptyView.visibility = View.GONE
                binding.trainVideos.visibility = View.VISIBLE
                mVideoAdapter.setData(it)
            } else {
                mVideoAdapter.clear()
                binding.emptyView.visibility = View.VISIBLE
                binding.trainVideos.visibility = View.GONE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.listVideo(true, level)
    }

}