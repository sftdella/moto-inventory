package com.example.motoinventory.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.motoinventory.MainActivity
import com.example.motoinventory.R

object NotificationHelper {

    private const val CHANNEL_ID_GENERAL = "pcm_general"
    private const val CHANNEL_ID_APPROVAL = "pcm_approval"
    private const val CHANNEL_NAME_GENERAL = "PCM Inventory"
    private const val CHANNEL_NAME_APPROVAL = "Approval Penjualan"

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NotificationManager::class.java)

            val generalChannel = NotificationChannel(
                CHANNEL_ID_GENERAL,
                CHANNEL_NAME_GENERAL,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Notifikasi umum PCM Inventory" }

            val approvalChannel = NotificationChannel(
                CHANNEL_ID_APPROVAL,
                CHANNEL_NAME_APPROVAL,
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Notifikasi persetujuan penjualan motor" }

            manager.createNotificationChannel(generalChannel)
            manager.createNotificationChannel(approvalChannel)
        }
    }

    fun showApprovalNotification(context: Context, count: Int) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_APPROVAL)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Approval Menunggu")
            .setContentText("Ada $count motor menunggu persetujuan penjualan")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(1001, notification)
        } catch (_: SecurityException) { }
    }

    fun showMotorLamaNotification(context: Context, count: Int) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_GENERAL)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Motor Lama Belum Terjual")
            .setContentText("$count motor sudah lebih dari 30 hari belum terjual")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(1002, notification)
        } catch (_: SecurityException) { }
    }
}
