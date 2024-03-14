package users

import menu.MenuManager
import menu.ReviewManager

class Admin(private val menuManager: MenuManager, private val reviewManager: ReviewManager) {
    fun addItem() {
        println("Enter item name:")
        val name = readLine().orEmpty()

        println("Enter quantity:")
        val quantity = readLine()?.toIntOrNull() ?: 0
        if (quantity <= 0) {
            println("Error: Incorrect quantity.")
            return
        }

        println("Enter item price:")
        val price = readLine()?.toDoubleOrNull() ?: 0.0
        if (price <= 0) {
            println("Error: Incorrect item price.")
            return
        }

        println("Enter cooking time in minutes:")
        val cookingTime = readLine()?.toIntOrNull() ?: 0
        if (cookingTime <= 0) {
            println("Error: Incorrect cooking time.")
            return
        }

        menuManager.addItem(name, quantity, price, cookingTime)
        println("Item successfully added to the menu.")
    }

    fun removeItem() {
        menuManager.displayMenu()
        println("Enter item name to remove:")
        val itemName = readLine().orEmpty()
        if (menuManager.removeItem(itemName)) {
            println("Item \"$itemName\" removed from the menu.")
        } else {
            println("Item \"$itemName\" not found.")
        }
    }

    fun editItem() {
        menuManager.displayMenu()
        println("Enter item number to edit:")
        val itemNumber = readLine()?.toIntOrNull() ?: 0

        println("Choose parameter to edit:")
        println("1. Quantity")
        println("2. Price")
        println("3. Cooking Time")

        when (readLine()?.toIntOrNull()) {
            1 -> editItemProperty(itemNumber, "quantity")
            2 -> editItemProperty(itemNumber, "price")
            3 -> editItemProperty(itemNumber, "preparationTime")
            else -> println("Incorrect choice.")
        }
    }

    fun viewAverageRatings() {
        println("Average Ratings for Items:")
        menuManager.getMenu().forEach { menuItem ->
            val averageRating = reviewManager.getAverageRatingForItem(menuItem.name)
            println("${menuItem.name}: ${"%.2f".format(averageRating)}")
        }
    }

    private fun editItemProperty(itemNumber: Int, property: String) {
        val menuItem = menuManager.getMenu().getOrNull(itemNumber - 1)
        if (menuItem != null) {
            println("Enter new value:")
            val newValue = readLine()?.let {
                when (property) {
                    "quantity", "preparationTime" -> it.toIntOrNull() ?: 0
                    "price" -> it.toDoubleOrNull() ?: 0.0
                    else -> 0
                }
            } ?: return

            when (property) {
                "quantity" -> menuItem.quantity = newValue as Int
                "price" -> menuItem.price = newValue as Double
                "preparationTime" -> menuItem.preparationTime = newValue as Int
            }
            menuManager.saveMenuToFile()
            println("Item \"$property\" updated.")
        } else {
            println("Item not found.")
        }
    }
}
