package com.liabit.autoclear

import android.app.Activity
import android.content.Intent
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.abs
import kotlin.random.Random

typealias ResultParser<O> = (requestCode: Int, resultCode: Int, data: Intent?) -> O
typealias ResultCallback<O> = (requestCode: Int, resultCode: Int, O) -> Unit

/**
 * Note: 请不要忘记在 [activity] 的 [Activity.onActivityResult] 方法中调用 [onActivityResult] 此方法
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class ActivityResultLauncherCompat(private var activity: Activity? = null) {

    private val mNextLocalRequestCode = AtomicInteger(abs(Random.nextInt(100, 500)))

    private val mIntentParser: ResultParser<Intent?> = { _, _, intent ->
        intent
    }

    data class ParserAndCallback<O>(
        var parser: ResultParser<O>,
        var callback: ResultCallback<O>
    )

    private val mCallbackMap = HashMap<Int, ParserAndCallback<*>>()

    fun startActivityForResult(intent: Intent, callback: ResultCallback<Intent?>) {
        startActivityForResult(intent, mIntentParser, callback)
    }

    fun <O> startActivityForResult(intent: Intent, parser: ResultParser<O>, callback: ResultCallback<O>) {
        val activity = activity ?: return
        startActivityForResult(activity, intent, parser, callback)
    }

    fun startActivityForResult(activity: Activity, intent: Intent, callback: ResultCallback<Intent?>) {
        startActivityForResult(activity, intent, mIntentParser, callback)
    }

    fun <O> startActivityForResult(activity: Activity, intent: Intent, parser: ResultParser<O>, callback: ResultCallback<O>) {
        val requestCode = mNextLocalRequestCode.getAndIncrement()
        mCallbackMap[requestCode] = ParserAndCallback(parser, callback)
        activity.startActivityForResult(intent, requestCode)
    }

    /**
     * Note: 请不要忘记在 [activity] 的 [Activity.onActivityResult] 方法中调用此方法
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (mCallbackMap.containsKey(requestCode)) {
            dispatchResult(requestCode, resultCode, data, mCallbackMap[requestCode] as ParserAndCallback<*>)
            mCallbackMap.remove(requestCode)
        }
    }

    private fun <O> dispatchResult(requestCode: Int, resultCode: Int, data: Intent?, parserAndCallback: ParserAndCallback<O>) {
        val result = parserAndCallback.parser.invoke(requestCode, resultCode, data)
        parserAndCallback.callback.invoke(requestCode, resultCode, result)
    }

}