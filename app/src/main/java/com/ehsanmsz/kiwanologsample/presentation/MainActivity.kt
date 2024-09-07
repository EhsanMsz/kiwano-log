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

package com.ehsanmsz.kiwanologsample.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ehsanmsz.kiwanologsample.data.client.SampleClient
import com.ehsanmsz.kiwanologsample.data.server.SampleServer
import com.ehsanmsz.kiwanologsample.presentation.ui.theme.KiwanoLogTheme

class MainActivity : ComponentActivity() {

    private val sampleClient by lazy { SampleClient(applicationContext) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SampleServer.start()
        enableEdgeToEdge()
        setContent {

            val sampleServerRunningState by SampleServer.sampleServerRunningStateFlow.collectAsState()

            KiwanoLogTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Main(
                        modifier = Modifier.padding(innerPadding),
                        onRequestClick = sampleClient::request,
                        sampleServerRunningState = sampleServerRunningState,
                        onToggleHttpServerClick = SampleServer::toggle
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        SampleServer.stop()
        super.onDestroy()
    }

}

@Composable
fun Main(
    modifier: Modifier,
    onRequestClick: () -> Unit,
    sampleServerRunningState: SampleServer.RunningState,
    onToggleHttpServerClick: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = onToggleHttpServerClick) {
            Text(
                text = when (sampleServerRunningState) {
                    SampleServer.RunningState.Running -> "Stop Server"
                    SampleServer.RunningState.Stopped -> "Start Server"
                }
            )
        }
        Spacer(modifier = Modifier.padding(8.dp))
        Button(onClick = onRequestClick) {
            Text(text = "Request")
        }
    }
}
