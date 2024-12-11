package com.example.assignease.apiInterface

import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import com.example.assignease.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class NotificationService : FirebaseMessagingService(){

    val channelId = "Assign Ease"

    override fun onMessageReceived(message: RemoteMessage) {

        val manager = getSystemService(NOTIFICATION_SERVICE)
        createNotificationChannel(manager as NotificationManager)

        val notification = NotificationCompat.Builder(this,channelId)
            .setContentTitle(message.notification?.title)
            .setContentText(message.notification?.body)
            .setSmallIcon(R.drawable.assign)
            .setAutoCancel(false)
            .setContentIntent(null)
            .build()

        manager.notify(Random.nextInt() , notification)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(channelId , "assignease" , NotificationManager.IMPORTANCE_HIGH)
            .apply {
                description = "New Work"
                enableLights(true)
            }
        notificationManager.createNotificationChannel(channel)
    }
}