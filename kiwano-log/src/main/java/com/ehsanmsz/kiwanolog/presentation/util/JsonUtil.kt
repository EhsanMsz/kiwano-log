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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import com.ehsanmsz.kiwanolog.presentation.ui.theme.ExtraColors.JsonColors
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by Ehsan Msz on 24 Apr, 2025
 */

internal object JsonUtil {
    private val stringRegex = Regex("\"(.*?)\"")
    private val numberRegex = Regex("\\b-?\\d+(\\.\\d+)?\\b")
    private val booleanRegex = Regex("\\b(true|false)\\b")
    private val nullRegex = Regex("\\bnull\\b")
    private val braceRegex = Regex("[{}\\[\\]]")

    fun formatJson(json: String, colors: JsonColors): AnnotatedString {
        val annotatedJson = runCatching {
            val trimmedJson = json.trim()
            val formattedJson = when {
                trimmedJson.startsWith("{") -> JSONObject(trimmedJson).toString(4)
                trimmedJson.startsWith("[") -> JSONArray(trimmedJson).toString(4)
                else -> return AnnotatedString(json)
            }

            highlightJson(formattedJson, colors)
        }.getOrDefault(AnnotatedString(json))
        return annotatedJson
    }

    private fun highlightJson(formattedJson: String, colors: JsonColors) = buildAnnotatedString {
        append(formattedJson)

        stringRegex.findAll(formattedJson).forEach { match ->
            val end = match.range.last + 1
            val isKey = formattedJson.drop(end).dropWhile { it.isWhitespace() }.firstOrNull() == ':'
            addStyle(
                style = SpanStyle(color = if (isKey) colors.key else colors.string),
                start = match.range.first,
                end = end
            )
        }

        numberRegex.findAll(formattedJson).forEach { match ->
            addStyle(SpanStyle(color = colors.number), match.range.first, match.range.last + 1)
        }

        booleanRegex.findAll(formattedJson).forEach { match ->
            addStyle(SpanStyle(color = colors.boolean), match.range.first, match.range.last + 1)
        }

        nullRegex.findAll(formattedJson).forEach { match ->
            addStyle(SpanStyle(color = colors.nullValue), match.range.first, match.range.last + 1)
        }

        braceRegex.findAll(formattedJson).forEach { match ->
            addStyle(SpanStyle(color = colors.brace), match.range.first, match.range.last + 1)
        }
    }
}
