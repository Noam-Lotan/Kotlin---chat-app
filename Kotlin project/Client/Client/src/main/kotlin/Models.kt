package org.example

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val username: String,
    val password: String
)

enum class MenuOption(val value: Int, val description: String) {
    LOGIN(1, "Login"),
    REGISTER(2, "Register");

    companion object {
        fun fromInt(value: Int): MenuOption? {
            return values().find { it.value == value }
        }
    }
}