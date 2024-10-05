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

package com.ehsanmsz.kiwanolog.device.logger

import android.content.Context
import com.ehsanmsz.kiwanolog.data.repository.HttpRequestRepositoryProvider
import com.ehsanmsz.kiwanolog.data.repository.mapper.toKiwanoHttpHeaderArray
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Created by Ehsan Msz on 02 Sep, 2024
 */

/**
 * This class stores and shows (via notification) the http requests
 */
internal class KiwanoLogger(private val context: Context) {

    private val httpRequestRepository = HttpRequestRepositoryProvider.get(context)

    init {
        httpRequestRepository.completeAllPendingLogs()
        observeLogsAndSendNotification()
    }

    /**
     * Observe database and shows notification
     */
    @OptIn(DelicateCoroutinesApi::class)
    private fun observeLogsAndSendNotification() {
        GlobalScope.launch(Dispatchers.IO) {
            httpRequestRepository.lastNotNotifiedRequest().collect {

            }
        }
    }

    suspend fun logRequest(
        method: String,
        url: String,
        host: String,
        port: Int,
        path: String,
        protocol: String
    ): Long? = httpRequestRepository.logRequest(
        url = url,
        method = method,
        host = host,
        port = port,
        path = if (path.startsWith("/")) path else "/$path",
        protocol = protocol
    )

    fun logRequestException(
        id: Long,
        throwable: Throwable
    ) {
        httpRequestRepository.logRequestException(
            id = id,
            exception = throwable.stackTraceToString()
        )
    }

    fun logRequestBodyAndHeader(
        id: Long,
        requestBody: String?,
        requestHeaders: Set<Map.Entry<String, List<String>>>
    ) {
        httpRequestRepository.logRequestBodyAndHeader(
            id = id,
            requestBody = requestBody,
            requestHeaders = requestHeaders.toKiwanoHttpHeaderArray()
        )
    }

    fun logResponse(
        id: Long,
        statusCode: Int,
        responseBody: String?,
        responseHeaders: Set<Map.Entry<String, List<String>>>,
        protocolVersion: String,
        responseTime: Long,
        duration: Long
    ) {
        httpRequestRepository.logResponse(
            id = id,
            statusCode = statusCode,
            responseBody = responseBody,
            responseHeaders = responseHeaders.toKiwanoHttpHeaderArray(),
            protocolVersion = protocolVersion,
            responseTime = responseTime,
            duration = duration
        )
    }
}
