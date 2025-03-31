package com.ehsanmsz.kiwanolog.device.notification.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ehsanmsz.kiwanolog.device.notification.KiwanoNotificationManagerProvider
import com.ehsanmsz.kiwanolog.domain.device.KiwanoNotificationManager

/**
 * Created by Ehsan Msz on 22 Mar, 2025
 */

/**
 * Broadcast receiver to get notification delete event
 */
internal class KiwanoNotificationDeleteBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == KiwanoNotificationManager.NotificationDeleteAction) {
            context?.let(KiwanoNotificationManagerProvider::get)?.onNotificationDelete()
        }
    }
}