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

package com.ehsanmsz.kiwanolog.data.repository.mapper

import com.ehsanmsz.kiwanolog.data.local.entity.HttpRequestEntity
import com.ehsanmsz.kiwanolog.domain.model.KiwanoHttpHeader
import com.ehsanmsz.kiwanolog.domain.model.KiwanoHttpModel
import io.ktor.utils.io.printStack
import kotlinx.serialization.json.Json

/**
 * Created by Ehsan Msz on 05 Oct, 2024
 */

private val json = Json {
    isLenient = true
    ignoreUnknownKeys = true
    explicitNulls = false
}

internal fun HttpRequestEntity.toKiwanoHttpModel() = KiwanoHttpModel(
    info = KiwanoHttpModel.Info(
        method = method,
        url = url,
        host = host,
        port = port,
        path = path,
        protocol = protocol,
        statusCode = statusCode,
        state = state,
        duration = duration,
        exception = exception,
        protocolVersion = protocolVersion
    ),
    request = KiwanoHttpModel.Request(
        headers = requestHeaders?.toKiwanoHttpHeaderArray() ?: emptyArray(),
        body = requestBody,
        timeMillis = requestTime
    ),
    response = KiwanoHttpModel.Response(
        headers = responseHeaders?.toKiwanoHttpHeaderArray(),
        body = responseBody,
        timeMillis = responseTime
    )
)

internal fun Set<Map.Entry<String, List<String>>>.toKiwanoHttpHeaderArray(): Array<KiwanoHttpHeader> =
    map { KiwanoHttpHeader(it.key, it.value) }.toTypedArray()

internal fun String.toKiwanoHttpHeaderArray(): Array<KiwanoHttpHeader> = try {
    json.decodeFromString(this)
} catch (t: Throwable) {
    t.printStack()
    emptyArray()
}
