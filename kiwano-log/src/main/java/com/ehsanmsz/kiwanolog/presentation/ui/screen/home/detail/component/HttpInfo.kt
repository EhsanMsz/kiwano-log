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

package com.ehsanmsz.kiwanolog.presentation.ui.screen.home.detail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ehsanmsz.kiwanolog.R
import com.ehsanmsz.kiwanolog.domain.model.KiwanoHttpModel
import com.ehsanmsz.kiwanolog.presentation.ui.theme.AppTheme
import com.ehsanmsz.kiwanolog.presentation.util.StorageUtil
import com.ehsanmsz.kiwanolog.presentation.util.TimeUtil
import com.ehsanmsz.kiwanolog.presentation.util.bold
import com.ehsanmsz.kiwanolog.presentation.util.color

/**
 * Created by Ehsan Msz on 24 Apr, 2025
 */

@Composable
internal fun HttpInfo(model: KiwanoHttpModel?) {
    val context = LocalContext.current
    if (model != null) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(12.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = buildAnnotatedString {
                    append(
                        key = context.getString(R.string.kiwano_method),
                        value = model.info.method,
                        newLine = false
                    )
                    append(
                        key = context.getString(R.string.kiwano_url),
                        value = model.info.url
                    )
                    append(
                        key = context.getString(R.string.kiwano_status),
                        value = model.info.statusCode?.toString() ?: ""
                    )
                    append(
                        key = context.getString(R.string.kiwano_protocol),
                        value = "${model.info.protocol} ${model.info.protocolVersion ?: ""}"
                    )
                    append(
                        key = context.getString(R.string.kiwano_request_size),
                        value = model.request.size?.let { StorageUtil.formatSize(it) } ?: "-"
                    )
                    append(
                        key = context.getString(R.string.kiwano_request_date_time),
                        value = TimeUtil.getFormattedDateTime(model.request.timeMillis)
                    )
                    append(
                        key = context.getString(R.string.kiwano_response_size),
                        value = model.response?.size?.let { StorageUtil.formatSize(it) } ?: "-"
                    )
                    append(
                        key = context.getString(R.string.kiwano_response_date_time),
                        value = model.response?.timeMillis?.let { TimeUtil.getFormattedDateTime(it) }
                            ?: "-"
                    )

                    append(
                        key = context.getString(R.string.kiwano_response_duration_millis),
                        value = model.info.duration?.toString() ?: "-"
                    )


                    model.info.exception?.let { exception ->
                        color(MaterialTheme.colorScheme.error) {
                            append(
                                key = context.getString(R.string.kiwano_exception),
                                value = exception
                            )
                        }
                    }
                },
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 28.sp
            )
        }
    }
}

private fun AnnotatedString.Builder.append(key: String, value: String, newLine: Boolean = true) {
    if (newLine) append("\n")
    bold { append(key) }
    append(" : ")
    append(value)
}


@Composable
@PreviewLightDark
private fun Preview() {
    AppTheme {
//        HttpInfo(
//            info = KiwanoHttpModel.Info(
//                method = "GET",
//                url = "http://example.com:8080/get",
//                host = "example.com",
//                port = 8080,
//                path = "/get",
//                protocol = "Http",
//                state = HttpRequestState.Completed,
//                statusCode = 200,
//                protocolVersion = "1.1",
//                duration = 26,
//                exception = null
//            )
//        )
    }
}