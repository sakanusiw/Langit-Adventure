package com.example.langitadventure

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.InputStream

class PaymentActivity : AppCompatActivity() {

    private val PICK_FILE_REQUEST_CODE = 1
    private lateinit var textViewFileChosen: TextView
    private var selectedFileUri: Uri? = null // Simpan URI file yang dipilih

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_payment)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        textViewFileChosen = findViewById(R.id.textViewFileChosen)

        val imageButtonPilihFile: ImageButton = findViewById(R.id.imageButtonPilihFile)
        imageButtonPilihFile.setOnClickListener {
            openFilePicker()
        }

        val buttonClick = findViewById<ImageButton>(R.id.imageButtonBack)
        buttonClick.setOnClickListener {
            val intent = Intent(this, BasketActivity::class.java)
            startActivity(intent)
        }

        val buttonClick1 = findViewById<Button>(R.id.buttonPaymentKirim)
        buttonClick1.setOnClickListener {
            if (selectedFileUri != null) {
                uploadFileToFirebase(selectedFileUri!!) // Panggil fungsi upload saat tombol diklik
            } else {
                Toast.makeText(this, "Silakan pilih file terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*" // Use specific MIME type if you want to restrict the file type
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            selectedFileUri = data?.data // Simpan URI file yang dipilih
            selectedFileUri?.let {
                val filePath = getFilePathFromUri(it)
                Toast.makeText(this, "File selected: $filePath", Toast.LENGTH_LONG).show()
                textViewFileChosen.text = "File dipilih: $filePath"
                textViewFileChosen.visibility = TextView.VISIBLE
            }
        }
    }

    private fun uploadFileToFirebase(uri: Uri) {
        val storageReference: StorageReference = FirebaseStorage.getInstance().reference.child("uploads/${System.currentTimeMillis()}_${getFileName(uri)}")

        // Dapatkan InputStream dari URI
        contentResolver.openInputStream(uri)?.let { inputStream ->
            storageReference.putStream(inputStream)
                .addOnSuccessListener {
                    Toast.makeText(this, "File berhasil diupload!", Toast.LENGTH_SHORT).show()

                    // Pindah ke PaymentSentActivity setelah berhasil upload
                    val intent = Intent(this, PaymentSentActivity::class.java)
                    startActivity(intent)
                    finish() // Optional: Tutup PaymentActivity jika tidak ingin kembali ke halaman ini
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Gagal mengupload file: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }


    private fun getFilePathFromUri(uri: Uri): String? {
        var filePath: String? = null
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    filePath = it.getString(index)
                }
            }
        }
        return filePath
    }

    private fun getFileName(uri: Uri): String? {
        var fileName: String? = null
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    fileName = it.getString(index)
                }
            }
        }
        return fileName
    }
}

