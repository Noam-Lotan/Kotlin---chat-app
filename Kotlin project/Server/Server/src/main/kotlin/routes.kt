package com.example

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Register route and a protected login test route.
 */
fun Application.configureRoutes() {
    routing {
        //calls repository.register to check if the register success and return if succed or not.
        post("/register") {
            val user = call.receive<User>()
            val ok = Repository.register(user.username, user.password)
            if (ok) {
                call.respondText("User ${user.username} registered successfully", status = HttpStatusCode.Created)
            } else {
                call.respondText("User ${user.username} already exists", status = HttpStatusCode.Conflict)
            }
        }
        // authenticate Protects the following routes with Basic Authentication using the "auth-basic" config
        authenticate("auth-basic") {
            //respond with garbage (the idea is to check if the login have been succesful or not)
            get("/login") {
                val principal = call.principal<UserIdPrincipal>()!!
                call.respondText("Hello ${principal.name}")
            }
        }
    }
}