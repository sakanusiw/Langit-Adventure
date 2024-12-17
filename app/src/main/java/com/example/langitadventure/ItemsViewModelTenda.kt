package com.example.langitadventure
import com.google.firebase.firestore.PropertyName
//ItemsViewModel untuk semua Barang
data class ItemsViewModelTenda(
    @get:PropertyName("image_url") val imageUrl: String = "", // URL gambar dari Firestore
    @get:PropertyName("name") val textnama: String = "", // Nama barang
    @get:PropertyName("price_per_night") val textharga: String = "", // Harga per malam dalam bentuk string
    @get:PropertyName("availability") val availability: Boolean = false, // Ketersediaan
    @get:PropertyName("booking_count") val bookingCount: Int = 0, // Jumlah pemesanan
    @get:PropertyName("category") val category: String = "", // Kategori
    @get:PropertyName("description") val description: String = "", // Deskripsi
    @get:PropertyName("stock") val stock: String = "", // Stok
    var itemId: String = "" // ID dokumen untuk identifikasi unik
)
