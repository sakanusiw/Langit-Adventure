package com.example.langitadventure

import android.widget.ImageView

data class ItemsViewModelBasket(
    var itemName: String,
    var rentDate: String,
    var duration: String,
    var quantity: Int,
    var totalPrice: String,
    var imageResource: Int
) {
    fun incrementQuantity() {
        quantity++
    }

    fun decrementQuantity() {
        if (quantity > 1) {
            quantity--
        }
    }

}
