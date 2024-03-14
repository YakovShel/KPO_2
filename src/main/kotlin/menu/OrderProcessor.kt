package menu

import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.max

class OrderProcessor(
    private val orderFile: String,
    private val menuManager: MenuManager
) {
    private val orders: MutableList<Order> = loadOrders()

    fun createOrder(username: String) {
        val order = Order(username, menuManager, mutableListOf(), 0.0, 0, OrderStatus.NEW)

        do {
            menuManager.displayMenu()

            println("Choose the number of the item to order:")
            val itemNumber = readLine()?.toIntOrNull()

            if (itemNumber != null && itemNumber in 1..menuManager.getMenu().size) {
                val item = menuManager.getMenu()[itemNumber - 1]
                println("Enter the quantity (maximum ${item.quantity}):")
                val quantity = readLine()?.toIntOrNull()

                if (quantity != null && quantity > 0 && quantity <= item.quantity) {
                    val orderedItem = OrderedItem(item.name, quantity, item.price)
                    order.items.add(orderedItem)
                    item.quantity -= quantity
                    order.totalPrice += orderedItem.price * quantity
                    order.maxCookingTime = max(order.maxCookingTime, item.preparationTime)
                    println("Item added to the order.")
                } else {
                    println("Error: Invalid quantity.")
                }
            } else {
                println("Error: Invalid Item number.")
            }

            println("Your current order:")
            order.items.forEachIndexed { index, item ->
                println("${index + 1}. ${item.name} - ${item.quantity} portions")
            }

            println("Do you want to add another Item to your order? (yes/no):")
        } while (readLine()?.lowercase() == "yes")

        if (order.totalPrice > 0.0) {
            orders.add(order)

            processOrderAsync(order)

            println("Thank you, your order №${orders.indexOf(order) + 1} has been accepted! " +
                    "Total price: ${order.totalPrice} rub. " +
                    "Cooking time: ${order.maxCookingTime} minutes.")
        } else {
            println("Error: The total amount of the order is 0. Please add items to your order.")
        }
    }

    fun cancelOrder(username: String) {
        val userOrders = orders.filter { it.user == username && it.status != OrderStatus.DONE && it.status != OrderStatus.CANCELLED && it.status != OrderStatus.PAID }
        if (userOrders.isEmpty()) {
            println("You don't have active orders.")
            return
        }

        println("Choose the order number to cancel:")
        userOrders.forEachIndexed { index, order ->
            println("${index + 1}. Order №${orders.indexOf(order) + 1} - ${order.status}")
        }

        val selectedOrderIndex = readLine()?.toIntOrNull()?.minus(1)
        if (selectedOrderIndex != null && selectedOrderIndex in 0 until userOrders.size) {
            val selectedOrder = userOrders[selectedOrderIndex]
            selectedOrder.status = OrderStatus.CANCELLED
            for (orderedItem in selectedOrder.items) {
                val itemFromMenu = menuManager.getMenu().find { it.name == orderedItem.name }
                var quantityInMenu = itemFromMenu?.quantity ?: 0
                quantityInMenu += orderedItem.quantity
                itemFromMenu?.quantity = quantityInMenu
            }
            println("Your order has been successfully cancelled!")
        } else {
            println("Error: Invalid order number.")
        }
    }

    var revenue: Double = 0.0



    fun saveRevenueToFile(profit: Double) {
        val file = File("revenue.txt")
        file.writeText(revenue.toString())
    }

    fun payOrder(username: String) {
        val userOrders = orders.filter { it.user == username && it.status == OrderStatus.DONE }
        if (userOrders.isEmpty()) {
            println("You don't have unpaid orders.")
            return
        }

        println("Choose the order number to pay:")
        userOrders.forEachIndexed { index, order ->
            println("${index + 1}. Order №${orders.indexOf(order) + 1} - ${order.status} - ${order.totalPrice}rub.")
        }

        val selectedOrderIndex = readLine()?.toIntOrNull()?.minus(1)
        if (selectedOrderIndex != null && selectedOrderIndex in 0 until userOrders.size) {
            val selectedOrder = userOrders[selectedOrderIndex]
            selectedOrder.status = OrderStatus.PAID
            println("Your order has been successfully paid!")
            saveRevenueToFile(getProfit())
        } else {
            println("Error: Invalid order number.")
        }
    }

    fun getProfit(): Double {
        return orders.filter { it.status == OrderStatus.PAID }.sumByDouble { it.totalPrice }
    }

    fun addItemToOrder(authenticatedLogin: String) {
        val order = getOrder(authenticatedLogin)
        if (order != null) {
            println("Enter the name of the item to add:")
            val itemName = readLine().orEmpty()
            println("Enter quantity:")
            val quantity = readLine()?.toIntOrNull() ?: 0
            println("Enter price per item:")
            val price = readLine()?.toDoubleOrNull() ?: 0.0
            order.addItem(itemName, quantity, price)
        } else {
            println("Error: Order not found.")
        }
    }

    fun getOrder(username: String): Order? {
        return orders.find { it.user == username && it.status == OrderStatus.IN_PROGRESS }
    }

    private fun processOrderAsync(order: Order) {
        val executorService = Executors.newSingleThreadExecutor()
        executorService.submit {
            println("Processing order №${orders.indexOf(order) + 1} started.")
            order.status = OrderStatus.IN_PROGRESS
            TimeUnit.SECONDS.sleep(order.maxCookingTime.toLong())
            if (order.status != OrderStatus.CANCELLED) {
                println("Order №${orders.indexOf(order) + 1} is ready!")
                order.status = OrderStatus.DONE
                saveOrders()
            }
        }
        executorService.shutdown()
    }

    private fun loadOrders(): MutableList<Order> {
        if (File(orderFile).exists()) {
            val lines = File(orderFile).readLines()
            return lines.mapNotNull { parseOrder(it) }.toMutableList()
        }
        return mutableListOf()
    }

    private fun parseOrder(line: String): Order? {
        val parts = line.split(";")
        if (parts.size >= 4) {
            val username = parts[0]
            val items = parts.subList(1, parts.size - 2).mapNotNull { parseOrderedItem(it) }
            val totalPrice = parts[parts.size - 2].toDoubleOrNull()
            val status = OrderStatus.valueOf(parts[parts.size - 1])
            if (username != null && items.isNotEmpty() && totalPrice != null) {
                return Order(username, menuManager, items.toMutableList(), totalPrice, 0, status)
            }
        }
        return null
    }

    private fun saveOrders() {
        val content = orders.joinToString("\n") { formatOrder(it) }
        File(orderFile).writeText(content)
    }

    private fun formatOrder(order: Order): String {
        val username = order.user
        val items = order.items.joinToString(";") { formatOrderedItem(it) }
        val totalPrice = order.totalPrice
        val status = order.status
        return "$username;$items;$totalPrice;$status"
    }

    private fun parseOrderedItem(line: String): OrderedItem? {
        val parts = line.split(",")
        if (parts.size == 3) {
            val name = parts[0]
            val quantity = parts[1].toIntOrNull()
            val price = parts[2].toDoubleOrNull()
            if (quantity != null && price != null) {
                return OrderedItem(name, quantity, price)
            }
        }
        return null
    }

    private fun formatOrderedItem(orderedItem: OrderedItem): String {
        val name = orderedItem.name
        val quantity = orderedItem.quantity
        val price = orderedItem.price
        return "$name,$quantity,$price"
    }
}