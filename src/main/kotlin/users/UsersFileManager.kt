package users

import java.io.File

class UsersFileManager(private val filePath: String) {

    fun createUser(login: String, password: String, role: Role) {
        val userString = "$login,$password,${role.name}\n"
        File(filePath).appendText(userString)
    }

    fun getUser(login: String): User? {
        val users = getUsersFromFile()
        return users.find { it.login == login }
    }

    private fun getUsersFromFile(): List<User> {
        val file = File(filePath)
        if (!file.exists()) {
            println("File $filePath not found.")
            return emptyList()
        }

        val users = mutableListOf<User>()
        file.forEachLine { line ->
            val (name, password, role) = line.split(",")
            users.add(User(name, password, Role.valueOf(role)))
        }
        return users
    }
}
