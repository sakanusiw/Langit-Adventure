package com.example.langitadventure

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.langitadventure.RecyclerView.TendaAdapter

class TendaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tenda)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // getting the recyclerview by its id
        val recyclerview1 = findViewById<RecyclerView>(R.id.recyclerViewTenda)

        // this creates a horizontal layout Manager
        recyclerview1.layoutManager = GridLayoutManager(this@TendaActivity, 2)

        // ArrayList of class ItemsViewModelCategory
        val data1 = ArrayList<ItemsViewModelTenda>()

        // This loop will create 20 Views containing the image with the count of view
//        for (i in 1..20) {
//            data.add(ItemsViewModelCategory(R.drawable.kategori_tenda, "Item $i"))
//        }

        data1.add(ItemsViewModelTenda(R.drawable.tenda_dome_nsm4, "Tenda Dome NSM 4", "Rp35.000/Malam"))
        data1.add(ItemsViewModelTenda(R.drawable.tenda_dome_compass, "Tenda Dome Compass", "Rp30.000/Malam"))
        data1.add(ItemsViewModelTenda(R.drawable.tenda_dome_arei_eliot, "Tenda Dome Arei Eliot", "Rp35.000/Malam"))
        data1.add(ItemsViewModelTenda(R.drawable.tenda_co_trex, "Tenda Co-Trex", "Rp20.000/Malam"))
        data1.add(ItemsViewModelTenda(R.drawable.tenda_dome_borneo6, "Tenda Dome Borneo 6", "Rp60.000/Malam"))
        data1.add(ItemsViewModelTenda(R.drawable.tenda_go_java6, "Tenda Go Java 6", "Rp65.000/Malam"))
        data1.add(ItemsViewModelTenda(R.drawable.tenda_dome_borneo4, "Tenda Dome Borneo 4", "Rp45.000/Malam"))

        // This will pass the ArrayList to our Adapter
        val adapter1 = TendaAdapter(data1)

        // Setting the Adapter with the recyclerview
        recyclerview1.adapter = adapter1

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
}