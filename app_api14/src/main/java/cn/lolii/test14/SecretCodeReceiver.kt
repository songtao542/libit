package cn.lolii.test14

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent

class SecretCodeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            val intent = Intent()
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val componentName = ComponentName(context.packageName, MainActivity.ALIAS)
            intent.component = componentName
            it.startActivity(intent)
        }
    }

}