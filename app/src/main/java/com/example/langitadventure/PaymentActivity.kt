package com.example.langitadventure

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class PaymentActivity : AppCompatActivity() {

    private val PICK_FILE_REQUEST_CODE = 1
    private lateinit var textViewFileChosen: TextView
    private var selectedFileUri: Uri? = null

    // Registering for file picker result
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedFileUri = it
            val filePath = getFilePathFromUri(it)
            textViewFileChosen.text = "File dipilih: ${filePath ?: "Tidak diketahui"}"
            textViewFileChosen.visibility = TextView.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Nomor rekening TextView
        val nomorRekeningTextView = findViewById<TextView>(R.id.textViewNomorRekening)
        val imageButtonCopy = findViewById<ImageButton>(R.id.imageButtonCopy)

        // Fungsi untuk menyalin teks ke clipboard
        imageButtonCopy.setOnClickListener {
            val nomorRekening = nomorRekeningTextView.text.toString()
            if (nomorRekening.isNotEmpty()) {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Nomor Rekening", nomorRekening)
                clipboard.setPrimaryClip(clip)

                // Tampilkan pesan konfirmasi
                Toast.makeText(this, "Nomor rekening berhasil disalin", Toast.LENGTH_SHORT).show()
            } else {
                // Jika nomor rekening kosong
                Toast.makeText(this, "Nomor rekening kosong", Toast.LENGTH_SHORT).show()
            }
        }

        // Ambil total harga dari intent
        val totalPrice = intent.getIntExtra("TOTAL_PRICE", 0)

        // Tampilkan total harga di TextView
        val totalPriceTextView = findViewById<TextView>(R.id.textViewTotalTransfer)
        totalPriceTextView.text = String.format("Rp%,d", totalPrice)

        textViewFileChosen = findViewById(R.id.textViewFileChosen)

        findViewById<ImageButton>(R.id.imageButtonPilihFile).setOnClickListener {
            getContent.launch("*/*")  // Launch file picker
        }

        findViewById<ImageButton>(R.id.imageButtonBack).setOnClickListener {
            startActivity(Intent(this, BasketActivity::class.java))  // Go back to BasketActivity
        }

        findViewById<Button>(R.id.buttonPaymentKirim).setOnClickListener {
            selectedFileUri?.let {
                uploadProofAndProcessOrder(it)
            } ?: Toast.makeText(this, "Silakan pilih file terlebih dahulu", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openFilePicker() {
        Log.d("PaymentActivity", "Opening file picker...")
        getContent.launch("*/*") // Open the file picker
    }

    private fun uploadProofAndProcessOrder(uri: Uri) {
        Log.d("PaymentActivity", "Uploading proof and processing order...")

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Log.e("PaymentActivity", "User not logged in.")
            Toast.makeText(this, "Harap login terlebih dahulu.", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = currentUser.uid
        val storageRef: StorageReference = FirebaseStorage.getInstance().reference
            .child("transferProofs/$userId/${System.currentTimeMillis()}_${getFileName(uri)}")

        selectedFileUri?.let { fileUri ->
            contentResolver.openInputStream(fileUri)?.let { inputStream ->
                storageRef.putStream(inputStream)
                    .addOnSuccessListener {
                        Log.d("PaymentActivity", "File uploaded successfully.")
                        storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                            Log.d("PaymentActivity", "Download URL: $downloadUrl")
                            processOrder(downloadUrl.toString(),
                                onSuccess = {
                                    Toast.makeText(this, "Pesanan berhasil diproses!", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, PaymentSentActivity::class.java))
                                    finish()
                                },
                                onFailure = { exception ->
                                    Log.e("PaymentActivity", "Failed to process order: ${exception.message}")
                                    Toast.makeText(this, "Terjadi kesalahan: ${exception.message}", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("PaymentActivity", "Failed to upload file: ${e.message}")
                        Toast.makeText(this, "Gagal mengunggah file: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } ?: run {
                Log.e("PaymentActivity", "Failed to read selected file.")
                Toast.makeText(this, "Gagal membaca file yang dipilih.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun processOrder(proofUrl: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        Log.d("PaymentActivity", "Processing order with proof URL: $proofUrl")

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Log.e("PaymentActivity", "User not found.")
            onFailure(Exception("Pengguna tidak ditemukan."))
            return
        }

        val userId = currentUser.uid
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(userId).collection("cart")
            .get()
            .addOnSuccessListener { querySnapshot ->
                Log.d("PaymentActivity", "Cart items fetched successfully.")
                val batch = db.batch()
                val ordersRef = db.collection("users").document(userId).collection("orders")

                querySnapshot.documents.forEach { cartItem ->
                    val orderData = cartItem.data?.toMutableMap() ?: mutableMapOf()
                    orderData["proofUrl"] = proofUrl
                    orderData["timestamp"] = System.currentTimeMillis()
                    orderData["status"] = "Pembayaran Diperiksa"
                    ordersRef.document(cartItem.id).set(orderData)

                    // Update stock in the "items" collection using Firestore transaction
                    val itemId = cartItem.getString("itemId") ?: ""
                    val quantity = cartItem.getLong("quantity")?.toInt() ?: 0

                    if (itemId.isNotEmpty() && quantity > 0) {
                        val itemRef = db.collection("items").document(itemId)

                        db.runTransaction { transaction ->
                            val snapshot = transaction.get(itemRef)
                            val currentStock = snapshot.getLong("stock")?.toInt() ?: 0
                            val newStock = currentStock - quantity

                            if (newStock >= 0) {
                                transaction.update(itemRef, "stock", newStock)
                            } else {
                                Log.w("PaymentActivity", "Stock is insufficient for item: $itemId")
                            }
                        }.addOnSuccessListener {
                            Log.d("PaymentActivity", "Transaction completed successfully.")
                        }.addOnFailureListener { e ->
                            Log.e("PaymentActivity", "Transaction failed", e)
                        }
                    }

                    // Delete the cart item after order is processed
                    batch.delete(cartItem.reference)
                }

                batch.commit()
                    .addOnSuccessListener {
                        Log.d("PaymentActivity", "Order processed successfully.")
                        onSuccess()
                    }
                    .addOnFailureListener { exception ->
                        Log.e("PaymentActivity", "Failed to process order: ${exception.message}", exception)
                        onFailure(exception)
                    }
            }
            .addOnFailureListener { exception ->
                Log.e("PaymentActivity", "Failed to fetch cart items: ${exception.message}", exception)
                onFailure(exception)
            }
    }

    private fun getFilePathFromUri(uri: Uri): String? = getFileName(uri)

    private fun getFileName(uri: Uri): String? {
        return try {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index != -1) it.getString(index) else null
                } else null
            }
        } catch (e: Exception) {
            Log.e("PaymentActivity", "Error getting file name", e)
            null
        }
    }
}
