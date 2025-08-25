package com.example

import io.ktor.http.ContentType
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get("/test1"){
            val text = "<h1>Hello from Ktor</h1>"
            val type = ContentType.parse("text/html")
            call.respondText(text, type)
        }
        post("/register"){
            val creds = call.receive<User>()
        }
        // this route to authenticate (Login)
        authenticate("auth-basic-hashed") {
            get("/protected/route/basic") {
                val principal = call.principal<UserIdPrincipal>()!!
                call.respondText("Hello ${principal.name}")
            }
        }
    }
}
