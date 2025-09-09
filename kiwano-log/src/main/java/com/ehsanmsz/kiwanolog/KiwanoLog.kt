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

package com.ehsanmsz.kiwanolog

import android.content.Context
import com.ehsanmsz.kiwanolog.device.logger.KiwanoLogger
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.replaceResponse
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.plugins.isSaved
import io.ktor.client.request.HttpSendPipeline
import io.ktor.client.statement.HttpReceivePipeline
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HeadersBuilder
import io.ktor.http.HttpHeaders
import io.ktor.http.append
import io.ktor.http.content.OutgoingContent
import io.ktor.http.encodedPath
import io.ktor.util.AttributeKey
import io.ktor.util.appendAll
import io.ktor.util.split
import io.ktor.utils.io.InternalAPI
import io.ktor.utils.io.readRemaining
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.io.readByteArray

/**
 * Created by Ehsan Msz on 02 Sep, 2024
 */
class KiwanoLog private constructor(private val context: Context) {

    private val idAttribute = AttributeKey<Long>("KiwanoLogIdAttribute")
    private val kiwanoLogger = KiwanoLogger(context)

    private fun initRequestLogging(scope: HttpClient) {
        scope.sendPipeline.intercept(HttpSendPipeline.Monitoring) {

            val id = kiwanoLogger.logRequest(
                method = context.method.value,
                url = context.url.toString(),
                host = context.url.host,
                port = context.url.port,
                path = context.url.encodedPath,
                protocol = context.url.protocol.name.uppercase(),
            )
            if (id != null) {
                context.attributes.put(idAttribute, id)
            }

            try {
                val outgoingContent = context.body as? OutgoingContent
                var requestSize: Int? = null

                val body = when (outgoingContent) {
                    is OutgoingContent.ByteArrayContent -> {
                        val requestBytes = outgoingContent.bytes()
                        requestSize = requestBytes.size

                        if (requestSize <= 2 * 1024 * 1024)
                            String(bytes = requestBytes, charset = Charsets.UTF_8)
                        else
                            "Body Omitted"
                    }

                    is OutgoingContent.NoContent -> null
                    else -> "Body Omitted"
                }

                //log request body and headers
                if (id != null) {
                    kiwanoLogger.logRequestBodyAndHeader(
                        id = id,
                        requestBody = body,
                        requestSize = requestSize,
                        requestHeaders = getHeadersWithContentTypeAndLength(
                            existingHeaders = context.headers,
                            contentLength = outgoingContent?.contentLength,
                            contentType = outgoingContent?.contentType
                        )
                    )
                }
            } catch (t: Throwable) {
                t.printStackTrace()
            }

            try {
                proceedWith(subject)
            } catch (t: Throwable) {
                if (id != null) {
                    kiwanoLogger.logRequestException(
                        id = id,
                        throwable = t
                    )
                }
                throw t
            }
        }
    }

    private fun getHeadersWithContentTypeAndLength(
        existingHeaders: HeadersBuilder,
        contentLength: Long?,
        contentType: ContentType?
    ): Set<Map.Entry<String, List<String>>> {

        val headerBuilder = HeadersBuilder().apply {
            appendAll(existingHeaders)
        }

        if (!headerBuilder.contains(HttpHeaders.ContentType) && contentType != null) {
            headerBuilder.append(HttpHeaders.ContentType, contentType)
        }
        if (!headerBuilder.contains(HttpHeaders.ContentLength) && contentLength != null) {
            headerBuilder.append(HttpHeaders.ContentLength, contentLength.toString())
        }

        return headerBuilder.entries()
    }

    @OptIn(DelicateCoroutinesApi::class, InternalAPI::class)
    private fun initResponseLogging(scope: HttpClient) {
        scope.receivePipeline.intercept(HttpReceivePipeline.After) { response ->

            val contentLength = response.headers[HttpHeaders.ContentLength]?.toIntOrNull() ?: 0
            val (newResponse, bodyText) = response.getResponseAndBodyAsText(contentLength)

            response.call.attributes[idAttribute].let { id ->
                kiwanoLogger.logResponse(
                    id = id,
                    statusCode = response.status.value,
                    protocolVersion = response.version.let { "${it.major}.${it.minor}" },
                    responseTime = response.responseTime.timestamp,
                    duration = response.responseTime.timestamp - response.requestTime.timestamp,
                    responseBody = bodyText,
                    responseSize = contentLength,
                    responseHeaders = response.headers.entries()
                )
            }
            proceedWith(newResponse)
        }
    }

    @OptIn(InternalAPI::class)
    private suspend fun HttpResponse.getResponseAndBodyAsText(contentLength: Int): Pair<HttpResponse, String> {
        if (contentLength > 1 * 1024 * 1024)
            return this to "Body Omitted, Content Length is greater than 1MB"

        if (isSaved)
            return this to bodyAsText()
        else {
            val (channel, copyChannel) = rawContent.split(this)
            val text = copyChannel.readRemaining().readByteArray()
            return call.replaceResponse { channel }.response to String(text, Charsets.UTF_8)
        }
    }

    class Config {
        var applicationContext: Context? = null
        fun setContext(context: Context) {
            applicationContext = context.applicationContext
        }
    }

    companion object Plugin : HttpClientPlugin<Config, KiwanoLog> {
        override val key = AttributeKey<KiwanoLog>("KiwanoLog")
        override fun install(plugin: KiwanoLog, scope: HttpClient) {
            with(plugin) {
                initRequestLogging(scope)
                initResponseLogging(scope)
            }
        }

        override fun prepare(block: Config.() -> Unit): KiwanoLog {
            val context = Config().apply(block).applicationContext
                ?: throw IllegalArgumentException("Context is not provided. Please provide context using KiwanoLog.Config.setContext(context) method.")
            return KiwanoLog(context)
        }
    }
}

fun HttpClientConfig<*>.kiwanoLog(context: Context) {
    install(KiwanoLog) {
        setContext(context)
    }
}

fun HttpClientConfig<*>.kiwanoLog(block: KiwanoLog.Config.() -> Unit) {
    install(KiwanoLog, block)
}