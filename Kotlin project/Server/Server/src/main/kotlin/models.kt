package com.example

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val username: String,
    val password: String
)

@Serializable
data class MessageResponse(val message: String)
