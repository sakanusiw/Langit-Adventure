package com.example.langitadventure.RecyclerView

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.langitadventure.AlatMasakActivity
import com.example.langitadventure.BasketFragment
import com.example.langitadventure.ItemsViewModelCategory
import com.example.langitadventure.LampuActivity
import com.example.langitadventure.MainActivity
import com.example.langitadventure.PakaianActivity
import com.example.langitadventure.PerlengkapanActivity
import com.example.langitadventure.ProfileFragment
import com.example.langitadventure.R
import com.example.langitadventure.RegisterActivity
import com.example.langitadventure.SepatuActivity
import com.example.langitadventure.TasActivity
import com.example.langitadventure.TendaActivity

class CategoryAdapter(private val mList: List<ItemsViewModelCategory>) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_category, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]

        // sets the image to the imageview from our itemHolder class
        holder.imageButton.setImageResource(ItemsViewModel.image)

        // sets the text to the textview from our itemHolder class
        holder.textView.text = ItemsViewModel.text

        // Menambahkan OnClickListener pada ImageButton
        holder.imageButton.setOnClickListener {
            // Buat intent untuk membuka aktivitas yang sesuai dengan kategori
            val intent = when (ItemsViewModel.text) {
                "Tenda" -> Intent(holder.itemView.context, TendaActivity::class.java)
                "Tas" -> Intent(holder.itemView.context, TasActivity::class.java)
                "Lampu" -> Intent(holder.itemView.context, LampuActivity::class.java)
                "Pakaian" -> Intent(holder.itemView.context, PakaianActivity::class.java)
                "Sepatu" -> Intent(holder.itemView.context, SepatuActivity::class.java)
                "Alat Masak" -> Intent(holder.itemView.context, AlatMasakActivity::class.java)
                "Perlengkapan" -> Intent(holder.itemView.context, PerlengkapanActivity::class.java)
                else -> Intent(holder.itemView.context, MainActivity::class.java)
            }
            // Mulai aktivitas baru
            holder.itemView.context.startActivity(intent)
        }

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageButton: ImageButton = itemView.findViewById(R.id.imageButtonCardKategori)
        val textView: TextView = itemView.findViewById(R.id.textViewCardKategori)
    }
}
