package menu

import java.io.File

class MenuManager(private val menuFile: String) {
    private val menu: MutableList<MenuItem> = mutableListOf()

    init {
        loadMenuFromFile()
    }

    fun displayMenu() {
        println("Menu:")
        menu.forEachIndexed { index, item ->
            println("${index + 1}. ${item.name} - ${item.price} rub., ${item.quantity} portions, ready in ${item.preparationTime} minutes.")
        }
    }

    fun addItem(name: String, quantity: Int, price: Double, preparationTime: Int) {
        menu.add(MenuItem(name, quantity, price, preparationTime))
        saveMenuToFile()
        println("Item \"$name\" added to the menu.")
    }

    fun removeItem(name: String): Boolean {
        val item = menu.find { it.name == name }
        return if (item != null) {
            menu.remove(item)
            saveMenuToFile()
            true
        } else {
            false
        }
    }

    fun updateItem(name: String, quantity: Int, price: Double, preparationTime: Int): Boolean {
        val item = menu.find { it.name == name }
        return if (item != null) {
            item.quantity = quantity
            item.price = price
            item.preparationTime = preparationTime
            saveMenuToFile()
            true
        } else {
            false
        }
    }

    fun getMenu(): List<MenuItem> {
        return menu.toList()
    }

    private fun loadMenuFromFile() {
        if (File(menuFile).exists()) {
            val lines = File(menuFile).readLines()
            menu.addAll(lines.mapNotNull { parseMenuItem(it) })
        }
    }

    fun saveMenuToFile() {
        val content = menu.joinToString("\n") { formatMenuItem(it) }
        File(menuFile).writeText(content)
    }

    private fun parseMenuItem(line: String): MenuItem? {
        val parts = line.split(";")
        return if (parts.size == 4) {
            MenuItem(parts[0], parts[1].toInt(), parts[2].toDouble(), parts[3].toInt())
        } else {
            null
        }
    }

    private fun formatMenuItem(item: MenuItem): String {
        return "${item.name};${item.quantity};${item.price};${item.preparationTime}"
    }

}