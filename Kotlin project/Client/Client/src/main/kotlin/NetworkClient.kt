package org.example

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.HttpMethod
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.ktor.websocket.*
import kotlinx.coroutines.*
import java.util.Scanner
//fasfa3
object NetworkClient {
    private const val HOST = "127.0.0.1"
    private const val PORT = 8080
    private const val BASE_URL = "http://$HOST:$PORT"

    private var client: HttpClient? = null

    fun initialize() {
        // Initial setup if needed
    }

    suspend fun register(username: String, password: String) {
        val httpClient = createBasicHttpClient()
        try {
            val response: HttpResponse = httpClient.post("$BASE_URL/register") {
                contentType(ContentType.Application.Json)
                setBody(User(username, password))
            }
            println(response.bodyAsText())
            // TODO: Add check if register was successful
        } catch (e: Exception) {
            println("Registration failed: ${e.message}")
        } finally {
            httpClient.close()
        }
    }

    suspend fun login(username: String, password: String): Boolean {
        val httpClient = createAuthenticatedHttpClient(username, password)
        return try {
            val response: HttpResponse = httpClient.get("$BASE_URL/login")
            println("Login response: ${response.bodyAsText()}")
            println("Status: ${response.status}")
            response.status.value in 200..299
        } catch (e: Exception) {
            println("Login failed: ${e.message}")
            false
        } finally {
            httpClient.close()
        }
    }

    suspend fun startChatSession(username: String, password: String) {
        val wsClient = createWebSocketClient(username, password)
        try {
            runBlocking {
                wsClient.webSocket(
                    method = HttpMethod.Get,
                    host = HOST,
                    port = PORT,
                    path = "/chat"
                ) {
                    val receiveJob = launchReceiveLoop()
                    val sendJob = launchSendLoop()
                    joinAll(receiveJob, sendJob)
                }
            }
        } catch (e: Exception) {
            println("Chat session error: ${e.message}")
        } finally {
            wsClient.close()
        }
    }

    private fun createBasicHttpClient(): HttpClient {
        return HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }
        }
    }

    private fun createAuthenticatedHttpClient(username: String, password: String): HttpClient {
        return HttpClient(CIO) {
            install(Auth) {
                basic {
                    credentials { BasicAuthCredentials(username, password) }
                    sendWithoutRequest { true }
                }
            }
        }
    }

    private fun createWebSocketClient(username: String, password: String): HttpClient {
        return HttpClient(CIO) {
            install(Auth) {
                basic {
                    credentials { BasicAuthCredentials(username, password) }
                    sendWithoutRequest { true }
                }
            }
            install(WebSockets)
            install(ContentNegotiation) {
                json(Json { isLenient = true })
            }
        }
    }

    private fun DefaultClientWebSocketSession.launchReceiveLoop() = launch {
        try {
            for (frame in incoming) {
                when (frame) {
                    is Frame.Text -> println(frame.readText())
                    is Frame.Close -> {
                        println("Connection closed by server")
                        return@launch
                    }
                    else -> {}
                }
            }
        } catch (e: Exception) {
            println("Receive loop error: ${e.message}")
        }
    }

    private fun DefaultClientWebSocketSession.launchSendLoop() = launch {
        val scanner = Scanner(System.`in`)
        try {
            while (isActive) {
                val input = scanner.nextLine()
                send(input)
            }
        } catch (e: Exception) {
            println("Send loop error: ${e.message}")
        }
    }
}