package com.ehsanmsz.kiwanolog.domain.device

import com.ehsanmsz.kiwanolog.domain.model.KiwanoHttpModel

/**
 * Created by Ehsan Msz on 22 Mar, 2025
 */

/**
 * Notification manager
 */
internal interface KiwanoNotificationManager {

    @Suppress("ConstPropertyName")
    companion object {
        const val NotificationDeleteAction = "com.ehsanmsz.kiwanolog.notification_delete"
    }

    /**
     * Notifies [httpModel].
     */
    fun notify(httpModel: KiwanoHttpModel)


    /**
     * Invokes when notification deletes.
     *
     * @see [com.ehsanmsz.kiwanolog.device.notification.receiver.KiwanoNotificationDeleteBroadcastReceiver]
     */
    fun onNotificationDelete()
}