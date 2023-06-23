package com.AflamTvMaroc.moviesdarija.api.notification_worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.AflamTvMaroc.moviesdarija.api.ApiInterface
import com.AflamTvMaroc.moviesdarija.model.Notification
import com.AflamTvMaroc.moviesdarija.sendNotification
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * This class is now unused of workmanager
 */
class NotificationWorker(var ctx: Context, parameters: WorkerParameters) : Worker(ctx, parameters) {
    private lateinit var retrofit: Retrofit

    override fun doWork(): Result {
        retrofit = Retrofit.Builder()
            .baseUrl(ApiInterface.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return try {
            getNotification(context = ctx)
            Result.success()
        } catch (e: Throwable) {
            Result.failure()
        }
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
                    }
                }
            }

            override fun onFailure(call: Call<List<Notification?>?>, t: Throwable) {}
        })

    }

}