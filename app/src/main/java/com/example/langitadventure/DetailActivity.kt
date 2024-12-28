package com.example.langitadventure

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import java.util.concurrent.TimeUnit

class DetailActivity : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var startDate: Long? = null
    private var endDate: Long? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance() // Inisialisasi FirebaseAuth
        firestore = FirebaseFirestore.getInstance()

        // Ambil ID barang dari Intent
        val itemId = intent.getStringExtra("ITEM_ID")

//        // Ambil data dari Firestore
//        if (itemId != null) {
//            getItemDetails(itemId)
//        }

        // Ambil data dari Firestore
        itemId?.let { getItemDetails(it) } // Use let to safely call the function if itemId is not null

        val textViewTanggalMulai: TextView = findViewById(R.id.textViewTanggalMulai)
        val textViewTanggalAkhir: TextView = findViewById(R.id.textViewTanggalAkhir)

        textViewTanggalMulai.setOnClickListener {
            // Buka dialog untuk memilih tanggal mulai
            showDatePickerDialog(textViewTanggalMulai, isStartDate = true)
        }

        textViewTanggalAkhir.setOnClickListener {
            // Buka dialog untuk memilih tanggal akhir
            showDatePickerDialog(textViewTanggalAkhir, isStartDate = false)
        }

        //Intent
        val buttonClick = findViewById<ImageButton>(R.id.imageButtonBack)
        buttonClick.setOnClickListener {
            finish()
        }

        val buttonClick1 = findViewById<ImageButton>(R.id.imageButtonBasket)
        buttonClick1.setOnClickListener {
            val intent = Intent(this, BasketActivity::class.java)
            startActivity(intent)
        }

        val buttonClick2 = findViewById<Button>(R.id.buttonDetailTambahKeranjang)
        buttonClick2.setOnClickListener{
            handleAddToCart()
        }
    }

    private fun handleAddToCart() {
        val user = auth.currentUser

        if (user == null) {
            // Jika pengguna belum login, arahkan ke halaman login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show()
        } else if (startDate == null || endDate == null) {
            // Jika tanggal sewa atau tanggal kembali belum dipilih
            Toast.makeText(this, "Silakan pilih tanggal sewa dan tanggal kembali", Toast.LENGTH_SHORT).show()
        } else {
            // Cek apakah tanggal sewa dan tanggal kembali berbeda
            if (startDate != endDate) {
                // Jika tanggal sewa dan tanggal kembali berbeda, tambahkan ke keranjang
                addToCart()
            } else {
                // Jika tanggal sewa dan tanggal kembali sama, beri pesan
                Toast.makeText(this, "Tanggal sewa dan kembali tidak boleh sama", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addToCart() {
        // Pastikan pengguna sudah login
        val userId = auth.currentUser?.uid
        val itemPricePerNight = intent.getIntExtra("ITEM_PRICE", 0) // Retrieve from intent with a default value
        val itemId = intent.getStringExtra("ITEM_ID") ?: "" // Ambil itemId dari intent

        if (userId != null && startDate != null && endDate != null && itemPricePerNight > 0) {
            // Periksa apakah item sudah ada di keranjang
            firestore.collection("users").document(userId).collection("cart")
                .whereEqualTo("itemId", itemId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        // Jika item sudah ada di keranjang, tampilkan pesan
                        Toast.makeText(this, "Item sudah ada di keranjang", Toast.LENGTH_SHORT).show()
                    } else {
                        // Jika item belum ada di keranjang, lanjutkan proses penambahan
                        // Hitung durasi sewa dalam malam
                        val duration = TimeUnit.MILLISECONDS.toDays(endDate!! - startDate!!).toInt()

                        // Ambil detail item yang ditampilkan di halaman detail
                        val itemName = findViewById<TextView>(R.id.textViewNamaBarang).text.toString()
                        val imageUrl = intent.getStringExtra("ITEM_IMAGE_URL") ?: ""

                        // Hitung total harga berdasarkan durasi
                        val totalPrice = duration * itemPricePerNight
                        Log.d("DetailActivity", "Total Price: $totalPrice")

                        // Data yang akan disimpan ke Firestore
                        val cartItem = hashMapOf(
                            "itemId" to itemId,
                            "itemName" to itemName,
                            "startDate" to startDate, // waktu dalam milidetik
                            "endDate" to endDate, // waktu dalam milidetik
                            "duration" to duration, // durasi dalam malam
                            "quantity" to 1,
                            "totalPrice" to totalPrice,
                            "imageUrl" to imageUrl,
                            "price_per_night" to itemPricePerNight // Menambahkan price_per_night untuk sinkronisasi dengan BasketFragment
                        )

                        // Akses ke sub-koleksi `cart` dalam dokumen pengguna
                        firestore.collection("users").document(userId).collection("cart")
                            .add(cartItem)
                            .addOnSuccessListener {documentReference ->
                                Toast.makeText(this, "Item berhasil ditambahkan ke keranjang", Toast.LENGTH_SHORT).show()
                                Log.d("DetailActivity", "Item added with ID: ${documentReference.id}")
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Gagal menambahkan item: ${e.message}", Toast.LENGTH_SHORT).show()
                                Log.e("DetailActivity", "Error adding item to cart", e)
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Gagal memeriksa keranjang: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Silakan pilih tanggal sewa dan login terlebih dahulu", Toast.LENGTH_SHORT).show()
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
                    val itemPricePerNight = document.getDouble("price_per_night")?.toInt() // Ensure the field name matches your Firestore data
                    val itemImage = document.getString("image_url")
                    val itemAvailable = document.getBoolean("availability") ?: false
                    val itemStock = document.getLong("stock")?.toInt() ?: 0

                    // Tampilkan data di UI
                    findViewById<TextView>(R.id.textViewDetailKategori).text = itemCategory ?: "Kategori tidak tersedia"
                    findViewById<TextView>(R.id.textViewNamaBarang).text = itemName ?: "Nama tidak tersedia"
                    findViewById<TextView>(R.id.textViewDeskripsiText).text = itemDescription ?: "Deskripsi tidak tersedia"
                    findViewById<TextView>(R.id.textViewDetailHarga).text = itemPricePerNight?.let { "Rp$it/Malam" } ?: "Harga tidak tersedia"

                    // Set itemPricePerNight in intent extras for use in addToCart
                    if (itemPricePerNight != null) {
                        intent.putExtra("ITEM_PRICE", itemPricePerNight)
                        Log.d("DetailActivity", "Retrieved item price per night: $itemPricePerNight")
                    } else {
                        Log.w("DetailActivity", "itemPricePerNight is null")
                    }

                    // Jika menggunakan image loading library seperti Glide
                    itemImage?.let {
                        Glide.with(this)
                            .load(it) // URL gambar dari Firestore
                            .into(findViewById<ImageView>(R.id.imageViewDetail))
                    }

                    // Kondisi untuk tombol keranjang berdasarkan ketersediaan
                    val buttonDetailTambahKeranjang: Button = findViewById(R.id.buttonDetailTambahKeranjang)
                    if (!itemAvailable || itemStock == 0) {
                        buttonDetailTambahKeranjang.text = "Stok Kosong"
                        buttonDetailTambahKeranjang.isEnabled = false
                    } else {
                        buttonDetailTambahKeranjang.text = "+ Keranjang"
                        buttonDetailTambahKeranjang.isEnabled = true
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

    private fun showDatePickerDialog(textView: TextView, isStartDate: Boolean) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                val selectedCalendar = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDay)
                }

                if (isStartDate) {
                    // Set the start date
                    startDate = selectedCalendar.timeInMillis
                    textView.text = "Tanggal sewa: $selectedDate"
                } else {
                    // Set the end date and calculate the difference
                    endDate = selectedCalendar.timeInMillis
                    val daysBetween = TimeUnit.MILLISECONDS.toDays(endDate!! - (startDate ?: 0))

                    if (startDate == null) {
                        Toast.makeText(this, "Pilih tanggal sewa terlebih dahulu", Toast.LENGTH_SHORT).show()
                    } else if (daysBetween in 1..7) {
                        textView.text = "Tanggal kembali: $selectedDate"
                    } else {
                        Toast.makeText(this, "Durasi penyewaan maksimal 7 malam", Toast.LENGTH_SHORT).show()
                        endDate = null
                    }
                }
            },
            year,
            month,
            day
        )

        // Set minimum date to today for start date or to the selected start date for end date
        datePickerDialog.datePicker.minDate = if (isStartDate) {
            calendar.timeInMillis
        } else {
            startDate ?: calendar.timeInMillis
        }

        datePickerDialog.show()
    }
}