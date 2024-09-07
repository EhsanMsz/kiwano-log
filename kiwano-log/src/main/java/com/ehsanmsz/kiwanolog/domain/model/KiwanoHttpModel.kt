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

package com.ehsanmsz.kiwanolog.domain.model

import com.ehsanmsz.kiwanolog.data.local.entity.HttpRequestState

/**
 * Created by Ehsan Msz on 04 Sep, 2024
 */
internal class KiwanoHttpModel(
    val info: Info,
    val request: Request,
    val response: Response? = null,
) {

    data class Info(
        val method: String,
        val statusCode: Int? = null,
        val scheme: String,
        val host: String,
        val port: Int,
        val path: String,
        val protocol: String,
        val protocolVersion: String? = null,
        val duration: Long? = null,
        val state: HttpRequestState,
        val exception: String? = null,
    )

    data class Request(
        val headers: Set<Map.Entry<String, List<String>>>,
        val body: String?,
        val timeMillis: Long,
    )

    data class Response(
        val headers: Set<Map.Entry<String, List<String>>>? = null,
        val body: String? = null,
        val timeMillis: Long? = null,
    )
}