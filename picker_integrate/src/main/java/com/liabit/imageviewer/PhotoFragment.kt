package com.liabit.imageviewer

import android.graphics.BitmapFactory
import android.graphics.Color
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
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.liabit.integratepicker.R
import com.liabit.photoview.DraggablePhotoView
import com.liabit.photoview.OnPhotoTapListener
import kotlinx.coroutines.*
import java.io.File


/**
 */
open class PhotoFragment : Fragment() {

    companion object {
        const val URI = "uri"
        const val PHOTO = "photo"

        private const val KEY_DRAGGABLE = "draggable"
        private const val KEY_SENSITIVITY = "sensitivity"

        @JvmStatic
        fun newInstance(photo: Photo, extras: Bundle? = null) = PhotoFragment().apply {
            arguments = Bundle().apply {
                putParcelable(PHOTO, photo)
                extras?.let { putAll(extras) }
            }
        }
    }

    private var mOnPhotoSingleTapListener: OnPhotoSingleTapListener? = null
    private var mDraggable: Boolean = true
    private var mSensitivity: Float = 0.5f
    private var mLoadImageJob: Job? = null
    private var mPhoto: Photo? = null
    private var mInitIndex: Int = 0
    private var mIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mPhoto = it.getParcelable(PHOTO)
            mInitIndex = it.getInt(PhotoViewerFragment.INDEX, 0)
            mDraggable = it.getBoolean(KEY_DRAGGABLE, true)
            mSensitivity = it.getFloat(KEY_SENSITIVITY, 0.5f)
        }
    }

    private lateinit var mPhotoView: DraggablePhotoView
    private lateinit var mRootView: View
    private var isTransformOut: Boolean = false

    private val mCrossFadeFactory by lazy { DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mRootView = inflater.inflate(R.layout.p_fragment_photo_view, container, false)
        mPhotoView = mRootView.findViewById(R.id.photoView)
        return mRootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mPhotoView.setDraggable(mDraggable, mSensitivity)
        mPhotoView.setMinimumScale(1f)
        mPhotoView.setThumbRect(mPhoto?.bounds)
        /*mImageView.setOnViewTapListener(object : OnViewTapListener {
            override fun onViewTap(view: View, x: Float, y: Float) {
                if (mImageView.checkMinScale()) {
                    transformOut()
                }
            }
        })*/
        mPhotoView.setOnPhotoTapListener(object : OnPhotoTapListener {
            override fun onPhotoTap(view: ImageView, x: Float, y: Float) {
                if (mPhotoView.checkMinScale()) {
                    transformOut()
                }
            }
        })
        mPhotoView.setAlphaChangeListener(object : DraggablePhotoView.OnAlphaChangeListener {
            override fun onAlphaChange(alpha: Int) {
                mRootView.alpha = alpha / 255f
            }
        })
        mPhotoView.setOnTransformListener(object : DraggablePhotoView.OnTransformListener {
            override fun onTransformCompleted(status: DraggablePhotoView.Status?) {
                if (status == DraggablePhotoView.Status.STATE_OUT) {
                    mOnPhotoSingleTapListener?.onPhotoSingleTap()
                }
            }
        })
        transformInIfNeeded()
        loadFile(mPhoto?.uri)
    }

    private fun transformOut() {
        if (!isTransformOut && mPhoto?.bounds != null) {
            isTransformOut = true
            mPhotoView.transformOut()
        } else {
            mOnPhotoSingleTapListener?.onPhotoSingleTap()
        }
    }

    private fun transformInIfNeeded() {
        if (mInitIndex == mIndex && mPhoto?.bounds != null) {
            mPhotoView.transformIn()
        } else {
            mRootView.setBackgroundColor(Color.BLACK)
        }
    }

    protected open fun loadFile(uri: Uri? = null) {
        if (uri == null) return
        mLoadImageJob?.cancel()
        mLoadImageJob = GlobalScope.launch(Dispatchers.Main) {
            val file = withContext(Dispatchers.IO) {
                @Suppress("BlockingMethodInNonBlockingContext")
                return@withContext try {
                    Glide.with(this@PhotoFragment)
                            .downloadOnly()
                            .load(uri)
                            .submit()
                            .get()
                } catch (e: Exception) {
                    Log.e("PhotoFragment", "load photo error!", e)
                    null
                }
            }
            if (isActive && file != null && file.exists()) {
                mPhotoView.isZoomable = "image/gif" != getFileMime(file)
                Glide.with(this@PhotoFragment)
                        .load(file)
                        .apply(RequestOptions().priority(Priority.HIGH).fitCenter())
                        .transition(withCrossFade(mCrossFadeFactory))
                        .into(mPhotoView)
            }
        }
    }

    private fun getFileMime(file: File): String {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(file.path, options)
        return options.outMimeType ?: "image/png"
    }

    override fun onDestroyView() {
        mLoadImageJob?.cancel()
        super.onDestroyView()
    }

    fun setOnPhotoSingleTapListener(listener: OnPhotoSingleTapListener?) {
        mOnPhotoSingleTapListener = listener
    }

    fun setIndex(index: Int) {
        mIndex = index
    }

    interface OnPhotoSingleTapListener {
        fun onPhotoSingleTap()
    }

}
