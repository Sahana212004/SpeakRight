package com.example.speakright.ui.theme

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.speakright.R
import java.util.*

class NotificationWorker(context: Context, params: WorkerParameters) :
    Worker(context, params) {

    override fun doWork(): Result {
        sendDailyNotification()
        return Result.success()
    }

    private fun sendDailyNotification() {
        val prefs = applicationContext.getSharedPreferences("streakPrefs", Context.MODE_PRIVATE)
        val lastOpen = prefs.getLong("lastOpenTime", 0L)
        var streak = prefs.getInt("streakCount", 0)

        // Update streak based on last open
        val now = System.currentTimeMillis()
        val oneDay = 24 * 60 * 60 * 1000L
        streak = when {
            lastOpen == 0L -> 1
            now - lastOpen in oneDay..(2 * oneDay - 1) -> streak + 1
            now - lastOpen >= 2 * oneDay -> 1
            else -> streak
        }
        prefs.edit().putInt("streakCount", streak).putLong("lastOpenTime", now).apply()

        // Notification channel
        val channelId = "study_reminders"
        val channelName = "Study Reminders"
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            channel.enableLights(true)
            channel.enableVibration(true)
            manager.createNotificationChannel(channel)
        }

        // Motivational messages
        val messages = listOf(
            "Keep your streak alive! You're on day $streak!",
            "Time to practice and level up!",
            "Consistency is the key! Start studying now.",
            "Don't miss your daily practice!"
        )
        val randomMessage = messages.random()

        // Intent to open Dashboard / start practice
        val intent = Intent(applicationContext, DashboardActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build notification
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle("SpeakRight Reminder")
            .setContentText(randomMessage)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(R.drawable.ic_play, "Start Practice", pendingIntent)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
