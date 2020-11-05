package com.liabit.test

import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.hardware.camera2.*
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.Process
import android.util.Log
import android.view.Surface

/**
 * 解决在Android4.4上 java.lang.VerifyError 错误，拆分成3个类 FlashlightApi23，FlashlightApi21，FlashlightDefault
 */
class FlashLightUtil private constructor(context: Context) : Flashlight {

    companion object {
        private var instance: FlashLightUtil? = null

        @JvmStatic
        fun getInstance(context: Context): FlashLightUtil {
            if (instance == null) {
                synchronized(FlashLightUtil::class.java) {
                    if (instance == null) {
                        instance = FlashLightUtil(context.applicationContext)
                    }
                }
            }
            return instance!!
        }
    }

    private var mFlashlight: Flashlight

    init {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> mFlashlight = FlashlightApi23(context)
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> mFlashlight = FlashlightApi21(context)
            else -> mFlashlight = FlashlightDefault()
        }
    }

    override fun isFlashlightOn(): Boolean {
        return mFlashlight.isFlashlightOn()
    }

    override fun toggleFlashlight(callback: ((on: Boolean) -> Unit)?) {
        mFlashlight.toggleFlashlight(callback)
    }
}


@Suppress("CascadeIf", "LiftReturnOrAssignment")
class FlashlightApi23(private val context: Context) : Flashlight {
    private var isTorchOpen = false
    private var mCameraId = ""

    private var mProxy: Flashlight? = null

    override fun isFlashlightOn(): Boolean {
        mProxy?.let {
            return it.isFlashlightOn()
        }
        return isTorchOpen
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun toggleFlashlight(callback: ((on: Boolean) -> Unit)?) {
        try {
            val manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager //获得camera_service服务
            if (mCameraId.isEmpty()) {
                mCameraId = getCameraId(manager)
            }
            if (isTorchOpen) {
                manager.setTorchMode(mCameraId, false)
                isTorchOpen = false
                callback?.invoke(false)
            } else {
                manager.setTorchMode(mCameraId, true)
                isTorchOpen = true
                callback?.invoke(true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            //360手机虽然系统是M及以上，但是CameraManager.setTorchMode 会报 Bad argument passed to camera service 错误
            //但是可以使用 API21 的方式正常打开
            tryApi21(callback)
        }
    }

    private fun tryApi21(callback: ((on: Boolean) -> Unit)?) {
        try {
            if (mProxy == null) {
                mProxy = FlashlightApi21(context)
            }
            mProxy?.toggleFlashlight(callback)
        } catch (e: Exception) {
            e.printStackTrace()
            callback?.invoke(false)
        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    fun getCameraId(manager: CameraManager): String {
        val ids = manager.cameraIdList
        for (id in ids) {
            val characteristics = manager.getCameraCharacteristics(id)
            if (characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true) {
                return id
            }
        }
        return "0"
    }
}

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class FlashlightApi21(private val context: Context) : Flashlight {

    @Volatile
    private var isTorchOpen = false

    private var mCameraDevice: CameraDevice? = null
    private var mCameraCaptureSession: CameraCaptureSession? = null
    private var mSurface: Surface? = null
    private var mSurfaceTexture: SurfaceTexture? = null

    private var mBackHandler: Handler? = null
    private var mBackThread = HandlerThread("background")

    private fun getBackHandler(): Handler {
        if (mBackHandler == null) {
            mBackThread.start()
            mBackHandler = Handler(mBackThread.looper)
        }
        return mBackHandler!!
    }

    override fun isFlashlightOn(): Boolean {
        return isTorchOpen
    }

    private fun release() {
        mCameraCaptureSession?.close()
        mCameraDevice?.close()
        mSurfaceTexture?.release()
        mSurface?.release()
        mCameraDevice = null
        mCameraCaptureSession = null
        mSurfaceTexture = null
        mSurface = null
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun toggleFlashlight(callback: ((on: Boolean) -> Unit)?) {
        try {
            if (isTorchOpen) {
                getBackHandler().post {
                    try {
                        release()
                        isTorchOpen = false
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                callback?.invoke(false)
            } else {
                release()
                val manager =
                        context.applicationContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager //获得camera_service服务
                val ids = manager.cameraIdList
                for (id in ids) {
                    val characteristics = manager.getCameraCharacteristics(id)
                    if (characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true) {
                        val result = context.checkPermission(
                                android.Manifest.permission.CAMERA,
                                Process.myPid(),
                                Process.myUid()
                        )
                        if (result == PackageManager.PERMISSION_GRANTED) {
                            manager.openCamera(id, object : CameraDevice.StateCallback() {
                                override fun onOpened(camera: CameraDevice) {
                                    try {
                                        mCameraDevice = camera
                                        val list = ArrayList<Surface>()
                                        mSurfaceTexture = SurfaceTexture(1)
                                        mSurface = Surface(mSurfaceTexture)
                                        list.add(mSurface!!)

                                        camera.createCaptureSession(
                                                list,
                                                object : CameraCaptureSession.StateCallback() {
                                                    override fun onConfigured(session: CameraCaptureSession) {
                                                        try {
                                                            mCameraCaptureSession = session
                                                            val request =
                                                                    camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                                                            //request.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AF_MODE_AUTO)
                                                            request.set(
                                                                    CaptureRequest.FLASH_MODE,
                                                                    CameraMetadata.FLASH_MODE_TORCH
                                                            )
                                                            mSurface?.let { request.addTarget(it) }
                                                            session.capture(request.build(), null, null)
                                                            isTorchOpen = true
                                                            callback?.invoke(true)
                                                        } catch (e: Exception) {
                                                            e.printStackTrace()
                                                        }
                                                    }

                                                    override fun onConfigureFailed(session: CameraCaptureSession) {
                                                        isTorchOpen = false
                                                    }
                                                },
                                                getBackHandler()
                                        )
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }

                                override fun onDisconnected(camera: CameraDevice) {
                                }

                                override fun onError(camera: CameraDevice, error: Int) {
                                    isTorchOpen = false
                                }
                            }, getBackHandler())
                        }
                        break
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}


class FlashlightDefault : Flashlight {

    private var isTorchOpen = false
    private var mCamera: Camera? = null

    override fun isFlashlightOn(): Boolean {
        return isTorchOpen
    }

    @Suppress("DEPRECATION")
    override fun toggleFlashlight(callback: ((on: Boolean) -> Unit)?) {
        try {
            // 获取手机背面的摄像头
            if (mCamera == null) {
                mCamera = getCamera() ?: return
            }
            val camera = mCamera ?: return
            val parameters = camera.parameters
            val flashModes = parameters.supportedFlashModes
            val flashMode = parameters.flashMode ?: return
            if (Camera.Parameters.FLASH_MODE_OFF != flashMode) {
                if (flashModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
                    parameters.flashMode = Camera.Parameters.FLASH_MODE_OFF
                    camera.parameters = parameters
                    camera.stopPreview()
                    camera.release()
                    mCamera = null
                    isTorchOpen = false
                    callback?.invoke(false)
                }
            } else if (Camera.Parameters.FLASH_MODE_TORCH != flashMode) {
                if (flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                    parameters.flashMode = Camera.Parameters.FLASH_MODE_TORCH
                    camera.startPreview()
                    camera.parameters = parameters
                    isTorchOpen = true
                    callback?.invoke(true)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Suppress("DEPRECATION", "LiftReturnOrAssignment")
    private fun getCamera(): Camera? {
        val numCameras = Camera.getNumberOfCameras()
        if (numCameras == 0) {
            return null
        }

        var index = 0
        while (index < numCameras) {
            val cameraInfo = Camera.CameraInfo()
            Camera.getCameraInfo(index, cameraInfo)
            // CAMERA_FACING_BACK：手机背面的摄像头
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                break
            }
            index++
        }
        var camera: Camera?
        if (index < numCameras) {
            Log.d("ShortcutView", "Opening camera =$index")
            camera = Camera.open(index)
        } else {
            Log.d("ShortcutView", "No camera facing back; returning camera #0")
            camera = Camera.open(0)
        }

        return camera
    }
}

interface Flashlight {
    fun isFlashlightOn(): Boolean

    fun toggleFlashlight(callback: ((on: Boolean) -> Unit)? = null)
}

