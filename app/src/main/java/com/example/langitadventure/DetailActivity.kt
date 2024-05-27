package com.example.langitadventure

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Calendar

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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