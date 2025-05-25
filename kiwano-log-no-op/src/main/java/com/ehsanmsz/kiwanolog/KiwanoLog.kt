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
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.util.AttributeKey

/**
 * Created by Ehsan Msz on 02 Sep, 2024
 */
class KiwanoLog private constructor(private val context: Context) {

    class Config {
        var applicationContext: Context? = null
        fun setContext(context: Context) {
            applicationContext = context.applicationContext
        }
    }

    companion object Plugin : HttpClientPlugin<Config, KiwanoLog> {
        override val key = AttributeKey<KiwanoLog>("KiwanoLog")
        override fun install(plugin: KiwanoLog, scope: HttpClient) {
            /* no-op */
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