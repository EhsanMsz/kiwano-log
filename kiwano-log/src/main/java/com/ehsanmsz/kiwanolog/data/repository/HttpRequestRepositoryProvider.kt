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

import android.content.Context
import com.ehsanmsz.kiwanolog.data.local.database.KiwanoLogDatabase
import com.ehsanmsz.kiwanolog.domain.repository.HttpRequestRepository

/**
 * Created by Ehsan Msz on 04 Sep, 2024
 */

/**
 * This class provides the [HttpRequestRepository] instance
 */
internal object HttpRequestRepositoryProvider {

    @Volatile
    private var httpRequestRepository: HttpRequestRepositoryImpl? = null

    /**
     * Returns the [HttpRequestRepository] instance (singleton)
     */
    fun get(context: Context): HttpRequestRepository {
        return synchronized(this) {
            if (httpRequestRepository == null) {
                httpRequestRepository = HttpRequestRepositoryImpl(
                    KiwanoLogDatabase.buildDatabase(context).getHttpRequestDao()
                )
            }
            httpRequestRepository!!
        }
    }
}