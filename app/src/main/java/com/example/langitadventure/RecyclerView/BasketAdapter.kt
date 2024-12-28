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
                        // Update the UI and notify listener of the removed item
                        mList.removeAt(position)
                        notifyItemRemoved(position)
                        // Update total price after item removal (if required)
                        onItemRemoved(item.totalPrice)
                        // Notify adapter if necessary to refresh
                        notifyItemRangeChanged(position, mList.size)
                    }
                    .addOnFailureListener { e ->
                        Log.e("BasketAdapter", "Failed to delete item: $cartDocumentId", e)
                    }
            } else {
                Log.e("BasketAdapter", "cartDocumentId is empty for item: ${item.itemId}")
            }
        }

        // Plus button (Adding quantity)
        holder.plusButton.setOnClickListener {
            // Check stock from Firestore
            db.collection("items").document(item.itemId)
                .get()
                .addOnSuccessListener { document ->
                    val stock = document.getLong("stock")?.toInt() ?: 0
                    val maxQuantity = minOf(10, stock)
                    if (item.quantity < maxQuantity) {
                        item.quantity++
                        updateItemQuantity(item, holder)
                    } else {
                        // Show a message or disable button if stock is exceeded
                        Log.d("BasketAdapter", "Not enough stock. Available stock: $stock")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("BasketAdapter", "Error fetching stock", e)
                }
        }

        // Minus button (Decreasing quantity)
        holder.minusButton.setOnClickListener {
            if (item.quantity > 1) { // Limit quantity to a min of 1
                item.quantity--
                updateItemQuantity(item, holder)
            }
        }
    }

    private fun updateItemQuantity(item: ItemsViewModelBasket, holder: ViewHolder) {
        // Update total price based on new quantity
        item.totalPrice = item.pricePerNight * item.quantity * item.duration
        holder.quantity.text = item.quantity.toString()
        holder.totalPrice.text = "Rp${item.totalPrice}"

        // Update quantity and total price in Firestore
        db.collection("users").document(auth.currentUser!!.uid).collection("cart")
            .document(item.cartDocumentId)
            .update("quantity", item.quantity, "totalPrice", item.totalPrice)
            .addOnSuccessListener {
                onQuantityChanged(item.totalPrice) // Update overall total price
            }
            .addOnFailureListener { e ->
                Log.e("BasketAdapter", "Failed to update item quantity: ${item.cartDocumentId}", e)
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
        val plusButton: ImageButton = itemView.findViewById(R.id.imageButtonCardKeranjangPlus) // Add quantity button
        val minusButton: ImageButton = itemView.findViewById(R.id.imageButtonCardKeranjangMinus) // Subtract quantity button
    }
}
