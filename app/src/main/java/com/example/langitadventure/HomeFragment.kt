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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var firestore: FirebaseFirestore
    private lateinit var tendaAdapter: TendaAdapter
    private val data1 = ArrayList<ItemsViewModelTenda>()

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
        tendaAdapter = TendaAdapter(data1)
        recyclerViewTenda.adapter = tendaAdapter

        // Memuat data dari Firestore
        loadDataFromFirestore()
    }

    private fun loadDataFromFirestore() {
        firestore.collection("items") // Nama koleksi Firestore
            .get()
            .addOnSuccessListener { result ->
                data1.clear() // Hapus data lama
                for (document in result) {
                    val item = document.toObject<ItemsViewModelTenda>()
                    item.itemId = document.id // Set ID dokumen sebagai itemId
                    Log.d("HomeFragment", "Loaded item: $item") // Log item yang diambil
                    data1.add(item)
                }
                // Notifikasi perubahan data di adapter
                tendaAdapter.notifyDataSetChanged()
                Log.d("HomeFragment", "Total items loaded: ${data1.size}")
            }
            .addOnFailureListener { exception ->
                Log.e("HomeFragment", "Error loading data from Firestore", exception)
                Toast.makeText(requireContext(), "Error loading data", Toast.LENGTH_SHORT).show() // Feedback untuk pengguna
            }
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
