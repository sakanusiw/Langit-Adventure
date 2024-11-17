package com.example.langitadventure

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.langitadventure.RecyclerView.CategoryAdapter
import com.example.langitadventure.RecyclerView.TendaAdapter
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var firestore: FirebaseFirestore
//    Adapter dan data untuk masing-masing barang
    private lateinit var tendaAdapter: TendaAdapter
    private val dataTenda = ArrayList<ItemsViewModelTenda>()
    private lateinit var tasAdapter: TendaAdapter
    private val dataTas = ArrayList<ItemsViewModelTenda>()
    private lateinit var LampuAdapter: TendaAdapter
    private val dataLampu = ArrayList<ItemsViewModelTenda>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        firestore = FirebaseFirestore.getInstance() // Initialize Firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView untuk kategori
        val recyclerViewKategori = view.findViewById<RecyclerView>(R.id.recyclerViewKategori)
        recyclerViewKategori.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val dataKategori = listOf(
            ItemsViewModelCategory(R.drawable.kategori_tenda, "Tenda"),
            ItemsViewModelCategory(R.drawable.kategori_tas, "Tas"),
            ItemsViewModelCategory(R.drawable.kategori_lampu, "Lampu"),
            ItemsViewModelCategory(R.drawable.kategori_pakaian, "Pakaian"),
            ItemsViewModelCategory(R.drawable.kategori_sepatu, "Sepatu"),
            ItemsViewModelCategory(R.drawable.kategori_alat_masak, "Alat Masak"),
            ItemsViewModelCategory(R.drawable.kategori_perlengkapan, "Perlengkapan")
        )

        val categoryAdapter = CategoryAdapter(dataKategori)
        recyclerViewKategori.adapter = categoryAdapter

        //=================================

        // RecyclerView untuk Tenda
        val recyclerViewTenda = view.findViewById<RecyclerView>(R.id.recyclerViewTenda)
        recyclerViewTenda.layoutManager = GridLayoutManager(requireContext(), 2)

        // Menginisialisasi adapter untuk Tenda dan mengaturnya ke RecyclerView
        tendaAdapter = TendaAdapter(dataTenda)
        recyclerViewTenda.adapter = tendaAdapter

        // RecyclerView untuk Tas
        val recyclerViewTas = view.findViewById<RecyclerView>(R.id.recyclerViewTas)
        recyclerViewTas.layoutManager = GridLayoutManager(requireContext(), 2)
        tasAdapter = TendaAdapter(dataTas)
        recyclerViewTas.adapter = tasAdapter

        // RecyclerView untuk Lampu
        val recyclerViewLampu = view.findViewById<RecyclerView>(R.id.recyclerViewLampu)
        recyclerViewLampu.layoutManager = GridLayoutManager(requireContext(), 2)
        LampuAdapter = TendaAdapter(dataLampu)
        recyclerViewLampu.adapter = LampuAdapter

        // Memuat data dari Firestore
        loadDataFromFirestoreForTenda()
        loadDataFromFirestoreForTas()
        loadDataFromFirestoreForLampu()
    }

    private fun loadDataFromFirestoreForTenda() {
        firestore.collection("items") // Nama koleksi Firestore
            .whereEqualTo("category", "Tenda") // Filter kategori jika diperlukan
            .get()
            .addOnSuccessListener { documents ->
                dataTenda.clear() // Hapus data lama
                for (document in documents) {
                    // Konversi data Firestore ke model item Tenda
                    val item = convertDocumentToItem(document)
                    dataTenda.add(item)
                }
                // Notifikasi perubahan data di adapter
                tendaAdapter.notifyDataSetChanged()
                Log.d("HomeFragment", "Total items loaded: ${dataTenda.size}")
            }
            .addOnFailureListener { exception ->
                Log.e("HomeFragment", "Error loading data from Firestore", exception)
                Toast.makeText(requireContext(), "Error loading Tenda data", Toast.LENGTH_SHORT).show() // Feedback untuk pengguna
            }
    }

    private fun loadDataFromFirestoreForTas() {
        firestore.collection("items") // Nama koleksi Firestore
            .whereEqualTo("category", "Tas") // Filter kategori jika diperlukan
            .get()
            .addOnSuccessListener { documents ->
                dataTas.clear() // Hapus data lama
                for (document in documents) {
                    // Konversi data Firestore ke model item Tenda
                    val item = convertDocumentToItem(document)
                    dataTas.add(item)
                }
                // Notifikasi perubahan data di adapter
                tasAdapter.notifyDataSetChanged()
                Log.d("HomeFragment", "Total items loaded: ${dataTas.size}")
            }
            .addOnFailureListener { exception ->
                Log.e("HomeFragment", "Error loading data from Firestore", exception)
                Toast.makeText(requireContext(), "Error loading Tenda data", Toast.LENGTH_SHORT).show() // Feedback untuk pengguna
            }
    }

    private fun loadDataFromFirestoreForLampu() {
        firestore.collection("items") // Nama koleksi Firestore
            .whereEqualTo("category", "Lampu") // Filter kategori jika diperlukan
            .get()
            .addOnSuccessListener { documents ->
                dataLampu.clear() // Hapus data lama
                for (document in documents) {
                    // Konversi data Firestore ke model item Tenda
                    val item = convertDocumentToItem(document)
                    dataLampu.add(item)
                }
                // Notifikasi perubahan data di adapter
                LampuAdapter.notifyDataSetChanged()
                Log.d("HomeFragment", "Total items loaded: ${dataLampu.size}")
            }
            .addOnFailureListener { exception ->
                Log.e("HomeFragment", "Error loading data from Firestore", exception)
                Toast.makeText(requireContext(), "Error loading Tenda data", Toast.LENGTH_SHORT).show() // Feedback untuk pengguna
            }
    }

    private fun convertDocumentToItem(document: DocumentSnapshot): ItemsViewModelTenda {
        val namaBarang = document.getString("name") ?: ""
        val hargaPerMalam = document.getDouble("price_per_night")?.toInt() ?: 0
        val gambarBarang = document.getString("image_url") ?: ""
        val availability = document.getBoolean("availability") ?: false
        val bookingCount = document.getLong("booking_count")?.toInt() ?: 0
        val category = document.getString("category") ?: ""
        val description = document.getString("description") ?: ""

        // Log nilai yang diambil untuk debugging
        Log.d("TendaActivity", "namaBarang: $namaBarang, hargaPerMalam: $hargaPerMalam, gambarBarang: $gambarBarang, availability: $availability, bookingCount: $bookingCount, category: $category, description: $description")

        return ItemsViewModelTenda(
            imageUrl = gambarBarang, // URL gambar dari Firestore
            textnama = namaBarang,
            textharga = "Rp$hargaPerMalam/Malam", // Format harga
            availability = availability,
            bookingCount = bookingCount,
            category = category,
            description = description,
            itemId = document.id // ID dokumen unik
        )
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
