package com.ehsanmsz.kiwanolog.device.notification

import android.content.Context
import com.ehsanmsz.kiwanolog.domain.device.KiwanoNotificationManager

/**
 * Created by Ehsan Msz on 22 Mar, 2025
 */

/**
 * This class provides the [KiwanoNotificationManager] instance
 */
internal object KiwanoNotificationManagerProvider {

    @Volatile
    private var kiwanoNotificationManager: KiwanoNotificationManager? = null

    /**
     * Returns the [KiwanoNotificationManager] instance (singleton)
     */
    fun get(context: Context): KiwanoNotificationManager {
        return synchronized(this) {
            if (kiwanoNotificationManager == null) {
                kiwanoNotificationManager = KiwanoNotificationManagerImpl(context)
            }
            kiwanoNotificationManager!!
        }
    }

}