package com.example.langitadventure

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.langitadventure.RecyclerView.CategoryAdapter
import com.example.langitadventure.RecyclerView.TendaAdapter
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

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
    private lateinit var PakaianAdapter: TendaAdapter
    private val dataPakaian = ArrayList<ItemsViewModelTenda>()

//    Adapter untuk fitur Search
    private lateinit var searchAdapter: TendaAdapter


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

        // RecyclerView untuk hasil pencarian
        val recyclerViewSearch = view.findViewById<RecyclerView>(R.id.recyclerViewSearch)
        recyclerViewSearch.layoutManager = GridLayoutManager(requireContext(), 2)

        // Menggunakan adapter yang sama seperti Tenda
        searchAdapter = TendaAdapter(emptyList())
        recyclerViewSearch.adapter = searchAdapter

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

        // RecyclerView untuk Lampu
        val recyclerViewPakaian = view.findViewById<RecyclerView>(R.id.recyclerViewPakaian)
        recyclerViewPakaian.layoutManager = GridLayoutManager(requireContext(), 2)
        PakaianAdapter = TendaAdapter(dataPakaian)
        recyclerViewPakaian.adapter = PakaianAdapter

        // Memuat data dari Firestore
        loadDataFromFirestoreForTenda()
        loadDataFromFirestoreForTas()
        loadDataFromFirestoreForLampu()
        loadDataFromFirestoreForPakaian()

        // Tambahkan listener untuk SearchView
        val searchView = view.findViewById<SearchView>(R.id.searchViewCariBarang)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { performSearch(it, view) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (it.isEmpty()) {
                        resetViewVisibility(view)
                    } else {
                        performSearch(it, view)
                    }
                }
                return true
            }
        })
        // Memuat semua data saat pertama kali membuka fragment
        loadAllData()
    }

    // Fungsi untuk mencari item berdasarkan nama
// Fungsi pencarian
    private fun performSearch(query: String, view: View) {
        // Gabungkan semua data dari kategori
        val allData = dataTenda + dataTas + dataLampu + dataPakaian

        // Filter data berdasarkan nama barang
        val filteredList = allData.filter { item ->
            item.textnama.contains(query, ignoreCase = true)
        }

        // Atur visibilitas elemen
        val scrollViewHome = view.findViewById<ScrollView>(R.id.scrollViewHome)
        val recyclerViewSearch = view.findViewById<RecyclerView>(R.id.recyclerViewSearch)

        if (filteredList.isNotEmpty()) {
            // Sembunyikan elemen lain dan tampilkan RecyclerView pencarian
            scrollViewHome.visibility = View.GONE
            recyclerViewSearch.visibility = View.VISIBLE

            // Update adapter pencarian dengan data hasil filter
            searchAdapter.updateData(filteredList)
        } else {
            // Jika tidak ada hasil, tampilkan pesan
            Toast.makeText(requireContext(), "No items found", Toast.LENGTH_SHORT).show()
        }
    }

    // Fungsi untuk mereset visibilitas elemen
    private fun resetViewVisibility(view: View) {
        val scrollViewHome = view.findViewById<ScrollView>(R.id.scrollViewHome)
        val recyclerViewSearch = view.findViewById<RecyclerView>(R.id.recyclerViewSearch)

        // Tampilkan elemen utama dan sembunyikan RecyclerView pencarian
        scrollViewHome.visibility = View.VISIBLE
        recyclerViewSearch.visibility = View.GONE

        // Kosongkan data adapter pencarian
        searchAdapter.updateData(emptyList())
    }

    // Fungsi untuk memuat ulang data
    private fun loadAllData() {
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

    private fun loadDataFromFirestoreForPakaian() {
        firestore.collection("items") // Nama koleksi Firestore
            .whereEqualTo("category", "Pakaian") // Filter kategori jika diperlukan
            .get()
            .addOnSuccessListener { documents ->
                dataPakaian.clear() // Hapus data lama
                for (document in documents) {
                    // Konversi data Firestore ke model item Tenda
                    val item = convertDocumentToItem(document)
                    dataPakaian.add(item)
                }
                // Notifikasi perubahan data di adapter
                PakaianAdapter.notifyDataSetChanged()
                Log.d("HomeFragment", "Total items loaded: ${dataPakaian.size}")
            }
            .addOnFailureListener { exception ->
                Log.e("HomeFragment", "Error loading data from Firestore", exception)
                Toast.makeText(requireContext(), "Error loading Pakaian data", Toast.LENGTH_SHORT).show() // Feedback untuk pengguna
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
        val stock = document.getLong("stock")?.toInt() ?: 0

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
            stock = "Stok: $stock",
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
