package org.example

import kotlinx.coroutines.*
import java.util.Scanner

object ChatApplication {
    private lateinit var networkClient: NetworkClient
    private lateinit var userInterface: UserInterface

    suspend fun run() {
        initialize()
        startMainLoop()
    }

    private suspend fun initialize() {
        networkClient = NetworkClient
        userInterface = UserInterface
        networkClient.initialize()
    }

    private suspend fun startMainLoop() {
        val scanner = Scanner(System.`in`)

        while (true) {
            when (val option = userInterface.showMainMenu(scanner)) {
                MenuOption.LOGIN -> handleLogin(scanner)
                MenuOption.REGISTER -> handleRegister(scanner)
                null -> userInterface.showInvalidOption()
            }
        }
    }

    private suspend fun handleLogin(scanner: Scanner) {
        val credentials = userInterface.promptCredentials(scanner)
        if (networkClient.login(credentials.first, credentials.second)) {
            networkClient.startChatSession(credentials.first, credentials.second)
        }
    }

    private suspend fun handleRegister(scanner: Scanner) {
        val credentials = userInterface.promptCredentials(scanner)
        networkClient.register(credentials.first, credentials.second)
    }
}