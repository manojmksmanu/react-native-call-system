package com.foregrounsdservice.calling.notification

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.foregrounsdservice.calling.ui.CallActivity

object CallNotification {

    const val CORE_ID = 1001
    const val CALL_ID = 2001

    private const val CHANNEL_CORE = "dozo_core"
    private const val CHANNEL_CALL = "dozo_call"

    /* -------------------------------
       CHANNELS
    -------------------------------- */

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_CORE,
                "Dozo Background",
                NotificationManager.IMPORTANCE_LOW
            )
        )

        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_CALL,
                "Dozo Call",
                NotificationManager.IMPORTANCE_HIGH
            )
        )
    }

    /* -------------------------------
       CORE NOTIFICATION
    -------------------------------- */

    fun core(context: Context): Notification =
        NotificationCompat.Builder(context, CHANNEL_CORE)
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setContentTitle("Dozo Live")
            .setContentText("Service running")
            .setOngoing(true)
            .setSilent(true)
            .build()

    /* -------------------------------
       CALL NOTIFICATION (PURE UI)
    -------------------------------- */

    fun call(
        context: Context,
        userName: String,
        elapsedText: String
    ): Notification {

        val intent = Intent(context, CallActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pi = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, CHANNEL_CALL)
            .setSmallIcon(android.R.drawable.sym_call_incoming)
            .setContentTitle(userName)
            .setContentText("Call duration • $elapsedText")
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(pi)
            .build()
    }
}
