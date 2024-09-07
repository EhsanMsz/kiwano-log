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

package com.ehsanmsz.kiwanologsample.data.client

import android.content.Context
import com.ehsanmsz.kiwanolog.kiwanoLog
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Created by Ehsan Msz on 02 Sep, 2024
 */
class SampleClient(context: Context) {
    private var job: Job? = null

    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    explicitNulls = false
                }
            )
        }

        kiwanoLog(context)

        defaultRequest {
            url {
                url("http://localhost:8080/")
                port = 8080
            }
            headers {
                append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun request() {
        runCatching { job?.cancel() }
        job = GlobalScope.launch(Dispatchers.IO) {

            httpClient.runHttpRequest {
                get { }
            }

            //does not exists
            httpClient.runHttpRequest {
                delete {
                    setBody(DefaultRequest.Json)
                    url {
                        path("successExampleWithBody")
                    }
                }
            }

            httpClient.runHttpRequest {
                post {
                    setBody(DefaultRequest.Json)
                    url {
                        path("successExampleWithBody")
                    }
                }
            }

            httpClient.runHttpRequest {
                post {
                    setBody(DefaultRequest.Json)
                    url {
                        path("delayed/successExampleWithBody")
                    }
                }
            }

            httpClient.runHttpRequest {
                put {
                    setBody(DefaultRequest.Json)
                    url {
                        path("delayed/failureExampleWithBody")
                    }
                }
            }

            httpClient.runHttpRequest {
                delete {
                    url {
                        path("serverErrorExample")
                    }
                }
            }
        }
    }

    private suspend fun HttpClient.runHttpRequest(block: suspend HttpClient.() -> Unit) {
        try {
            block()
        } catch (t: Throwable) {
            println("HttpError: $t")
        }
    }

    object DefaultRequest {
        val Json = buildJsonObject {
            put("type", "json")
            put("message", "This is a json message")
        }
    }
}
