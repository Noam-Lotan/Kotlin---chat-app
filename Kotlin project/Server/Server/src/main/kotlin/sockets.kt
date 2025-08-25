package com.example

import io.ktor.server.application.*
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    // Broadcast hub: all messages flow through here to reach all connected clients
    val messageResponseFlow = MutableSharedFlow<MessageResponse>()
    val sharedFlow = messageResponseFlow.asSharedFlow()

    routing {
        // authenticate Protects the following routes with Basic Authentication using the "auth-basic" config
;        authenticate("auth-basic") {
            webSocket("/chat") {
                val principalName = call.principal<UserIdPrincipal>()?.name // TO DO: handle null

                send("[system] Welcome, $principalName!")

                // Launch coroutine to forward broadcast messages to this specific session from the shared flow
                val distributor = launch() {
                    sharedFlow.collect { message ->
                        send(message.message)
                    }
                }

                // Handle incoming messages and broadcast them to all sessions (emit to the flow)
                runCatching {
                    incoming.consumeEach { frame ->
                        if (frame is Frame.Text) {
                            val receivedText = frame.readText()
                            val messageResponse = MessageResponse("${principalName}: $receivedText")
                            // Emit to shared flow - will reach all connected sessions
                            messageResponseFlow.emit(messageResponse)
                        }
                    }
                }.onFailure { exception ->
                    println("WebSocket exception: ${exception.localizedMessage}")
                }.also {
                    // Clean up: cancel the distributor when connection ends
                    distributor.cancel()
                }
            }
        }
    }
}