import users.Role
import users.UserManager
import menu.MenuManager
import menu.OrderProcessor
import menu.ReviewManager
import users.Admin

class Functions(
    private val userManager: UserManager,
    private val menuManager: MenuManager,
    private val orderProcessor: OrderProcessor,
    private val reviewManager: ReviewManager
) {
    private var authenticatedLogin: String = ""

    fun registerUser(role: Role) {
        println("Enter login:")
        val login = readLine().orEmpty()
        println("Enter password:")
        val password = readLine().orEmpty()

        if (userManager.userExists(login)) {
            println("User $login already exists.")
        } else {
            userManager.createUser(login, password, role)
            println("User $login successfully registered!")
        }
    }

    fun authenticateUser(username: String, password: String, role: Role): Boolean {
        val user = userManager.getUser(username)
        return if (user != null && user.password == password && user.role == role) {
            true
        } else {
            println("User not found, check your data and try again.")
            false
        }
    }

    fun customerMenu() {
        while (true) {
            println("Customer Menu:")
            println("1. View Menu")
            println("2. Make Order")
            println("3. Add Item to Order")
            println("4. Delete Order")
            println("5. Pay Orders")
            println("6. Leave a Review")
            println("7. Exit")

            when (readLine()?.toIntOrNull()) {
                1 -> menuManager.displayMenu()
                2 -> orderProcessor.createOrder(authenticatedLogin)
                3 -> orderProcessor.addItemToOrder(authenticatedLogin) // Добавлен новый пункт меню
                4 -> orderProcessor.cancelOrder(authenticatedLogin)
                5 -> {
                    orderProcessor.payOrder(authenticatedLogin)
                    println("Orders paid successfully.")
                }
                6 -> {
                    val order = orderProcessor.getOrder(authenticatedLogin)
                    if (order != null) {
                        println("Your current order:")
                        order.displayOrder()

                        println("Enter the name of the item you want to review:")
                        val itemName = readLine().orEmpty()

                        println("Enter your rating (from 1 to 5):")
                        val rating = readLine()?.toIntOrNull() ?: 0

                        if (rating in 1..5) {
                            println("Enter your comment:")
                            val comment = readLine().orEmpty()

                            reviewManager.addReview(itemName, rating, comment)
                            println("Review added successfully.")
                        } else {
                            println("Invalid rating. Please enter a number from 1 to 5.")
                        }
                    } else {
                        println("No active order found for the authenticated user.")
                    }
                }
                7 -> return
                else -> println("Incorrect choice.")
            }
        }
    }

    fun adminMenu() {
        val admin = Admin(menuManager, reviewManager) // Создаем объект класса Admin

        while (true) {
            println("Admin Menu:")
            println("1. Add Item")
            println("2. Remove Item")
            println("3. Edit Item")
            println("4. View Menu")
            println("5. View Restaurant's Profit")
            println("6. View Reviews")
            println("7. View Average Ratings")
            println("8. End Shift")

            when (readLine()?.toIntOrNull()) {
                1 -> admin.addItem() // Вызываем методы Admin вместо прямого вызова
                2 -> admin.removeItem()
                3 -> admin.editItem()
                4 -> menuManager.displayMenu()
                5 -> {
                    println("Restaurant's profit: ${orderProcessor.getProfit()} rub.")
                }
                6 -> reviewManager.viewReviews()
                7 -> admin.viewAverageRatings()
                8 -> return
                else -> println("Incorrect choice.")
            }
        }
    }
}