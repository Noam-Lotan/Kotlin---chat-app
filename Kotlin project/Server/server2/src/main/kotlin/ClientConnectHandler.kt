package com.example

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.getDigestFunction
import kotlinx.serialization.Serializable
import java.util.concurrent.ConcurrentHashMap
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json

@Serializable
data class User(
    val username: String,
    val password: String
)

fun Application.configureConnection() {
    val userStorage = mutableListOf<User>()
    userStorage.addAll(
        arrayOf(
            User("admin","password"),
            User("user","user")

        )
    )

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }

    val digestFunction = getDigestFunction("SHA-256") { "ktor${it.length}" }

    val hashedUserTable: MutableMap<String, ByteArray> = ConcurrentHashMap<String, ByteArray>().apply {
        put("jetbrains", digestFunction("foobar"))
        put("admin", digestFunction("password"))
    }

    // Extension function to authenticate credentials against the hashed user table
    fun MutableMap<String, ByteArray>.authenticate(credentials: UserPasswordCredential): UserIdPrincipal? {
        val hashedPassword = this[credentials.name]
        return if (hashedPassword != null && hashedPassword.contentEquals(digestFunction(credentials.password))) {
            UserIdPrincipal(credentials.name)
        } else {
            null
        }
    }

    authentication {
        basic("auth-basic-hashed") {
            realm = "Access to the '/' path"
            validate { credentials ->
                hashedUserTable.authenticate(credentials)
            }
        }
    }

    routing {
        post("/register"){
            val user = call.receive<User>()
            val prev = hashedUserTable.putIfAbsent(user.username, digestFunction(user.password))

            if (prev == null) {
                call.respondText("User ${user.username} registered successfully", status = HttpStatusCode.Created)
            } else {
                call.respondText("User ${user.username} already exists", status = HttpStatusCode.Conflict)
            }

        }

        // Protected route that requires authentication
        authenticate("auth-basic-hashed") {
            get("/protected/route/basic") {
                val principal = call.principal<UserIdPrincipal>()!!
                call.respondText("Hello ${principal.name}")
            }
        }
    }
}