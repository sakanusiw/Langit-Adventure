package com.example.langitadventure.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.langitadventure.ItemsViewModelOrder
import com.example.langitadventure.R
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class OrderAdapter(private val orders: List<ItemsViewModelOrder>) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]

        // Memuat gambar menggunakan Glide
        Glide.with(holder.imageView.context)
            .load(order.imageUrl)
            .into(holder.imageView)

        // Mengisi data teks
        holder.textViewNama.text = order.namaBarang
        holder.textViewHarga.text = "Rp${formatCurrency(order.hargaPerMalam)}/Malam"
        holder.textViewDurasi.text = "x${order.durasi} Malam"
        holder.textViewStatus.text = order.status
        holder.textViewJumlah.text = "x${order.jumlahBarang} Barang"
        holder.textViewTotal.text = "Total Biaya: Rp${formatCurrency(order.totalBiaya)}"

        // Format timestamp (pesanan dibuat)
        order.timestamp?.let {
            holder.textViewTimestamp.text = "Dipesan: ${formatTimestamp(it)}"
        }

        // Format tanggal sewa (startDate dan endDate)
        val startDateFormatted = formatDate(order.startDate)
        val endDateFormatted = formatDate(order.endDate)
        holder.textViewTanggal.text = "Sewa: $startDateFormatted - $endDateFormatted"
    }

    override fun getItemCount(): Int = orders.size

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageViewCardOrder)
        val textViewNama: TextView = itemView.findViewById(R.id.textViewCardOrderNama)
        val textViewHarga: TextView = itemView.findViewById(R.id.textViewCardOrderHarga)
        val textViewDurasi: TextView = itemView.findViewById(R.id.textViewCardOrderDurasi)
        val textViewStatus: TextView = itemView.findViewById(R.id.textViewCardOrderStatus)
        val textViewJumlah: TextView = itemView.findViewById(R.id.textViewCardOrderJumlah)
        val textViewTotal: TextView = itemView.findViewById(R.id.textViewCardOrderTotal)
        val textViewTimestamp: TextView = itemView.findViewById(R.id.textViewCardOrderTimestamp)
        val textViewTanggal: TextView = itemView.findViewById(R.id.textViewCardOrderTanggal)
    }

    // Fungsi untuk memformat angka menjadi format rupiah
    private fun formatCurrency(amount: Int?): String {
        val format = NumberFormat.getNumberInstance(Locale("in", "ID"))
        return format.format(amount ?: 0)
    }

    // Fungsi untuk memformat timestamp menjadi format waktu yang lebih terbaca
    private fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    // Fungsi untuk memformat timestamp menjadi tanggal
    private fun formatDate(timestamp: Long?): String {
        if (timestamp == null) return "Tanggal tidak valid"
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
