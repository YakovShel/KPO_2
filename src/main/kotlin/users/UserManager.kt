package users

class UserManager(private val usersFileManager: UsersFileManager) {

    fun createUser(login: String, password: String, role: Role) {
        usersFileManager.createUser(login, password, role)
    }

    fun getUser(login: String): User? {
        return usersFileManager.getUser(login)
    }

    fun userExists(username: String): Boolean {
        val user = getUser(username)
        return user != null
    }
}
