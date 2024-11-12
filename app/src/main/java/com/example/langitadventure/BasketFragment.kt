package com.example.langitadventure

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.langitadventure.RecyclerView.BasketAdapter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BasketFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BasketFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var basketAdapter: BasketAdapter
    private var itemsList: MutableList<ItemsViewModelBasket> = mutableListOf()
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_basket, container, false)

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Menggunakan findViewById pada view yang dihasilkan oleh onCreateView
        val buttonBayar = view.findViewById<Button>(R.id.buttonBayar)
        buttonBayar.setOnClickListener {
            val currentUser = auth.currentUser
            val intent = if (currentUser != null) {
                Intent(requireActivity(), PaymentActivity::class.java)
            } else {
                Intent(requireActivity(), LoginActivity::class.java)
            }
            startActivity(intent)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize TextView for total price
        val totalPriceTextView = view.findViewById<TextView>(R.id.textViewTotalHarga)
        val formattedTotal = "Total: Rp0"
        val spannableString = SpannableString(formattedTotal)
        spannableString.setSpan(UnderlineSpan(), 0, spannableString.length, 0)
        totalPriceTextView.text = spannableString

        //BasketRecyclerView

        // Set up RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewBasket)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        basketAdapter = BasketAdapter(
            itemsList,
            onItemRemoved = { itemPrice ->
                // Update total price after item removed
                updateTotalPrice(itemPrice)
            },
            onQuantityChanged = { totalPrice ->
                // Update total price after quantity changed
                updateTotalPrice(totalPrice)
            }
        )
        recyclerView.adapter = basketAdapter

        // Fetch basket items from Firestore
        fetchBasketItems(totalPriceTextView)
    }

    private fun updateTotalPrice(price: Int) {
        val totalPriceTextView = view?.findViewById<TextView>(R.id.textViewTotalHarga)
        val totalHarga = itemsList.sumOf { it.totalPrice } // Calculate the total price
        val formattedPrice = String.format("Total: Rp%,d", totalHarga)
        totalPriceTextView?.text = SpannableString(formattedPrice).apply {
            setSpan(UnderlineSpan(), 0, length, 0)
        }
        togglePaymentButton(totalHarga)
    }

    private fun fetchBasketItems(totalPriceTextView: TextView) {
        val userId = auth.currentUser?.uid
        Log.d("BasketFragment", "Fetching items for User ID: $userId")

        userId?.let {
            firestore.collection("users").document(it).collection("cart")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    itemsList.clear() // Clear list sebelum menambah data baru
                    val itemIds = mutableSetOf<String>() // Set untuk menyimpan itemId yang unik
                    var totalHarga = 0
                    Log.d("BasketFragment", "Fetched ${querySnapshot.size()} items")

                    querySnapshot.documents.forEach { document ->
                        val itemId = document.getString("itemId") ?: ""
                        if (itemId.isNotEmpty() && itemIds.add(itemId)) { // Hanya menambah item jika itemId unik
                            val itemName = document.getString("itemName") ?: ""
                            val startDate = document.getLong("startDate")
                            val endDate = document.getLong("endDate")
                            val duration = document.getLong("duration")?.toInt() ?: 0
                            val quantity = document.getLong("quantity")?.toInt() ?: 1
                            val imageUrl = document.getString("imageUrl") ?: ""

                            getItemPrice(itemId) { pricePerNight ->
                                val totalItemPrice = pricePerNight * duration * quantity
                                val formattedStartDate = formatDate(startDate)
                                val formattedEndDate = formatDate(endDate)

                                val basketItem = ItemsViewModelBasket(
                                    itemName,
                                    formattedStartDate,
                                    formattedEndDate,
                                    duration,
                                    quantity,
                                    totalItemPrice,
                                    imageUrl,
                                    itemId,                // Pastikan itemId ditambahkan
                                    pricePerNight          // Tambahkan pricePerNight
                                )

                                itemsList.add(basketItem)
                                totalHarga += totalItemPrice

                                // Update UI setelah semua item selesai diproses
                                if (itemsList.size == itemIds.size) {
                                    basketAdapter.notifyDataSetChanged()
                                    val formattedPrice = String.format("Total: Rp%,d", totalHarga)
                                    totalPriceTextView.text = SpannableString(formattedPrice).apply {
                                        setSpan(UnderlineSpan(), 0, length, 0)
                                    }
                                    togglePaymentButton(totalHarga)
                                }
                            }
                        } else {
                            Log.e("BasketFragment", "Item ID tidak valid atau duplikat di cart user.")
                        }
                    }

                    // Atur status tombol bayar sesuai jumlah item
                    view?.findViewById<Button>(R.id.buttonBayar)?.isEnabled = itemsList.isNotEmpty()
                }
                .addOnFailureListener { e ->
                    Log.e("BasketFragment", "Error fetching basket items: ", e)
                }
        }
    }


    private fun getItemPrice(itemId: String, callback: (Int) -> Unit) {

        if (itemId.isEmpty()) {
            Log.e("BasketFragment", "Invalid itemId")
        }

        if (itemId.isNotEmpty()) {
            firestore.collection("items")
                .document(itemId)
                .get()
                .addOnSuccessListener { document ->
                    val pricePerNight = document.getLong("price_per_night")?.toInt() ?: 0
                    callback(pricePerNight)
                }
                .addOnFailureListener { e ->
                    Log.e("BasketFragment", "Error fetching item price for itemId: $itemId", e)
                    callback(0)
                }
        } else {
            Log.e("BasketFragment", "itemId tidak valid atau kosong")
            callback(0)
        }
    }

    // Fungsi untuk mengubah timestamp menjadi tanggal dengan format DD/MM/YYYY
    fun formatDate(timestamp: Long?): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Format tanggal
        return if (timestamp != null) {
            val date = Date(timestamp)  // Mengonversi timestamp menjadi Date
            sdf.format(date)            // Mengubah tanggal menjadi format yang diinginkan
        } else {
            "Invalid Date"  // Tanggal tidak valid jika null
        }
    }

    private fun togglePaymentButton(totalHarga: Int) {
        val buttonBayar = view?.findViewById<Button>(R.id.buttonBayar)
        buttonBayar?.isEnabled = totalHarga > 0
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BasketFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BasketFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
