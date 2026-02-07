package com.foregrounsdservice.calling.notification

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.foregrounsdservice.calling.ui.CallActivity
import com.foregrounsdservice.R

object CallNotification {

    const val CORE_ID = 1001
    const val CALL_ID = 2001

    private const val CHANNEL_CORE = "dozo_core"
    private const val CHANNEL_CALL = "dozo_call"

    /* ------------------------------------------------
       CREATE CHANNELS (MANDATORY for Android O+)
    ------------------------------------------------ */

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 🔹 CORE (Foreground background service)
        val coreChannel = NotificationChannel(
            CHANNEL_CORE,
            "Dozo Background Service",
            NotificationManager.IMPORTANCE_LOW // ❗ NOT MIN
        ).apply {
            description = "Keeps Dozo Live running"
            setShowBadge(false)
            lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        }

        // 🔹 CALL (Active call notification)
        val callChannel = NotificationChannel(
            CHANNEL_CALL,
            "Dozo Call",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Ongoing call"
            setShowBadge(true)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }

        manager.createNotificationChannel(coreChannel)
        manager.createNotificationChannel(callChannel)
    }

    /* ------------------------------------------------
       CORE FOREGROUND NOTIFICATION
    ------------------------------------------------ */

    fun core(context: Context): Notification {
        return NotificationCompat.Builder(context, CHANNEL_CORE)
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setContentTitle("Dozo Live")
            .setContentText("Dozo Live is running")
            .setOngoing(true)
            .setSilent(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setForegroundServiceBehavior(
                NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE
            )
            .build()
    }

    /* ------------------------------------------------
       CALL NOTIFICATION (Tap → CallActivity)
    ------------------------------------------------ */

    fun call(context: Context, userName: String): Notification {

        val intent = Intent(context, CallActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, CHANNEL_CALL)
            .setSmallIcon(android.R.drawable.sym_call_incoming)
            .setContentTitle("Call in progress")
            .setContentText(userName)
            .setOngoing(true)
            .setAutoCancel(false)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }
}
