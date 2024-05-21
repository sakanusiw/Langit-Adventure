//package com.example.langitadventure.RecyclerView
//
//import android.content.Intent
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageButton
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.example.langitadventure.DetailActivity
//import com.example.langitadventure.ItemsViewModelBasket
//import com.example.langitadventure.ItemsViewModelTenda
//import com.example.langitadventure.MainActivity
//import com.example.langitadventure.R
//import com.example.langitadventure.TasActivity
//import com.example.langitadventure.TendaActivity
//
//class BasketAdapter(private val mList: List<ItemsViewModelBasket>) : RecyclerView.Adapter<BasketAdapter.ViewHolder>() {
//
//    // create new views
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        // inflates the card_view_design view
//        // that is used to hold list item
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.card_view_basket, parent, false)
//
//        return ViewHolder(view)
//    }
//
//    // binds the list items to a view
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//
//        val ItemsViewModel = mList[position]
//
//        // sets the image to the imageview from our itemHolder class
//        holder.imageButton.setImageResource(ItemsViewModel.image)
//
//        // sets the text to the textview from our itemHolder class
//        holder.textViewNama.text = ItemsViewModel.textnama
//        holder.textViewHarga.text = ItemsViewModel.textharga
//
//        // Menambahkan OnClickListener pada ImageButton
//        holder.imageButton.setOnClickListener {
//            // Buat intent untuk membuka aktivitas yang sesuai dengan kategori
//            val intent = when (ItemsViewModel.textnama) {
//                "Tenda Dome NSM 4" -> Intent(holder.itemView.context, DetailActivity::class.java)
////                "Tas" -> Intent(holder.itemView.context, TasActivity::class.java)
////                "Lampu" -> Intent(holder.itemView.context, RegisterActivity::class.java)
////                "Pakaian" -> Intent(holder.itemView.context, PakaianActivity::class.java)
////                "Perlengkapan" -> Intent(holder.itemView.context, PerlengkapanActivity::class.java)
//                else -> Intent(holder.itemView.context, MainActivity::class.java)
//            }
//            // Mulai aktivitas baru
//            holder.itemView.context.startActivity(intent)
//        }
//
//    }
//
//    // return the number of the items in the list
//    override fun getItemCount(): Int {
//        return mList.size
//    }
//
//    // Holds the views for adding it to image and text
//    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
//        val imageButton: ImageButton = itemView.findViewById(R.id.imageButtonCardTenda)
//        val textViewNama: TextView = itemView.findViewById(R.id.textViewCardTendaNama)
//        val textViewHarga: TextView = itemView.findViewById(R.id.textViewCardTendaHarga)
//    }
//}