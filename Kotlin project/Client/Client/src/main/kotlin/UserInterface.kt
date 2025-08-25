package org.example

import java.util.Scanner

object UserInterface {

    fun showMainMenu(scanner: Scanner): MenuOption? {
        println("Please Enter (1/Login, 2/Register)")
        val decision = readLine()?.trim()?.toIntOrNull() ?: -1
        return MenuOption.fromInt(decision)
    }

    fun promptCredentials(scanner: Scanner): Pair<String, String> {
        print("Enter username: ")
        val username = scanner.nextLine().trim()
        print("Enter password: ")
        val password = scanner.nextLine().trim()
        return Pair(username, password)
    }

    fun showInvalidOption() {
        println("Invalid option")
    }
}