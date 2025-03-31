package com.ehsanmsz.kiwanolog.device.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.text.SpannedString
import androidx.core.app.NotificationCompat
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import com.ehsanmsz.kiwanolog.device.notification.receiver.KiwanoNotificationDeleteBroadcastReceiver
import com.ehsanmsz.kiwanolog.domain.device.KiwanoNotificationManager
import com.ehsanmsz.kiwanolog.domain.device.KiwanoNotificationManager.Companion.NotificationDeleteAction
import com.ehsanmsz.kiwanolog.domain.model.KiwanoHttpModel
import com.ehsanmsz.kiwanolog.presentation.ui.KiwanoActivity

/**
 * Created by Ehsan Msz on 22 Mar, 2025
 */

/**
 * KiwanoNotificationManagerImpl
 */
internal class KiwanoNotificationManagerImpl(
    private val context: Context
) : KiwanoNotificationManager {

    private val notificationManager by lazy {
        context.getSystemService(NotificationManager::class.java)
    }

    private val contentIntent = PendingIntent.getActivity(
        context,
        48,
        Intent(context, KiwanoActivity::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    private val deleteIntent = PendingIntent.getBroadcast(
        context,
        47,
        Intent(context, KiwanoNotificationDeleteBroadcastReceiver::class.java)
            .apply { action = NotificationDeleteAction },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    private val httpModels = HashMap<Long, KiwanoHttpModel>()

    @Suppress("ConstPropertyName")
    companion object {
        private const val NotificationId = 474747
        private const val NotificationChannelId = "kiwano_log_channel"
        private const val NotificationChannelName = "KiwanoLog"
        private const val MaxQueueSize = 5
    }

    init {
        createNotificationChannel()
    }

    override fun onNotificationDelete() {
        httpModels.clear()
    }

    private fun addHttpModel(httpModel: KiwanoHttpModel) {
        if (httpModels.contains(httpModel.id)) {
            httpModels[httpModel.id] = httpModel
            return
        }

        if (httpModels.size >= MaxQueueSize + 1) {
            httpModels.remove(httpModels.minOf { it.key })
        }

        httpModels[httpModel.id] = httpModel
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NotificationChannelId,
                NotificationChannelName,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun notify(httpModel: KiwanoHttpModel) {
        addHttpModel(httpModel)
        val notification = NotificationCompat.Builder(context, NotificationChannelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setDeleteIntent(deleteIntent)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .setStyle(getNotificationStyle())
            .build()

        notificationManager.notify(NotificationId, notification)
    }

    private fun getNotificationStyle(): NotificationCompat.Style {
        val lines = mutableListOf<SpannedString>().apply {
            httpModels.values.sortedByDescending { it.id }.forEachIndexed { index, httpModel ->
                val spannedLine = buildSpannedString {
                    bold { append(httpModel.info.method) }
                    append("  ")
                    append(httpModel.info.path)
                }
                add(spannedLine)
            }
        }

        val notificationStyle = NotificationCompat.InboxStyle()
        lines.take(MaxQueueSize).forEach {
            notificationStyle.addLine(it)
        }

        if (lines.size > MaxQueueSize)
            notificationStyle.addLine("...")

        notificationStyle.setSummaryText("KiwanoLog")

        return notificationStyle
    }

}