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

package com.ehsanmsz.kiwanolog.data.local.database.converter

import androidx.room.TypeConverter
import com.ehsanmsz.kiwanolog.data.local.entity.HttpRequestState

/**
 * Created by Ehsan Msz on 04 Sep, 2024
 */
internal class HttpRequestStateConverter {

    @TypeConverter
    fun fromHttpRequestState(state: HttpRequestState): String = state.name


    @TypeConverter
    fun toHttpRequestState(state: String): HttpRequestState = HttpRequestState.fromString(state)

}