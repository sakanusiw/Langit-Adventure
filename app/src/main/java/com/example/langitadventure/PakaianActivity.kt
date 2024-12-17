package com.example.langitadventure

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.langitadventure.RecyclerView.TendaAdapter
import com.google.firebase.firestore.FirebaseFirestore

class PakaianActivity : AppCompatActivity() {

    private lateinit var recyclerView1: RecyclerView
    private lateinit var tendaAdapter: TendaAdapter
    private val data1 = ArrayList<ItemsViewModelTenda>() // List untuk data dari Firebase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pakaian)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Menginisialisasi RecyclerView
        recyclerView1 = findViewById(R.id.recyclerViewPakaian)
        recyclerView1.layoutManager = GridLayoutManager(this@PakaianActivity, 2)

        // Mengatur Adapter awal dengan list kosong
        tendaAdapter = TendaAdapter(data1)
        recyclerView1.adapter = tendaAdapter

        // Ambil data dari Firestore
        fetchItemsFromFirestore()

        //Intent
        val buttonClick = findViewById<ImageButton>(R.id.imageButtonBack)
        buttonClick.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        val buttonClick1 = findViewById<ImageButton>(R.id.imageButtonBasket)
        buttonClick1.setOnClickListener {
            val intent = Intent(this, BasketActivity::class.java)
            startActivity(intent)
        }
    }

    // Fungsi untuk mengambil data dari Firebase Firestore
    private fun fetchItemsFromFirestore() {
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("items")
            .whereEqualTo("category", "Pakaian") // Filter kategori jika diperlukan
            .get()
            .addOnSuccessListener { documents ->
                data1.clear() // Bersihkan list sebelumnya
                for (document in documents) {
                    val namaBarang = document.getString("name") ?: ""
                    val hargaPerMalam = document.getDouble("price_per_night")?.toInt() ?: 0
                    val gambarBarang = document.getString("image_url") ?: ""
                    val availability = document.getBoolean("availability") ?: false
                    val bookingCount = document.getLong("booking_count")?.toInt() ?: 0
                    val category = document.getString("category") ?: ""
                    val description = document.getString("description") ?: ""
                    val stock = document.getLong("stock")?.toInt() ?: 0

                    // Log nilai yang diambil untuk debugging
                    Log.d("PakaianActivity", "namaBarang: $namaBarang, hargaPerMalam: $hargaPerMalam, gambarBarang: $gambarBarang, availability: $availability, bookingCount: $bookingCount, category: $category, description: $description")

                    // Tambahkan data ke dalam list
                    val item = ItemsViewModelTenda(
                        imageUrl = gambarBarang, // URL gambar dari Firestore
                        textnama = namaBarang,
                        textharga = "Rp$hargaPerMalam/Malam", // Format harga
                        availability = availability,
                        bookingCount = bookingCount,
                        category = category,
                        description = description,
                        stock = "Stok: $stock",
                        itemId = document.id // ID dokumen unik
                    )
                    data1.add(item)
                }
                // Beri tahu adapter bahwa data telah berubah
                tendaAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("PakaianActivity", "Error fetching data: ", exception)
            }
    }
}