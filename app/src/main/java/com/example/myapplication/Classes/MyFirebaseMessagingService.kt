package com.example.myapplication.Classes

import android.annotation.SuppressLint

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.test.core.app.ApplicationProvider
import com.example.myapplication.R
import com.example.myapplication.UserSide.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage




class MyFirebaseMessagingService: FirebaseMessagingService(){

    companion object {
        private const val TAG = "MyFirebaseMessaging"
        private val activeNotifications: MutableList<PushNotification> = mutableListOf()
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val notification = remoteMessage.notification
        // First case when notifications are received via
        if (remoteMessage.notification != null) {
            showNotification(remoteMessage.notification!!.title!!,remoteMessage.notification!!.body!!)

            val title = notification!!.title ?: ""
            val body = notification.body ?: ""

            // Store the notification in your app's data model or storage
            val pushNotification = PushNotification(title, body)
            activeNotifications.add(pushNotification)

        }



    }

    override fun onDeletedMessages() {
        // Handle the case when the FCM server deletes pending messages
        activeNotifications.clear()
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        // You can update the user's FCM token in your app's data model or storage if needed
        // ...
    }



    // Method to get the custom Design for the display of
    // notification.
    @SuppressLint("RemoteViewLayout")
    private fun getCustomDesign(title: String, message: String): RemoteViews {
        val remoteViews = RemoteViews("com.example.myapplication", R.layout.notification_item
        )
        remoteViews.setTextViewText(R.id.title, title)
        remoteViews.setTextViewText(R.id.message, message)
        remoteViews.setImageViewResource(
            R.id.ivIcon,
            R.drawable.notificon
        )
        return remoteViews
    }

    // Method to display the notifications
    @SuppressLint("SuspiciousIndentation")
    private fun showNotification(
        title: String,
        message: String
    ) {
        // Pass the intent to switch to the MainActivity
        val intent = Intent(this, MainActivity::class.java)
        // Assign channel ID
        val channelId = "notification_channel"
        // Here FLAG_ACTIVITY_CLEAR_TOP flag is set to clear
        // the activities present in the activity stack,
        // on the top of the Activity that is to be launched
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        // Pass the intent to PendingIntent to start the
        // next Activity
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        // Create a Builder object using NotificationCompat
        // class. This will allow control over all the flags
        var builder: NotificationCompat.Builder = NotificationCompat.Builder(
            ApplicationProvider.getApplicationContext(),
            channelId
        )
            .setSmallIcon(R.drawable.notificon)
            .setAutoCancel(true)
            .setVibrate(
                longArrayOf(
                    1000, 1000, 1000,
                    1000, 1000
                )
            )
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        // A customized design for the notification can be
        // set only for Android versions 4.1 and above. Thus
        // condition for the same is checked here.
        builder = builder.setContent(
            getCustomDesign(title, message)
        )
        // Create an object of NotificationManager class to
        // notify the
        // user of events that happen in the background.
      val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Check if the Android Version is greater than Oreo


        if (Build.VERSION.SDK_INT
            >= Build.VERSION_CODES.O
        ) {
            val notificationChannel = NotificationChannel(channelId, "web_app", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify(0, builder.build())
    }


}

data class PushNotification(
    val title: String,
    val body: String
)




