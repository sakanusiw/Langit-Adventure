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

class PaymentActivity : AppCompatActivity() {

    private val PICK_FILE_REQUEST_CODE = 1
    private lateinit var textViewFileChosen: TextView

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

        val buttonClick1 = findViewById<Button>(R.id.buttonKirim)
        buttonClick1.setOnClickListener {
            val intent = Intent(this, PaymentSentActivity::class.java)
            startActivity(intent)
        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"  // Use specific MIME type if you want to restrict the file type
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val selectedFileUri: Uri? = data?.data
            selectedFileUri?.let {
                val filePath = getFilePathFromUri(it)
                Toast.makeText(this, "File selected: $filePath", Toast.LENGTH_LONG).show()
                textViewFileChosen.text = "File dipilih: $filePath"
                textViewFileChosen.visibility = TextView.VISIBLE
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

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PICK_FILE_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission granted, proceed with file picker
                } else {
                    Toast.makeText(this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }
}
