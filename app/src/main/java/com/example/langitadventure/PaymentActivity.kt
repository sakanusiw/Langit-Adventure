package com.example.langitadventure

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        textViewFileChosen = findViewById(R.id.textViewFileChosen)

        findViewById<ImageButton>(R.id.imageButtonPilihFile).setOnClickListener {
            openFilePicker()
        }

        findViewById<ImageButton>(R.id.imageButtonBack).setOnClickListener {
            startActivity(Intent(this, BasketActivity::class.java))
        }

        findViewById<Button>(R.id.buttonPaymentKirim).setOnClickListener {
            selectedFileUri?.let {
                uploadProofAndProcessOrder(it)
            } ?: Toast.makeText(this, "Silakan pilih file terlebih dahulu", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            selectedFileUri = data?.data
            selectedFileUri?.let {
                val filePath = getFilePathFromUri(it)
                textViewFileChosen.text = "File dipilih: ${filePath ?: "Tidak diketahui"}"
                textViewFileChosen.visibility = TextView.VISIBLE
            }
        }
    }

    private fun uploadProofAndProcessOrder(uri: Uri) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
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
                        storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                            processOrder(downloadUrl.toString(),
                                onSuccess = {
                                    Toast.makeText(this, "Pesanan berhasil diproses!", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, PaymentSentActivity::class.java))
                                    finish()
                                },
                                onFailure = { exception ->
                                    Toast.makeText(this, "Terjadi kesalahan: ${exception.message}", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Gagal mengunggah file: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } ?: run {
                Toast.makeText(this, "Gagal membaca file yang dipilih.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun processOrder(proofUrl: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            onFailure(Exception("Pengguna tidak ditemukan."))
            return
        }

        val userId = currentUser.uid
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(userId).collection("cart")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val batch = db.batch()
                val ordersRef = db.collection("users").document(userId).collection("orders")

                querySnapshot.documents.forEach { cartItem ->
                    val orderData = cartItem.data?.toMutableMap() ?: mutableMapOf()
                    Log.d("PaymentActivity", "Cart Item Data: $orderData")
                    orderData["proofUrl"] = proofUrl
                    orderData["timestamp"] = System.currentTimeMillis()
                    orderData["status"] = "Pembayaran Diperiksa"
                    ordersRef.document(cartItem.id).set(orderData)
                    batch.delete(cartItem.reference)
                }

                batch.commit()
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { exception -> onFailure(exception) }
            }
            .addOnFailureListener { exception -> onFailure(exception) }
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
            null
        }
    }
}
