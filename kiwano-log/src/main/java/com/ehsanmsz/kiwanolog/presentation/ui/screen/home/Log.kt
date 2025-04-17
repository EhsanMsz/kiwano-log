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

package com.ehsanmsz.kiwanolog.presentation.ui.screen.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.ehsanmsz.kiwanolog.R
import com.ehsanmsz.kiwanolog.data.local.entity.HttpRequestState
import com.ehsanmsz.kiwanolog.domain.model.KiwanoHttpModel
import com.ehsanmsz.kiwanolog.presentation.ui.theme.AppTheme
import com.ehsanmsz.kiwanolog.presentation.util.TimeUtil

/**
 * Created by Ehsan Msz on 03 Apr, 2025
 */

@Composable
internal fun Log(httpModel: KiwanoHttpModel) {
    ConstraintLayout(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .requiredHeight(92.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        val (pathRef, statusCodeRef, timeRef, sizeRef) = createRefs()

        AnimatedContent(
            modifier = Modifier.constrainAs(statusCodeRef) {
                bottom.linkTo(parent.bottom, 8.dp)
                start.linkTo(parent.start, 12.dp)
            },
            targetState = httpModel.info.statusCode == null
        ) {
            if (!it) {
                Text(
                    modifier = Modifier
                        .background(
                            color = when (httpModel.info.statusCode) {
                                in 200..299 -> MaterialTheme.colorScheme.primaryContainer
                                in 400..599 -> MaterialTheme.colorScheme.errorContainer
                                else -> Color.Transparent
                            },
                            shape = RoundedCornerShape(50)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                    text = "${httpModel.info.method} ${httpModel.info.statusCode}",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelLarge,
                    color = when (httpModel.info.statusCode) {
                        in 200..299 -> MaterialTheme.colorScheme.onPrimaryContainer
                        in 400..599 -> MaterialTheme.colorScheme.onErrorContainer
                        else -> Color.Transparent
                    }
                )
            } else {
                CircularProgressIndicator(
                    modifier = Modifier.requiredSize(18.dp),
                    strokeWidth = 2.dp
                )
            }
        }

        Row(
            modifier = Modifier.constrainAs(pathRef) {
                start.linkTo(parent.start, 12.dp)
                top.linkTo(parent.top, 8.dp)
                end.linkTo(parent.end, 12.dp)
                width = Dimension.fillToConstraints
            }
        ) {
            Text(
                text = "/",
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                maxLines = 2,
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis,
                text = httpModel.info.path.removePrefix("/"),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        Row(
            modifier = Modifier.constrainAs(timeRef) {
                end.linkTo(parent.end, 12.dp)
                bottom.linkTo(parent.bottom, 12.dp)
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .size(12.dp)
                    .padding(end = 2.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_kiwano_schedule),
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = "RequestTime",
            )
            Text(
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis,
                text = TimeUtil.getFormattedTime(httpModel.request.timeMillis),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelSmall
            )
        }

//        Row(
//            modifier = Modifier.constrainAs(sizeRef) {
//                end.linkTo(timeRef.start, 24.dp)
//                bottom.linkTo(parent.bottom, 12.dp)
//            },
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Icon(
//                modifier = Modifier.size(12.dp).padding(end = 2.dp),
//                imageVector = ImageVector.vectorResource(R.drawable.ic_kiwano_description),
//                tint = MaterialTheme.colorScheme.onSurface,
//                contentDescription = "RequestTime",
//            )
//            Text(
//                textAlign = TextAlign.Start,
//                overflow = TextOverflow.Ellipsis,
//                text = SizeUtil.formatSize(),
//                color = MaterialTheme.colorScheme.onSurface,
//                style = MaterialTheme.typography.labelSmall
//            )
//        }

    }
}

@PreviewLightDark
@Composable
private fun Preview() {
    AppTheme {
        Log(
            httpModel = KiwanoHttpModel(
                id = 0,
                info = KiwanoHttpModel.Info(
                    method = "GET",
                    statusCode = 200,
                    url = "http://localhost:8080/successExampleWithBody",
                    host = "localhost",
                    port = 8080,
                    path = "/successExampleWithBody",
                    protocol = "HTTP",
                    protocolVersion = "1.1",
                    duration = 23,
                    state = HttpRequestState.Completed,
                    exception = null
                ),
                request = KiwanoHttpModel.Request(
                    headers = arrayOf(),
                    body = "",
                    timeMillis = 1742660192741
                ),
                response = KiwanoHttpModel.Response(
                    headers = arrayOf(),
                    body = "",
                    timeMillis = 1742660192778
                )
            )
        )
    }
}