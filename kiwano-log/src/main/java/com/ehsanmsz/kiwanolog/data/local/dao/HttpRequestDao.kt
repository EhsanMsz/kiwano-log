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

package com.ehsanmsz.kiwanolog.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ehsanmsz.kiwanolog.data.local.entity.HttpRequestEntity
import com.ehsanmsz.kiwanolog.data.local.entity.HttpRequestState

/**
 * Created by Ehsan Msz on 02 Sep, 2024
 */

@Dao
internal interface HttpRequestDao {

    @Insert
    suspend fun insertRequest(requestEntity: HttpRequestEntity): Long

    @Query(
        """
            UPDATE http_request SET
                protocol_version=:protocolVersion, 
                status_code=:statusCode,
                response_time=:responseTime,
                duration=:duration,
                response_body=:responseBody,
                response_headers=:responseHeaders,
                state=:state
                WHERE id=:id
            """
    )
    suspend fun updateRequest(
        id: Long,
        statusCode: Int,
        protocolVersion: String,
        responseTime: Long,
        duration: Long,
        responseBody: String?,
        responseHeaders: String?,
        state: HttpRequestState = HttpRequestState.Completed
    )

    @Query("UPDATE http_request SET exception=:exception, state=:state WHERE id=:id")
    suspend fun updateRequestException(
        id: Long,
        exception: String,
        state: HttpRequestState = HttpRequestState.Failed
    )

    @Query("UPDATE http_request SET request_headers=:requestHeaders, request_body=:requestBody WHERE id=:id")
    suspend fun updateRequestBodyAndHeader(id: Long, requestHeaders: String?, requestBody: String?)

    @Query("UPDATE http_request SET state=:state WHERE state IS 'Running'")
    suspend fun setStateForAllRequests(state: HttpRequestState)

    @Query("UPDATE http_request SET state=:state WHERE id=:id")
    suspend fun logUnknown(id: Long, state: HttpRequestState = HttpRequestState.Unknown)

    @Query("DELETE FROM http_request")
    suspend fun clearRequests()
}