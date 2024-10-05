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

package com.ehsanmsz.kiwanolog.domain.repository

import com.ehsanmsz.kiwanolog.domain.model.KiwanoHttpHeader
import com.ehsanmsz.kiwanolog.domain.model.KiwanoHttpModel
import kotlinx.coroutines.flow.Flow

/**
 * Created by Ehsan Msz on 04 Sep, 2024
 */
internal interface HttpRequestRepository {

    /**
     * Logs information of the request.
     *
     * @return The id of the request in the database
     */
    suspend fun logRequest(
        method: String,
        url: String,
        host: String,
        port: Int,
        path: String,
        protocol: String
    ): Long?


    /**
     * Logs the existing [requestBody] and [requestHeaders] with the given [id]
     */
    fun logRequestBodyAndHeader(
        id: Long,
        requestBody: String?,
        requestHeaders: Array<KiwanoHttpHeader>
    )


    /**
     * Logs request [exception] with the given [id]
     */
    fun logRequestException(
        id: Long,
        exception: String
    )

    /**
     * Logs the response of the request with the given [id]
     */
    fun logResponse(
        id: Long,
        statusCode: Int,
        responseBody: String?,
        responseHeaders: Array<KiwanoHttpHeader>,
        protocolVersion: String,
        responseTime: Long,
        duration: Long
    )

    /**
     * Completes any pending request logs that didn't have the opportunity to be completed
     */
    fun completeAllPendingLogs()

    /**
     * Clears all the requests from the database
     */
    fun clearAllRequests()


    /**
     * Returns the last request that hasn't been notified yet
     */
    fun lastNotNotifiedRequest(): Flow<KiwanoHttpModel>
}