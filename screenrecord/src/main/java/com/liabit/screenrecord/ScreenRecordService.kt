package com.liabit.screenrecord

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class ScreenRecordService : Service() {

    companion object {
        const val CHANNEL_ID = "ScreenRecord"
        const val FOREGROUND_ID = 122
    }

    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("ScreenRecordService", "stop record by click notification")
            ScreenRecordUtil.getInstance(this@ScreenRecordService).stopRecord()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopForeground(STOP_FOREGROUND_REMOVE)
            } else {
                stopSelf()
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(FOREGROUND_ID, getNotification())
        try {
            //LocalBroadcastManager.getInstance(this).
            registerReceiver(mBroadcastReceiver, IntentFilter("com.liabit.screenrecord.STOP_RECORD"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            //LocalBroadcastManager.getInstance(this).
            unregisterReceiver(mBroadcastReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getNotification(): Notification {
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = getString(R.string.notification_channel)
            channel.enableVibration(false)
            channel.setSound(null, null)
            channel.setShowBadge(false)
            manager.createNotificationChannel(channel)
        }
        NotificationCompat.Builder(applicationContext, CHANNEL_ID)

        val builder = NotificationCompat.Builder(this, "ScreenRecord")
        builder.setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.screen_record))
        builder.setContentTitle(getString(R.string.notification_title))
        builder.setContentText(getString(R.string.notification_text))
        builder.setOngoing(true)
        builder.setSmallIcon(R.drawable.screen_record)
        builder.setAutoCancel(false)
        val intent = Intent("com.liabit.screenrecord.STOP_RECORD")
        val pendingIntent = PendingIntent.getBroadcast(this, 12, intent, PendingIntent.FLAG_ONE_SHOT)
        builder.setContentIntent(pendingIntent)
        return builder.build()
    }

}