package com.example

import java.util.concurrent.ConcurrentHashMap
import io.ktor.util.getDigestFunction

/**
 * Simple in-memory user store. For production, needed replace with persistent DB.
 */

object Repository {
    private val users = ConcurrentHashMap<String, ByteArray>()

    init {
        // default users for testing
        users.put("admin", PasswordHasher.hash("password"))
        users["user"] = PasswordHasher.hash("user")
    }

    /**
     * Centralized password hashing so both auth and repository use the same function.
     */
    object PasswordHasher {
        private val digest = getDigestFunction("SHA-256") { "ktor${it.length}" }
        fun hash(password: String): ByteArray = digest(password)
    }

    fun register(username: String, password: String): Boolean {
        val prev = users.putIfAbsent(username, PasswordHasher.hash(password))
        return prev == null
    }

    fun validate(username: String, password: String): Boolean {
        val stored = users[username] ?: return false
        return stored.contentEquals(PasswordHasher.hash(password))
    }
}