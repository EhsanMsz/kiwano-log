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

import androidx.compose.ui.text.AnnotatedString
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by Ehsan Msz on 24 Apr, 2025
 */

internal object JsonUtil {

    fun formatJson(json: String): AnnotatedString = runCatching {
        val trimmedJson = json.trim()
        val formattedJson = when {
            trimmedJson.startsWith("{") -> JSONObject(trimmedJson).toString(4)
            trimmedJson.startsWith("[") -> JSONArray(trimmedJson).toString(4)
            else -> json
        }
        AnnotatedString(formattedJson)

        //TODO: highlight json

    }.getOrDefault(AnnotatedString(json))

}