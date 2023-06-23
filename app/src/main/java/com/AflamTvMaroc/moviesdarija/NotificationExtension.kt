package com.AflamTvMaroc.moviesdarija

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.AflamTvMaroc.moviesdarija.model.Notification
import com.AflamTvMaroc.moviesdarija.ui.MainActivity
import java.text.SimpleDateFormat
import java.util.*


fun Context.sendNotification(data: Notification?) {
    var contentIntent: PendingIntent? = null
    when (data?.active?.lowercase()) {
        NOTIFICATIONSTATUS.ACTIVE.status -> {
            contentIntent = when (data.url.isNullOrEmpty()) {
                true -> {
                    /**
                     * Here the notification click will open the app
                     */
                    val notificationIntentActivity = Intent(this, MainActivity::class.java)
                    PendingIntent.getActivity(this, 0, notificationIntentActivity, FLAG_IMMUTABLE)
                }
                false -> {
                    /**
                     * Here the notification click will open the webView
                     */
                    val notificationIntent = Intent(Intent.ACTION_VIEW, Uri.parse(data.url))
                    PendingIntent.getActivity(this, 0, notificationIntent, FLAG_IMMUTABLE)
                }
            }
            val builder = NotificationCompat.Builder(this, "1")
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle(data.title)
                    .setContentText(data.body)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
            with(NotificationManagerCompat.from(this)) {
                notify(0, builder.build())
            }
        }
    }




}

fun Activity.createNotificationChannel() {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = this.getString(R.string.channel_name)
        val descriptionText = this.getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("1", name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

fun createIDForEachNotification(): Int {
    val now = Date()
    return SimpleDateFormat("ddHHmmss", Locale.US).format(now).toInt()
}

enum class NOTIFICATIONSTATUS(val status: String) {
    ACTIVE("active")
}