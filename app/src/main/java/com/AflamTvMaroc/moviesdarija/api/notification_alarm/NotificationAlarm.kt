package com.AflamTvMaroc.moviesdarija.api.notification_alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.ihsanbal.logging.Level
import com.ihsanbal.logging.LoggingInterceptor
import com.AflamTvMaroc.moviesdarija.api.ApiInterface
import com.AflamTvMaroc.moviesdarija.model.Notification
import com.AflamTvMaroc.moviesdarija.sendNotification
import com.AflamTvMaroc.moviesdarija.util.constants.AppConstants
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

/**
 * This class is now used of AlarmManager to handle sending notification in specific time
 */

class NotificationAlarm : BroadcastReceiver() {
    private lateinit var retrofit: Retrofit

    fun setAlarm(context: Context) {
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationAlarm::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_MUTABLE
        )
        val alarmStartTime = Calendar.getInstance()
        val now = Calendar.getInstance()
        alarmStartTime[Calendar.HOUR_OF_DAY] = AppConstants.At8MIDAY
        alarmStartTime[Calendar.MINUTE] = 0
        alarmStartTime[Calendar.SECOND] = 0
        if (now.after(alarmStartTime)) {
            alarmStartTime.add(Calendar.DATE, 1)
        }
        alarmMgr.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarmStartTime.timeInMillis,
            pendingIntent
        )
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val client = OkHttpClient().newBuilder()
        client.addInterceptor(
            LoggingInterceptor.Builder()
                .setLevel(Level.BASIC)
                .log(Log.VERBOSE)
                .build()
        )
        retrofit = Retrofit.Builder()
            .baseUrl(ApiInterface.BASE_URL)
            .client(client.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        context?.let { getNotification(it) }

    }

    private fun getNotification(context: Context) {
        val service: ApiInterface = retrofit.create(ApiInterface::class.java)
        val notificationResponse = service.getNotification(ApiInterface.API_NOTIFICATION)
        notificationResponse.enqueue(object : Callback<List<Notification?>?> {
            override fun onResponse(
                call: Call<List<Notification?>?>,
                response: Response<List<Notification?>?>

            ) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        val data = response.body()?.get(0)
                        (context).sendNotification(data)
                        setAlarm(context)
                    }
                }
            }

            override fun onFailure(call: Call<List<Notification?>?>, t: Throwable) {
                Toast.makeText(context, t.message, Toast.LENGTH_SHORT).show()
                val notification = Notification()
                notification.active = "active"
                notification.title = "No Internet"
                notification.body= t.message
                notification.url = null
                (context).sendNotification(notification)
                setAlarm(context)
            }
        })

    }

}