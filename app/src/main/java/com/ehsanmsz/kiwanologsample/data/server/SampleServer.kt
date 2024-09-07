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

package com.ehsanmsz.kiwanologsample.data.server

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

/**
 * Created by Ehsan Msz on 02 Sep, 2024
 */
object SampleServer {
    private val sampleServerRunningState = MutableStateFlow<RunningState>(RunningState.Stopped)
    val sampleServerRunningStateFlow: StateFlow<RunningState> = sampleServerRunningState

    private var scope: CoroutineScope? = null
    private var server: NettyApplicationEngine? = null

    fun toggle() {
        when (sampleServerRunningState.value) {
            RunningState.Running -> stop()
            RunningState.Stopped -> start()
        }
    }

    fun start() {
        if (scope == null || scope?.isActive == false) {
            scope = CoroutineScope(Dispatchers.IO)
        }
        server = embeddedServer(
            factory = Netty,
            port = 8080,
            module = {
                install(ContentNegotiation) {
                    json()
                }
                configureRouting()
            }
        )
        scope?.launch {
            server!!.start(wait = true)
        }
        sampleServerRunningState.tryEmit(RunningState.Running)
    }

    fun stop() {
        runCatching { scope?.cancel() }
        scope = null
        server?.stop()
        sampleServerRunningState.tryEmit(RunningState.Stopped)
    }

    sealed class RunningState {
        data object Running : RunningState()
        data object Stopped : RunningState()
    }

    object DefaultResponse {

        val Success = buildJsonObject {
            put("status", "success")
            put("message", "This is a success message")
            put("timeMillis", System.currentTimeMillis())
            putJsonObject("data") {
                put("data", "This is a data")
                put("anotherData", "This is another data")
            }
            putJsonArray("data") {
                add("This is a data")
                add("This is another data")
            }
        }

        val Failure = buildJsonObject {
            put("status", "failed")
            put("message", "This is a failure message")
            put("timeMillis", System.currentTimeMillis())
        }

    }

}