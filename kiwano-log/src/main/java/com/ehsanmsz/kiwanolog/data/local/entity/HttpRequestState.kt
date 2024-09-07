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

/**
 * Created by Ehsan Msz on 04 Sep, 2024
 */

/**
 * The state of the HTTP request
 */
internal sealed class HttpRequestState(val name: String) {

    /**
     * The request is running
     */
    data object Running : HttpRequestState("Running")

    /**
     * The request is completed
     */
    data object Completed : HttpRequestState("Completed")

    /**
     * The request is failed with an exception
     */
    data object Failed : HttpRequestState("Failed")

    /**
     * The request state is unknown
     * The manager could not conclude the state of the request
     */
    data object Unknown : HttpRequestState("Unknown")

    companion object {
        fun fromString(state: String): HttpRequestState {
            return when (state) {
                Running.name -> Running
                Completed.name -> Completed
                Failed.name -> Failed
                Unknown.name -> Unknown
                else -> Unknown
            }
        }
    }
}