package menu

import java.math.BigDecimal
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.max

data class Order(
    val user: String,
    val menu: MenuManager,
    val items: MutableList<OrderedItem> = mutableListOf(),
    var totalPrice: Double = 0.0,
    var maxCookingTime: Int = 0,
    var status: OrderStatus = OrderStatus.NEW
) {

    fun addItem(name: String, quantity: Int, price: Double) {
        if (status == OrderStatus.NEW) {
            val menuItem = menu.getMenu().find { it.name == name }
            if (menuItem != null) {
                val orderedItem = OrderedItem(name, quantity, price)
                items.add(orderedItem)
                totalPrice += price * quantity
                maxCookingTime = max(maxCookingTime, menuItem.preparationTime)
                println("Added $quantity portion(s) of $name to the order.")
            } else {
                println("Error: Item not found in the menu.")
            }
        } else {
            println("Cannot add items to the order. Status: $status")
        }
    }

    fun displayOrder() {
        println("User $user's order:")
        items.forEachIndexed { index, orderedItem ->
            println("${index + 1}. ${orderedItem.name} - ${orderedItem.quantity} portion(s)")
        }
        println("Total price: $totalPrice rub.")
        println("Max cooking time: $maxCookingTime min")
        println("Order status: $status")
    }
}
