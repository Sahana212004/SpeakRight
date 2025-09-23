package com.example.speakright.ui.theme

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import com.example.speakright.R
import java.util.*
import java.util.concurrent.TimeUnit

// Notification data model
data class NotificationItem(val title: String, val message: String, val time: String)

class NotificationsActivity : AppCompatActivity() {

    private lateinit var rvNotifications: RecyclerView
    private lateinit var btnBack: ImageButton
    private val notificationsList = mutableListOf<NotificationItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        rvNotifications = findViewById(R.id.rvNotifications)
        btnBack = findViewById(R.id.btnBack)

        // Back button
        btnBack.setOnClickListener { finish() }

        // Sample notifications (can be loaded dynamically from DB or SharedPreferences)
        addSampleNotifications()

        // RecyclerView setup
        val adapter = NotificationsAdapter(notificationsList)
        rvNotifications.layoutManager = LinearLayoutManager(this)
        rvNotifications.adapter = adapter

        // Schedule daily motivational notifications
        scheduleDailyNotifications()
    }

    private fun addSampleNotifications() {
        notificationsList.add(
            NotificationItem(
                "New Message",
                "You have a new message from John.",
                "5 min ago"
            )
        )
        notificationsList.add(
            NotificationItem(
                "Achievement Unlocked",
                "You completed Level 5!",
                "10 min ago"
            )
        )
        notificationsList.add(
            NotificationItem(
                "Reminder",
                "Don't forget to practice today.",
                "30 min ago"
            )
        )
    }

    private fun scheduleDailyNotifications() {
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            24, TimeUnit.HOURS
        )
            .setInitialDelay(calculateInitialDelay(hour = 8, minute = 0), TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "DailyStudyReminder",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

    // Calculate delay until 8 AM for first notification
    private fun calculateInitialDelay(hour: Int, minute: Int): Long {
        val currentTime = Calendar.getInstance()
        val targetTime = Calendar.getInstance()
        targetTime.set(Calendar.HOUR_OF_DAY, hour)
        targetTime.set(Calendar.MINUTE, minute)
        targetTime.set(Calendar.SECOND, 0)
        var delay = targetTime.timeInMillis - currentTime.timeInMillis
        if (delay < 0) delay += 24 * 60 * 60 * 1000 // schedule for next day if time passed
        return delay
    }
}
