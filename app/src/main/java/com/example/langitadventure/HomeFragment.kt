package com.example.langitadventure

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.langitadventure.RecyclerView.CategoryAdapter
import com.example.langitadventure.RecyclerView.TendaAdapter

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var param1: String? = null
    private var param2: String? = null

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
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // getting the recyclerview by its id
        val recyclerview = view.findViewById<RecyclerView>(R.id.recyclerViewKategori)

        // this creates a horizontal layout Manager
        recyclerview.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // ArrayList of class ItemsViewModelCategory
        val data = ArrayList<ItemsViewModelCategory>()

        // This loop will create 20 Views containing the image with the count of view
//        for (i in 1..20) {
//            data.add(ItemsViewModelCategory(R.drawable.kategori_tenda, "Item $i"))
//        }

        data.add(ItemsViewModelCategory(R.drawable.kategori_tenda, "Tenda"))
        data.add(ItemsViewModelCategory(R.drawable.kategori_tas, "Tas"))
        data.add(ItemsViewModelCategory(R.drawable.kategori_lampu, "Lampu"))
        data.add(ItemsViewModelCategory(R.drawable.kategori_pakaian, "Pakaian"))
        data.add(ItemsViewModelCategory(R.drawable.kategori_sepatu, "Sepatu"))
        data.add(ItemsViewModelCategory(R.drawable.kategori_alat_masak, "Alat Masak"))
        data.add(ItemsViewModelCategory(R.drawable.kategori_perlengkapan, "Perlengkapan"))

        // This will pass the ArrayList to our Adapter
        val adapter = CategoryAdapter(data)

        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter

        //=================================

        // getting the recyclerview by its id
        val recyclerview1 = view.findViewById<RecyclerView>(R.id.recyclerViewTenda)

        // this creates a horizontal layout Manager
        recyclerview1.layoutManager = GridLayoutManager(requireContext(), 2)

        // ArrayList of class ItemsViewModelCategory
        val data1 = ArrayList<ItemsViewModelTenda>()

        // This loop will create 20 Views containing the image with the count of view
//        for (i in 1..20) {
//            data.add(ItemsViewModelCategory(R.drawable.kategori_tenda, "Item $i"))
//        }

        data1.add(ItemsViewModelTenda(R.drawable.tenda_dome_nsm4, "Tenda Dome NSM 4", "Rp35.000/Malam"))
        data1.add(ItemsViewModelTenda(R.drawable.tenda_dome_compass, "Tenda Dome Compass", "Rp30.000/Malam"))
        data1.add(ItemsViewModelTenda(R.drawable.tenda_dome_arei_eliot, "Tenda Dome Arei Eliot", "Rp35.000/Malam"))
        data1.add(ItemsViewModelTenda(R.drawable.tenda_co_trex, "Tenda Co-Trex", "Rp20.000/Malam"))
        data1.add(ItemsViewModelTenda(R.drawable.tenda_dome_borneo6, "Tenda Dome Borneo 6", "Rp60.000/Malam"))
        data1.add(ItemsViewModelTenda(R.drawable.tenda_go_java6, "Tenda Go Java 6", "Rp65.000/Malam"))
        data1.add(ItemsViewModelTenda(R.drawable.tenda_dome_borneo4, "Tenda Dome Borneo 4", "Rp45.000/Malam"))

        // This will pass the ArrayList to our Adapter
        val adapter1 = TendaAdapter(data1)

        // Setting the Adapter with the recyclerview
        recyclerview1.adapter = adapter1

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
