package menu

import java.io.File

class ReviewManager(private val reviewFile: String) {
    data class Review(val itemName: String, val rating: Int, val comment: String)

    private val reviews: MutableList<Review> = mutableListOf()

    init {
        loadReviewsFromFile()
    }

    fun addReview(itemName: String, rating: Int, comment: String) {
        reviews.add(Review(itemName, rating, comment))
        saveReviewsToFile()
    }

    fun getAverageRatingForItem(itemName: String): Double {
        val ratings = reviews.filter { it.itemName == itemName }.map { it.rating }
        return if (ratings.isNotEmpty()) {
            ratings.average()
        } else {
            0.0
        }
    }

    fun viewReviews() {
        println("All reviews:")
        reviews.forEachIndexed { index, review ->
            println("${index + 1}. Item: ${review.itemName}, Rating: ${review.rating}, Comment: ${review.comment}")
        }
    }

    private fun loadReviewsFromFile() {
        if (File(reviewFile).exists()) {
            val lines = File(reviewFile).readLines()
            lines.forEach { line ->
                val parts = line.split(";")
                if (parts.size == 3) {
                    val itemName = parts[0]
                    val rating = parts[1].toIntOrNull()
                    val comment = parts[2]
                    if (rating != null) {
                        reviews.add(Review(itemName, rating, comment))
                    }
                }
            }
        }
    }

    private fun saveReviewsToFile() {
        val content = reviews.joinToString("\n") { "${it.itemName};${it.rating};${it.comment}" }
        File(reviewFile).writeText(content)
    }
}

