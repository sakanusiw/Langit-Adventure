package com.example.langitadventure.RecyclerView

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.langitadventure.DetailActivity
import com.example.langitadventure.ItemsViewModelTenda
import com.example.langitadventure.MainActivity
import com.example.langitadventure.R
import com.example.langitadventure.TasActivity
import com.example.langitadventure.TendaActivity

// Adapter untuk barang-barang / item
class TendaAdapter(private val mList: List<ItemsViewModelTenda>) : RecyclerView.Adapter<TendaAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_tenda, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val itemsViewModel = mList[position]

        // Menggunakan Glide untuk memuat gambar dari URL
        Glide.with(holder.itemView.context)
            .load(itemsViewModel.imageUrl) // URL gambar dari Firebase
//            .placeholder(R.drawable.placeholder_image) // Gambar sementara jika perlu
//            .error(R.drawable.error_image) // Gambar jika terjadi error
            .into(holder.imageButton)

        // sets the text to the textview from our itemHolder class
        holder.textViewNama.text = itemsViewModel.textnama
        holder.textViewHarga.text = itemsViewModel.textharga

        // Menambahkan OnClickListener pada ImageButton
        holder.imageButton.setOnClickListener {
            // Buat intent untuk membuka aktivitas yang sesuai dengan kategori
            val intent = Intent(holder.itemView.context, DetailActivity::class.java).apply {
                putExtra("ITEM_ID", itemsViewModel.itemId)
                putExtra("ITEM_NAME", itemsViewModel.textnama)
                putExtra("ITEM_PRICE", itemsViewModel.textharga)
                putExtra("ITEM_IMAGE_URL", itemsViewModel.imageUrl)
                putExtra("ITEM_DESCRIPTION", itemsViewModel.description)
                putExtra("ITEM_AVAILABILITY", itemsViewModel.availability)
                putExtra("ITEM_BOOKING_COUNT", itemsViewModel.bookingCount)
            }
            holder.itemView.context.startActivity(intent)
        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageButton: ImageButton = itemView.findViewById(R.id.imageButtonCardTenda)
        val textViewNama: TextView = itemView.findViewById(R.id.textViewCardTendaNama)
        val textViewHarga: TextView = itemView.findViewById(R.id.textViewCardTendaHarga)
    }
}