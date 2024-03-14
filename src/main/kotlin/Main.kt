import users.Role
import users.UserManager
import menu.MenuManager
import users.UsersFileManager
import menu.OrderProcessor
import menu.ReviewManager

fun main() {
    val userManager = UserManager(UsersFileManager("users.txt"))
    val menuManager = MenuManager("menu.txt")
    val orderProcessor = OrderProcessor("orders.txt", menuManager)
    val reviewManager = ReviewManager("review.txt")

    val functions = Functions(userManager, menuManager, orderProcessor, reviewManager)

    while (true) {
        println("Choose action: 1 - Registration, 2 - Authentication, 3 - Exit")
        when (readLine()?.toIntOrNull()) {
            1 -> {
                println("Choose user type: 1 - Customer, 2 - Admin")
                val role = when (readLine()?.toIntOrNull()) {
                    1 -> Role.CUSTOMER
                    2 -> Role.ADMIN
                    else -> {
                        println("Incorrect user type.")
                        continue
                    }
                }
                functions.registerUser(role)
            }
            2 -> {
                println("Choose user type: 1 - Customer, 2 - Admin")
                val role = when (readLine()?.toIntOrNull()) {
                    1 -> Role.CUSTOMER
                    2 -> Role.ADMIN
                    else -> {
                        println("Incorrect user type.")
                        continue
                    }
                }
                println("Enter login:")
                val login = readLine().orEmpty()
                println("Enter password:")
                val password = readLine().orEmpty()
                if (functions.authenticateUser(login, password, role)) {
                    when (role) {
                        Role.ADMIN -> functions.adminMenu()
                        Role.CUSTOMER -> functions.customerMenu()
                    }
                }
            }
            3 -> {
                println("Exiting the program.")
                break
            }
            else -> println("Incorrect action.")
        }
    }
}