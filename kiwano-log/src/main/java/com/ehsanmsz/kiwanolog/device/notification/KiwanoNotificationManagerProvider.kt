/*
 * Copyright 2024 Ehsan Msz
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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