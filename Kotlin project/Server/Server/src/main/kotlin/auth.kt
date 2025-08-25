package com.example

import io.ktor.server.application.*
import io.ktor.server.auth.*

/**
 * Configure basic authentication using the repository and shared hasher.
 */
fun Application.configureAuth() {
    authentication {
        basic("auth-basic") {
            realm = "Ktor Server"
            validate { credentials ->
                if (Repository.validate(credentials.name, credentials.password)) {
                    UserIdPrincipal(credentials.name)
                } else null
            }
        }
    }
}