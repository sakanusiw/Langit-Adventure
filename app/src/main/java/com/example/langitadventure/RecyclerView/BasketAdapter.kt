package com.example.langitadventure.RecyclerView

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.langitadventure.ItemsViewModelBasket
import com.example.langitadventure.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BasketAdapter(
    private val mList: MutableList<ItemsViewModelBasket>,
    private val onItemRemoved: (Int) -> Unit,
    private val onQuantityChanged: (Int) -> Unit
) : RecyclerView.Adapter<BasketAdapter.ViewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_basket, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mList[position]

        // Set total price
        holder.totalPrice.text = "Rp${item.totalPrice}"

        // Set other fields
        holder.itemName.text = item.itemName
        holder.startDate.text = item.formattedStartDate
        holder.endDate.text = item.formattedEndDate
        holder.duration.text = item.duration.toString()
        holder.quantity.text = item.quantity.toString()

        // Load image using Glide
        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .into(holder.itemImage)

        // Delete button
        holder.deleteButton.setOnClickListener {
            val cartDocumentId = item.cartDocumentId
            if (cartDocumentId.isNotEmpty()) {
                db.collection("users").document(auth.currentUser!!.uid).collection("cart")
                    .document(cartDocumentId)
                    .delete()
                    .addOnSuccessListener {
                        onItemRemoved(item.totalPrice)
                        mList.removeAt(position)
                        notifyItemRemoved(position)
                    }
                    .addOnFailureListener { e ->
                        Log.e("BasketAdapter", "Gagal menghapus item: $cartDocumentId", e)
                    }
            } else {
                Log.e("BasketAdapter", "cartDocumentId kosong untuk item: ${item.itemId}")
            }
        }

        // Plus button
        holder.plusButton.setOnClickListener {
            if (item.quantity < 10) { // Batasi maksimal 10
                item.quantity++
                updateItemQuantity(item, holder)
            }
        }

        // Minus button
        holder.minusButton.setOnClickListener {
            if (item.quantity > 1) { // Batasi minimal 1
                item.quantity--
                updateItemQuantity(item, holder)
            }
        }
    }

    private fun updateItemQuantity(item: ItemsViewModelBasket, holder: ViewHolder) {
        // Perbarui jumlah kuantitas dan total harga item
        item.totalPrice = item.pricePerNight * item.quantity * item.duration
        holder.quantity.text = item.quantity.toString()
        holder.totalPrice.text = "Rp${item.totalPrice}"

        // Simpan pembaruan kuantitas dan total harga ke Firestore
        db.collection("users").document(auth.currentUser!!.uid).collection("cart")
            .document(item.cartDocumentId)
            .update("quantity", item.quantity, "totalPrice", item.totalPrice)
            .addOnSuccessListener {
                onQuantityChanged(item.totalPrice) // Perbarui total harga secara keseluruhan
            }
            .addOnFailureListener { e ->
                Log.e("BasketAdapter", "Gagal memperbarui kuantitas item: ${item.cartDocumentId}", e)
            }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val itemImage: ImageView = itemView.findViewById(R.id.imageViewCardKeranjang)
        val itemName: TextView = itemView.findViewById(R.id.textViewCardKeranjangNama)
        val startDate: TextView = itemView.findViewById(R.id.textViewCardKeranjangTanggalSewa)
        val endDate: TextView = itemView.findViewById(R.id.textViewCardKeranjangTanggalKembali)
        val duration: TextView = itemView.findViewById(R.id.textViewCardKeranjangDurasi)
        val quantity: TextView = itemView.findViewById(R.id.textViewCardKeranjangJumlah)
        val totalPrice: TextView = itemView.findViewById(R.id.textViewCardKeranjangTotalHarga)
        val deleteButton: ImageButton = itemView.findViewById(R.id.imageButtonCardKeranjangDelete)
        val plusButton: ImageButton = itemView.findViewById(R.id.imageButtonCardKeranjangPlus) // Tombol tambah kuantitas
        val minusButton: ImageButton = itemView.findViewById(R.id.imageButtonCardKeranjangMinus) // Tombol kurangi kuantitas
    }
}
