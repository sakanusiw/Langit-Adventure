package com.example.langitadventure

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class DetailActivity : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        firestore = FirebaseFirestore.getInstance()

        // Ambil ID barang dari Intent
        val itemId = intent.getStringExtra("ITEM_ID")

//        // Ambil data dari Firestore
//        if (itemId != null) {
//            getItemDetails(itemId)
//        }

        // Ambil data dari Firestore
        itemId?.let { getItemDetails(it) } // Use let to safely call the function if itemId is not null

        val textViewDetailTanggal: TextView = findViewById(R.id.textViewDetailTanggal)

        textViewDetailTanggal.setOnClickListener {
            showDatePickerDialog(textViewDetailTanggal)
        }

        //Intent
        val buttonClick = findViewById<ImageButton>(R.id.imageButtonBack)
        buttonClick.setOnClickListener {
            val intent = Intent(this, TendaActivity::class.java)
            startActivity(intent)
        }

        val buttonClick1 = findViewById<ImageButton>(R.id.imageButtonBasket)
        buttonClick1.setOnClickListener {
            val intent = Intent(this, BasketActivity::class.java)
            startActivity(intent)
        }

        val buttonClick2 = findViewById<Button>(R.id.buttonDetailKeranjang)
        buttonClick2.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getItemDetails(itemId: String) {
        firestore.collection("items") // Ganti dengan nama koleksi Anda
            .document(itemId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val itemName = document.getString("name") // Change to the correct field names
                    val itemDescription = document.getString("description")
                    val itemCategory = document.getString("category")
                    val itemPrice = document.getDouble("price_per_night")?.toInt() // Ensure the field name matches your Firestore data
                    val itemImage = document.getString("image_url")
                    val itemAvailable = document.getBoolean("availability")

                    // Tampilkan data di UI
                    findViewById<TextView>(R.id.textViewDetailKategori).text = itemCategory ?: "Kategori tidak tersedia"
                    findViewById<TextView>(R.id.textViewNamaBarang).text = itemName ?: "Nama tidak tersedia"
                    findViewById<TextView>(R.id.textViewDeskripsiText).text = itemDescription ?: "Deskripsi tidak tersedia"
                    findViewById<TextView>(R.id.textViewDetailHarga).text = itemPrice?.let { "Rp$it/Malam" } ?: "Harga tidak tersedia"

                    // Jika menggunakan image loading library seperti Glide
                    itemImage?.let {
                        Glide.with(this)
                            .load(it) // URL gambar dari Firestore
                            .into(findViewById<ImageView>(R.id.imageViewDetail))
                    }
                } else {
                    Log.d("DetailActivity", "Document does not exist")
                }
            }
            .addOnFailureListener { exception ->
                // Tangani error
                Log.w("DetailActivity", "Error getting documents: ", exception)
            }
    }

    private fun showDatePickerDialog(textView: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                textView.text = selectedDate
            },
            year,
            month,
            day
        )

        // Set the minimum date to today's date
        datePickerDialog.datePicker.minDate = calendar.timeInMillis

        datePickerDialog.show()
    }
}