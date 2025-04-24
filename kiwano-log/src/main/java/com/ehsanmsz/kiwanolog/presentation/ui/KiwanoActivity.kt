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

package com.ehsanmsz.kiwanolog.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.ehsanmsz.kiwanolog.presentation.ui.screen.home.HomeScreen
import com.ehsanmsz.kiwanolog.presentation.ui.screen.home.detail.HttpRequestDetailScreen
import com.ehsanmsz.kiwanolog.presentation.ui.theme.AppTheme

/**
 * Created by Ehsan Msz on 29 Mar, 2025
 */

internal class KiwanoActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                Main()
            }
        }
    }


    @Composable
    private fun Main() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = KiwanoScreen.Home) {

            composable<KiwanoScreen.Home> {
                HomeScreen(
                    navigateToDetail = { navController.navigate(KiwanoScreen.Detail(it)) }
                )
            }

            composable<KiwanoScreen.Detail> {
                val detail = it.toRoute<KiwanoScreen.Detail>()
                HttpRequestDetailScreen(
                    logId = detail.logId,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}