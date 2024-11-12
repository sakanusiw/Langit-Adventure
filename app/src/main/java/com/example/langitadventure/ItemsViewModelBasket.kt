package com.example.langitadventure

data class ItemsViewModelBasket(
    val itemName: String = "",
    val formattedStartDate: String = "",
    val formattedEndDate: String = "",
    val duration: Int = 0,
    var quantity: Int = 1,
    var totalPrice: Int = 0,
    val imageUrl: String = "",
    val itemId: String = "",
    var pricePerNight: Int = 0
)
 {
    fun incrementQuantity(maxQuantity: Int = Int.MAX_VALUE) {
        if (quantity < maxQuantity) {
            quantity++
        }
    }

    fun decrementQuantity() {
        if (quantity > 1) {
            quantity--
        }
    }

    // Calculates total price based on quantity and price per night
    fun calculateTotalPrice(pricePerNight: Int): Int {
        return pricePerNight * duration * quantity
    }

}
