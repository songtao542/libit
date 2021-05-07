package com.liabit.test.loadmore.train

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.liabit.recyclerview.loadmore.AbstractLoadMoreAdapter
import com.liabit.test.R

//class VideoListAdapter : AbstractLoadMoreAdapter<VideoListAdapter.VideoHolder>() {
class VideoListAdapter : RecyclerView.Adapter<VideoListAdapter.VideoHolder>() {
    companion object {
        private const val TAG = "HomeVideoAdapter"
    }

    private var mData = emptyList<Video>()

    fun clear() {
        mData = emptyList()
        notifyDataSetChanged()
    }

    fun setData(videoList: ArrayList<Video>) {
        mData = videoList
        notifyDataSetChanged()
    }

    fun getItem(position: Int): Video {
        return mData[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoHolder {
        return VideoHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_home_video_list, parent, false))
    }

    override fun onBindViewHolder(holder: VideoHolder, position: Int) {
        if (position == 3) {
            Log.d("TTTT", "vvvv")
        }
        holder.setData(position, mData[position])
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    inner class VideoHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val titleView: TextView = itemView.findViewById(R.id.item_title)
        private val summaryView: TextView = itemView.findViewById(R.id.item_summary)
        private val coverView: ImageView = itemView.findViewById(R.id.item_video_coverH)
        private val durationView: TextView = itemView.findViewById(R.id.video_duration)

        var mPosition = 0

        override fun onClick(v: View) {
        }

        init {
            titleView.setOnClickListener(this)
            itemView.setOnClickListener(this)
        }

        fun setData(position: Int, video: Video) {
            mPosition = position
            coverView.load(video.coverUrl, R.drawable.ic_place_holder)
            titleView.text = video.title
            val levelName = video.getLevelTitle(itemView.context)
            val level = video.level
            if (!levelName.isNullOrEmpty() && level != null) {
                summaryView.visibility = View.VISIBLE
                summaryView.text = itemView.context.getString(R.string.level_with_watch_num, levelName, level)
            } else {
                summaryView.visibility = View.INVISIBLE
            }
            video.minute?.let {
                durationView.visibility = View.VISIBLE
                durationView.text = itemView.context.getString(R.string.some_minute, it)
            } ?: kotlin.run {
                durationView.visibility = View.INVISIBLE
            }
        }
    }
}