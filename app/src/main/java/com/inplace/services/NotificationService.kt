package com.inplace.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.inplace.MainActivity
import com.inplace.R
import com.inplace.api.vk.ApiVk

class NotificationService : Service() {

    companion object {
        fun startService(context: Context) {
            val startIntent = Intent(context, NotificationService::class.java)
            ContextCompat.startForegroundService(context, startIntent)
        }
        fun stopService(context: Context) {
            val stopIntent = Intent(context, NotificationService::class.java)
            context.stopService(stopIntent)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private val NOTIFICATION_ID_MESSAGE = 1
    private val LIGHT_COLOR_ARGB = R.color.purple_500
    private val CHANNEL_MESSAGES = "messages"
    private val CHANNEL_NAME = "Новое сообщение"
    private var mMessageCount = 0
    lateinit var mManager : NotificationManager
    lateinit var notificationChannel: NotificationChannel
    private val intentToActivity = Intent(this, MainActivity::class.java)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //Уведомления
        // Подписка на новые сообщения
        ExecutorServices.getInstanceAPI().execute()
        {
            while (true) {

                val newMesgsResult = ApiVk.getNewMessages()
                Log.d("ApiVK", "end of get new message request")

                val newMessagesArray = newMesgsResult.result

                for (el in newMessagesArray) {
                    showMessageNotification(el.text)
                }
                Thread.sleep(3000)
            }
        }
        return START_NOT_STICKY
            //   return super.onStartCommand(intent, flags, startId)
    }

    private fun showMessageNotification(messageToShow: String) {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intentToActivity,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        mMessageCount++
        createNotificationChannel()
        val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.foto)
        val builder = NotificationCompat.Builder(this, CHANNEL_MESSAGES)
            .setLargeIcon(largeIcon)
            .setSmallIcon(R.drawable.ic_send)
            .setContentTitle(getString(R.string.message_name))
            .setContentText(messageToShow)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setLights(resources.getColor(LIGHT_COLOR_ARGB), 1000, 1000)
            .setColor(resources.getColor(R.color.purple_500))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
        val style = NotificationCompat.BigTextStyle()
        style.bigText(messageToShow)
        builder.setStyle(style)
        mManager.notify(NOTIFICATION_ID_MESSAGE, builder.build())
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(CHANNEL_MESSAGES, CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH)
            mManager = getSystemService(NotificationManager::class.java)
            mManager!!.createNotificationChannel(serviceChannel)
        }
    }
}