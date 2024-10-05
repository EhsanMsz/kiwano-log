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

package com.ehsanmsz.kiwanolog.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Ehsan Msz on 02 Sep, 2024
 */
@Entity(tableName = "http_request")
internal data class HttpRequestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val method: String,
    @ColumnInfo(name = "status_code")
    val statusCode: Int? = null,
    val url: String,
    val host: String,
    val port: Int,
    val path: String,
    val protocol: String,
    @ColumnInfo(name = "protocol_version")
    val protocolVersion: String? = null,
    @ColumnInfo(name = "request_headers")
    val requestHeaders: String? = null,
    @ColumnInfo(name = "request_body")
    val requestBody: String? = null,
    @ColumnInfo(name = "response_headers")
    val responseHeaders: String? = null,
    @ColumnInfo(name = "response_body")
    val responseBody: String? = null,
    @ColumnInfo(name = "request_time")
    val requestTime: Long,
    @ColumnInfo(name = "response_time")
    val responseTime: Long? = null,
    val duration: Long? = null,
    val state: HttpRequestState,
    val exception: String? = null,
    val notified: Boolean = false
)