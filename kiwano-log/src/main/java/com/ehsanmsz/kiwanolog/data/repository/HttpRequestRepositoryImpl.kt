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

package com.ehsanmsz.kiwanolog.data.repository

import com.ehsanmsz.kiwanolog.data.local.dao.HttpRequestDao
import com.ehsanmsz.kiwanolog.data.local.entity.HttpRequestEntity
import com.ehsanmsz.kiwanolog.data.local.entity.HttpRequestState
import com.ehsanmsz.kiwanolog.domain.repository.HttpRequestRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Created by Ehsan Msz on 04 Sep, 2024
 */
@OptIn(DelicateCoroutinesApi::class)
internal class HttpRequestRepositoryImpl(
    private val httpRequestDao: HttpRequestDao
) : HttpRequestRepository {

    private val json = Json {
        isLenient = true
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    override suspend fun logRequest(
        method: String,
        host: String,
        port: Int,
        path: String,
        protocol: String
    ): Long? {
        return try {
            httpRequestDao.insertRequest(
                HttpRequestEntity(
                    method = method,
                    port = port,
                    host = host,
                    path = path,
                    protocol = protocol,
                    requestTime = System.currentTimeMillis(),
                    state = HttpRequestState.Running
                )
            )
        } catch (t: Throwable) {
            null
        }
    }

    override fun logRequestBodyAndHeader(
        id: Long,
        requestBody: String?,
        requestHeaders: Set<Map.Entry<String, List<String>>>
    ) {
        logSafe(id) {
            httpRequestDao.updateRequestBodyAndHeader(
                id = id,
                requestBody = requestBody,
                requestHeaders = json.encodeToString(requestHeaders)
            )
        }
    }

    override fun logRequestException(id: Long, exception: String) {
        logSafe(id) {
            httpRequestDao.updateRequestException(
                id = id,
                exception = exception
            )
        }
    }

    override fun logResponse(
        id: Long,
        statusCode: Int,
        responseBody: String?,
        responseHeaders: Set<Map.Entry<String, List<String>>>,
        protocolVersion: String,
        responseTime: Long,
        duration: Long
    ) {
        logSafe(id) {
            httpRequestDao.updateRequest(
                id = id,
                statusCode = statusCode,
                protocolVersion = protocolVersion,
                responseTime = responseTime,
                duration = duration,
                responseBody = responseBody,
                responseHeaders = json.encodeToString(responseHeaders)
            )
        }
    }

    override fun completeAllPendingLogs() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                httpRequestDao.setStateForAllRequests(HttpRequestState.Unknown)
            } catch (t: Throwable) {
                /* no-op */
            }
        }
    }

    override fun clearAllRequests() {
        GlobalScope.launch(Dispatchers.IO) {
            runCatching { httpRequestDao.clearRequests() }
        }
    }

    private fun logSafe(
        id: Long,
        block: suspend () -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            supervisorScope {
                try {
                    block()
                } catch (t: Throwable) {
                    httpRequestDao.logUnknown(id)
                }
            }
        }
    }

}