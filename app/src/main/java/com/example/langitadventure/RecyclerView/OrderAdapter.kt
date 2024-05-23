package com.example.langitadventure.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.langitadventure.ItemsViewModelBasket
import com.example.langitadventure.ItemsViewModelOrder
import com.example.langitadventure.R

class OrderAdapter(private val mList: List<ItemsViewModelOrder>) : RecyclerView.Adapter<OrderAdapter.ViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_order, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModelOrder = mList[position]

        holder.imageViewCardOrder.setImageResource(ItemsViewModelOrder.imageResource)
        holder.textViewCardOrderNama.text = ItemsViewModelOrder.nama
        holder.textViewCardOrderHarga.text = ItemsViewModelOrder.hargaPerMalam
        holder.textViewCardOrderDurasi.text = ItemsViewModelOrder.durasi
        holder.textViewCardOrderStatus.text = ItemsViewModelOrder.status
        holder.textViewCardOrderJumlah.text = ItemsViewModelOrder.jumlah
        holder.textViewCardOrderTotal.text = ItemsViewModelOrder.totalBiaya
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewCardOrder: ImageView = itemView.findViewById(R.id.imageViewCardOrder)
        val textViewCardOrderNama: TextView = itemView.findViewById(R.id.textViewCardOrderNama)
        val textViewCardOrderHarga: TextView = itemView.findViewById(R.id.textViewCardOrderHarga)
        val textViewCardOrderDurasi: TextView = itemView.findViewById(R.id.textViewCardOrderDurasi)
        val textViewCardOrderStatus: TextView = itemView.findViewById(R.id.textViewCardOrderStatus)
        val textViewCardOrderJumlah: TextView = itemView.findViewById(R.id.textViewCardOrderJumlah)
        val textViewCardOrderTotal: TextView = itemView.findViewById(R.id.textViewCardOrderTotal)
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}