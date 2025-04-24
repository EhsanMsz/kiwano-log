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

import kotlin.math.log2
import kotlin.math.pow

/**
 * Created by Ehsan Msz on 17 Apr, 2025
 */
internal object StorageUtil {
    private val units = arrayOf("KB", "MB", "GB")

    /**
     * converts [bytes] to human readable format
     */
    fun formatSize(bytes: Int): String {
        if (bytes < 1024) return "$bytes B"

        val exp = (log2(bytes.toDouble()) / 10).toInt()
        val size = bytes / 1024.0.pow(exp)

        return "%.1f %s".format(size, units[exp - 1])
    }
}