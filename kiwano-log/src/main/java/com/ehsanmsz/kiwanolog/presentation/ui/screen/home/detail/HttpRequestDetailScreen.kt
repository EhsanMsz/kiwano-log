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

package com.ehsanmsz.kiwanolog.presentation.ui.screen.home.detail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ehsanmsz.kiwanolog.R
import com.ehsanmsz.kiwanolog.presentation.ui.screen.home.detail.component.HttpInfo
import com.ehsanmsz.kiwanolog.presentation.ui.screen.home.detail.component.HttpRequest
import com.ehsanmsz.kiwanolog.presentation.ui.screen.home.detail.component.HttpResponse

/**
 * Created by Ehsan Msz on 24 Apr, 2025
 */

@Composable
internal fun HttpRequestDetailScreen(
    logId: Long,
    onBack: () -> Unit,
    viewModel: HttpRequestDetailViewModel = viewModel()
) {

    var selectedNavigationBar by remember { mutableStateOf<LogDetail>(LogDetail.Info) }
    val navigationBarItems =
        remember { listOf(LogDetail.Info, LogDetail.Request, LogDetail.Response) }

    val httpModel by viewModel.model.collectAsState(null)

    LaunchedEffect(Unit) {
        viewModel.fetchLog(logId)
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                navigationBarItems.forEach {
                    NavigationBarItem(
                        selected = selectedNavigationBar == it,
                        onClick = { selectedNavigationBar = it },
                        label = {
                            Text(
                                text = stringResource(
                                    when (it) {
                                        LogDetail.Info -> R.string.kiwano_info
                                        LogDetail.Request -> R.string.kiwano_request
                                        LogDetail.Response -> R.string.kiwano_response
                                    }
                                )
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = ImageVector.vectorResource(
                                    when (it) {
                                        LogDetail.Info -> R.drawable.ic_kiwano_info
                                        LogDetail.Request -> R.drawable.ic_kiwano_arrow_up
                                        LogDetail.Response -> R.drawable.ic_kiwano_arrow_down
                                    }
                                ),
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        AnimatedContent(
            modifier = Modifier.padding(paddingValues),
            targetState = selectedNavigationBar,
            transitionSpec = { fadeIn(tween(500)).togetherWith(fadeOut(tween(500))) }
        ) {
            when (it) {
                LogDetail.Info -> HttpInfo(httpModel)
                LogDetail.Request -> HttpRequest(httpModel?.request)
                LogDetail.Response -> HttpResponse(httpModel?.response)
            }
        }
    }
}
