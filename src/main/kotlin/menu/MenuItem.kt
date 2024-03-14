package menu

class MenuItem(
    val name: String,
    var quantity: Int,
    var price: Double,
    var preparationTime: Int
) {
    override fun toString(): String {
        return "$name - $price rub., $quantity portions, ready in $preparationTime minutes."
    }
}