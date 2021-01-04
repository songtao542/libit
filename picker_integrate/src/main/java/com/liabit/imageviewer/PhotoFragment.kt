package com.liabit.imageviewer

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.liabit.integratepicker.R
import kotlinx.coroutines.*
import java.io.File

/**
 */
open class PhotoFragment : Fragment() {

    companion object {
        const val URI = "uri"

        @JvmStatic
        fun newInstance(uri: Uri, extras: Bundle? = null) = PhotoFragment().apply {
            arguments = Bundle().apply {
                putParcelable(URI, uri)
                extras?.let { putAll(extras) }
            }
        }
    }

    private var onPhotoSingleTapListener: OnPhotoSingleTapListener? = null

    private var loadImageJob: Job? = null

    private var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            uri = it.getParcelable(URI)
        }
    }

    private lateinit var imageView: ImageView
    private lateinit var scaleImageView: SubsamplingScaleImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.p_fragment_photo_view, container, false)
        imageView = view.findViewById(R.id.imageView)
        scaleImageView = view.findViewById(R.id.scaleImageView)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //imageView.displayType = ImageViewTouchBase.DisplayType.FIT_TO_SCREEN
        imageView.setOnClickListener {
            onPhotoSingleTapListener?.onPhotoSingleTap()
        }

        scaleImageView.setOnClickListener {
            onPhotoSingleTapListener?.onPhotoSingleTap()
        }

        loadFile(uri)
    }

    protected open fun loadFile(uri: Uri? = null) {
        loadImageJob?.cancel()
        loadImageJob = GlobalScope.launch(Dispatchers.Main) {
            val file = withContext(Dispatchers.IO) {
                @Suppress("BlockingMethodInNonBlockingContext")
                return@withContext try {
                    Glide.with(this@PhotoFragment)
                            .asFile()
                            .load(uri)
                            .submit()
                            .get()
                } catch (e: Exception) {
                    Log.e("PhotoFragment", "load photo error!", e)
                    null
                }
            }
            if (isActive && file != null && file.exists()) {
                if ("image/gif" == getFileMime(file)) {
                    scaleImageView.visibility = View.GONE
                    imageView.visibility = View.VISIBLE
                    Glide.with(this@PhotoFragment)
                            .load(file)
                            .apply(RequestOptions().priority(Priority.HIGH).fitCenter())
                            .into(imageView)
                } else {
                    imageView.visibility = View.GONE
                    scaleImageView.visibility = View.VISIBLE
                    scaleImageView.setImage(ImageSource.uri(Uri.fromFile(file)))
                }
            }
        }
    }

    private fun getFileMime(file: File): String {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(file.path, options)
        return options.outMimeType
    }

    override fun onDestroyView() {
        loadImageJob?.cancel()
        super.onDestroyView()
    }

    fun setOnPhotoSingleTapListener(listener: OnPhotoSingleTapListener?) {
        onPhotoSingleTapListener = listener
    }

    interface OnPhotoSingleTapListener {
        fun onPhotoSingleTap()
    }

}
