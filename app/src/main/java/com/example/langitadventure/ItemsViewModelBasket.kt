package com.example.langitadventure

import android.widget.ImageView

data class ItemsViewModelBasket(
    val itemName: String,
    val rentDate: String,
    val duration: String,
    val quantity: Int,
    val totalPrice: String,
    val imageResource: Int
)
