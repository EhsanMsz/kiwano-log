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

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.ehsanmsz.kiwanolog.data.local.dao.HttpRequestDao
import com.ehsanmsz.kiwanolog.data.local.entity.HttpRequestEntity
import com.ehsanmsz.kiwanolog.data.local.entity.HttpRequestState
import com.ehsanmsz.kiwanolog.data.repository.mapper.toKiwanoHttpModel
import com.ehsanmsz.kiwanolog.domain.model.KiwanoHttpHeader
import com.ehsanmsz.kiwanolog.domain.model.KiwanoHttpModel
import com.ehsanmsz.kiwanolog.domain.repository.KiwanoRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Created by Ehsan Msz on 04 Sep, 2024
 */

/**
 * KiwanoRepositoryImpl
 */
@OptIn(DelicateCoroutinesApi::class)
internal class KiwanoRepositoryImpl(
    private val httpRequestDao: HttpRequestDao
) : KiwanoRepository {

    private val json = Json {
        isLenient = true
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    override fun logs(searchText: String): Flow<PagingData<KiwanoHttpModel>> = Pager(
        config = PagingConfig(pageSize = 20, prefetchDistance = 10, initialLoadSize = 50),
        pagingSourceFactory = { httpRequestDao.logs(searchText) }
    ).flow.map { it.map { it.toKiwanoHttpModel() } }


    override suspend fun logRequest(
        method: String,
        url: String,
        host: String,
        port: Int,
        path: String,
        protocol: String
    ): Long? {
        return try {
            httpRequestDao.insertRequest(
                HttpRequestEntity(
                    method = method,
                    url = url,
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
        requestHeaders: Array<KiwanoHttpHeader>
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
        responseHeaders: Array<KiwanoHttpHeader>,
        responseSize: Int,
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
                responseSize = responseSize,
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

    override fun lastNotNotifiedRequest(): Flow<KiwanoHttpModel> =
        httpRequestDao.lastNotNotifiedRequest()
            .mapNotNull { it?.toKiwanoHttpModel() }
            .onEach { model -> httpRequestDao.setNotified(model.id) }

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