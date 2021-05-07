package com.liabit.test.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * 打开一个新的Activity
 */
fun <T : Fragment> Activity.startActivity(clazz: Class<T>, arguments: Bundle? = null) {
    StubActivity.start(this, clazz, arguments)
}

/**
 * 打开一个新的Activity
 */
fun <T : Fragment> Context.startActivity(clazz: Class<T>, arguments: Bundle? = null) {
    StubActivity.start(this, clazz, arguments)
}

/**
 * 打开一个新的Activity
 */
fun <T : Fragment> Fragment.startActivity(clazz: Class<T>, arguments: Bundle? = null) {
    val context = activity ?: context ?: return
    StubActivity.start(context, clazz, arguments)
}

@AndroidEntryPoint
open class StubActivity : BaseCompatActivity() {

    companion object {
        internal fun <T : Fragment> start(context: Context, clazz: Class<T>, arguments: Bundle? = null) {
            val intent = Intent(context, StubActivity::class.java)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            intent.putExtra("fragment", clazz.name)
            arguments?.let { intent.putExtras(it) }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            val fragmentClass = intent.getStringExtra("fragment") ?: return
            val clazz = Class.forName(fragmentClass)
            val fragment = (clazz.newInstance() as? Fragment) ?: return
            fragment.arguments = intent.extras
            supportFragmentManager
                .beginTransaction()
                .add(android.R.id.content, fragment, clazz.simpleName)
                .commitAllowingStateLoss()
        }
    }
}