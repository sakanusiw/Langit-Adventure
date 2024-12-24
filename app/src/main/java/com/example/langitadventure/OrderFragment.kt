package com.example.langitadventure

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.langitadventure.RecyclerView.OrderAdapter
import com.example.langitadventure.utils.NotificationHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

class OrderFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var orderAdapter: OrderAdapter
    private var ordersList: MutableList<ItemsViewModelOrder> = mutableListOf()
    private lateinit var progressBar: ProgressBar
    private lateinit var textViewEmpty: TextView
    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order, container, false)

        // Initialize Views
        recyclerView = view.findViewById(R.id.recyclerViewOrder)
        progressBar = view.findViewById(R.id.progressBarOrder)
        textViewEmpty = view.findViewById(R.id.textViewEmptyOrder)

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        orderAdapter = OrderAdapter(ordersList)
        recyclerView.adapter = orderAdapter

        fetchOrders()

        return view
    }

    override fun onStart() {
        super.onStart()
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users").document(userId).collection("orders")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e(TAG, "Error fetching order updates", error)
                    return@addSnapshotListener
                }

                snapshots?.documentChanges?.forEach { docChange ->
                    when (docChange.type) {
                        DocumentChange.Type.MODIFIED -> {
                            val updatedOrder = docChange.document.data
                            handleOrderUpdate(updatedOrder)
                        }
                        else -> {}
                    }
                }
            }
    }

    private fun handleOrderUpdate(updatedOrder: Map<String, Any>) {
        val status = updatedOrder["status"] as? String ?: return
        val itemName = updatedOrder["itemName"] as? String ?: "Pesanan"

        // Periksa apakah notifikasi diaktifkan
        val toggleOn = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE)
            .getBoolean("notifications_enabled", false)

        if (toggleOn) {
            NotificationHelper.sendNotification(
                requireContext(),
                "Status Pesanan",
                "Status untuk $itemName telah diperbarui: $status"
            )
        }
    }

    private fun fetchOrders() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            displayEmptyState("Pengguna belum login.")
            return
        }

        progressBar.visibility = View.VISIBLE
        textViewEmpty.visibility = View.GONE
        ordersList.clear()

        firestore.collection("users").document(currentUser.uid).collection("orders")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { querySnapshot ->
                querySnapshot.documents.forEach { document ->
                    val order = ItemsViewModelOrder(
                        imageUrl = document.getString("imageUrl") ?: "",
                        namaBarang = document.getString("itemName") ?: "",
                        hargaPerMalam = document.getLong("price_per_night")?.toInt() ?: 0,
                        durasi = document.getLong("duration")?.toInt() ?: 0,
                        jumlahBarang = document.getLong("quantity")?.toInt() ?: 0,
                        totalBiaya = document.getLong("totalPrice")?.toInt() ?: 0,
                        status = document.getString("status") ?: "",
                        timestamp = document.getLong("timestamp"),
                        startDate = document.getLong("startDate"),
                        endDate = document.getLong("endDate")
                    )
                    ordersList.add(order)
                }
                updateUI()
            }
            .addOnFailureListener {
                displayEmptyState("Gagal memuat pesanan. Coba lagi nanti.")
            }
    }

    private fun updateUI() {
        progressBar.visibility = View.GONE
        if (ordersList.isNotEmpty()) {
            textViewEmpty.visibility = View.GONE
            orderAdapter.notifyDataSetChanged()
        } else {
            displayEmptyState("Belum ada pesanan.")
        }
    }

    private fun displayEmptyState(message: String) {
        progressBar.visibility = View.GONE
        textViewEmpty.visibility = View.VISIBLE
        textViewEmpty.text = message
    }

    companion object {
        private const val TAG = "OrderFragment"
    }
}
