package com.example.langitadventure

data class ItemsViewModelOrder(
    val imageResource: Int,  // Resource ID for the image
    val nama: String,        // Nama item
    val hargaPerMalam: String,  // Harga per malam
    val durasi: String,         // Durasi sewa
    val status: String,         // Status item
    val jumlah: String,         // Jumlah item
    val totalBiaya: String      // Total biaya
)
