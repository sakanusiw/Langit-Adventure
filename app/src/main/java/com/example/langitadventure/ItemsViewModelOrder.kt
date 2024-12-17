package com.example.langitadventure

data class ItemsViewModelOrder(
    val imageUrl: String = "",
    val namaBarang: String = "",
    val hargaPerMalam: Int = 0,
    val durasi: Int = 0,
    val jumlahBarang: Int = 0,
    val totalBiaya: Int = 0,
    var status: String = "",
    var timestamp: Long? = null,
    var startDate: Long? = null,
    var endDate: Long? = null
)
