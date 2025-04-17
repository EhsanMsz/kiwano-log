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

package com.ehsanmsz.kiwanolog.presentation.util

import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Created by Ehsan Msz on 17 Apr, 2025
 */
internal object TimeUtil {
    private val dateTimeFormat = SimpleDateFormat("yyyy-mm-dd HH:mm:ss", Locale.US)
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.US)

    /**
     * Formats [timeMillis] to [yyyy-mm-dd HH:mm:ss]
     */
    fun getFormattedDateTime(timeMillis: Long): String =
        dateTimeFormat.format(timeMillis)

    /**
     * Formats [timeMillis] to [HH:mm:ss]
     */
    fun getFormattedTime(timeMillis: Long): String =
        timeFormat.format(timeMillis)
}