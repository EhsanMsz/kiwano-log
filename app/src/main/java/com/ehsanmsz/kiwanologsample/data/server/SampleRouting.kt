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

package com.ehsanmsz.kiwanologsample.data.server

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.routing
import kotlinx.coroutines.delay

/**
 * Created by Ehsan Msz on 02 Sep, 2024
 */
internal fun Application.configureRouting() {
    routing {

        get("/") {
            call.respond(
                status = HttpStatusCode.OK,
                message = SampleServer.DefaultResponse.Success
            )
        }

        post("/successExampleWithBody") {
            call.respond(
                status = HttpStatusCode.OK,
                message = SampleServer.DefaultResponse.Success
            )
        }

        post("/delayed/successExampleWithBody") {
            delay(500)
            call.respond(
                status = HttpStatusCode.OK,
                message = SampleServer.DefaultResponse.Success
            )
        }

        put("/delayed/failureExampleWithBody") {
            delay(200)
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = SampleServer.DefaultResponse.Failure
            )
        }

        delete("/serverErrorExample") {
            call.respond(
                status = HttpStatusCode.InternalServerError,
                message = SampleServer.DefaultResponse.Failure
            )
        }

    }
}