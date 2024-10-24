package com.example.langitadventure

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.langitadventure.RecyclerView.OrderAdapter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OrderFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OrderFragment : Fragment() {
    // TODO: Rename and change types of parameters
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
        return inflater.inflate(R.layout.fragment_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // getting the recyclerview by its id
        val recyclerview = view.findViewById<RecyclerView>(R.id.recyclerViewOrder)

        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(requireContext())

        // ArrayList of class ItemsViewModelBasket
        val data = ArrayList<ItemsViewModelOrder>()

        data.add(ItemsViewModelOrder(R.drawable.tenda_dome_nsm4,"Tenda Dome NSM4","Rp35.000/Malam","x2 Malam","Sedang Disewa","x1 Barang","Total Biaya: Rp70.000"))
        data.add(ItemsViewModelOrder(R.drawable.tenda_dome_nsm4,"Tenda Dome NSM4","Rp35.000/Malam","x2 Malam","Sedang Disewa","x1 Barang","Total Biaya: Rp70.000"))
        data.add(ItemsViewModelOrder(R.drawable.tenda_dome_nsm4,"Tenda Dome NSM4","Rp35.000/Malam","x2 Malam","Sedang Disewa","x1 Barang","Total Biaya: Rp70.000"))
//        data.add(ItemsViewModelOrder(R.drawable.tenda_dome_nsm4,"Tenda Dome NSM4","Rp35.000/Malam","x2 Malam","Sedang Disewa","x1 Barang","Total Biaya: Rp70.000"))
//        data.add(ItemsViewModelOrder(R.drawable.tenda_dome_nsm4,"Tenda Dome NSM4","Rp35.000/Malam","x2 Malam","Sedang Disewa","x1 Barang","Total Biaya: Rp70.000"))
//        data.add(ItemsViewModelOrder(R.drawable.tenda_dome_nsm4,"Tenda Dome NSM4","Rp35.000/Malam","x2 Malam","Sedang Disewa","x1 Barang","Total Biaya: Rp70.000"))
//        data.add(ItemsViewModelOrder(R.drawable.tenda_dome_nsm4,"Tenda Dome NSM4","Rp35.000/Malam","x2 Malam","Sedang Disewa","x1 Barang","Total Biaya: Rp70.000"))
        // This will pass the ArrayList to our Adapter
        val adapter = OrderAdapter(data)

        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OrderFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OrderFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}