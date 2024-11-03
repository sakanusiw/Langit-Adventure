package com.example.langitadventure

import BasketAdapter
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.langitadventure.RecyclerView.CategoryAdapter

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
    private lateinit var adapter: BasketAdapter
    private lateinit var itemList: List<ItemsViewModelBasket>

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

        // Menggunakan findViewById pada view yang dihasilkan oleh onCreateView
        val buttonClick = view.findViewById<Button>(R.id.buttonBayar)
        buttonClick.setOnClickListener {
            // Menggunakan requireActivity() sebagai konteks
            val intent = Intent(requireActivity(), PaymentActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Declaring and initializing the TextView from layout
        val mTextView = view.findViewById<TextView>(R.id.textViewTotal)
//        val mTextView1 = view.findViewById<TextView>(R.id.textViewCardKeranjangTotal)

        // Declaring a string
        val mString = "Total: Rp210.000"

        // Creating a Spannable String from the above string
        val mSpannableString = SpannableString(mString)

        // Setting underline style from position 0 till length of the spannable string
        mSpannableString.setSpan(UnderlineSpan(), 0, mSpannableString.length, 0)

        // Displaying this spannable string in TextView
        mTextView.text = mSpannableString
//        mTextView1.text = mSpannableString

        //BasketRecyclerView

        // getting the recyclerview by its id
        val recyclerview = view.findViewById<RecyclerView>(R.id.recyclerViewBasket)

        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(requireContext())

        // ArrayList of class ItemsViewModelBasket
        val data = ArrayList<ItemsViewModelBasket>()

        data.add(ItemsViewModelBasket("Tenda Dome NSM 4", "25/12/2024 - 27/12/2024", "Durasi: 2 Malam", 1, "Total Biaya: Rp70.000", R.drawable.tenda_dome_nsm4 ))
        data.add(ItemsViewModelBasket("Tenda Dome NSM 4", "06/03/2024 - 08/03/2024", "Durasi: 2 Malam", 1, "Total Biaya: Rp70.000", R.drawable.tenda_dome_nsm4 ))
        data.add(ItemsViewModelBasket("Tenda Dome NSM 4", "06/03/2024 - 08/03/2024", "Durasi: 2 Malam", 1, "Total Biaya: Rp70.000", R.drawable.tenda_dome_nsm4 ))
//        data.add(ItemsViewModelBasket("Tenda Dome NSM 4", "06/03/2024 - 08/03/2024", "Durasi: 2 Malam", 1, "Total Biaya: Rp70.000", R.drawable.tenda_dome_nsm4 ))
//        data.add(ItemsViewModelBasket("Tenda Dome NSM 4", "06/03/2024 - 08/03/2024", "Durasi: 2 Malam", 1, "Total Biaya: Rp70.000", R.drawable.tenda_dome_nsm4 ))
//        data.add(ItemsViewModelBasket("Tenda Dome NSM 4", "06/03/2024 - 08/03/2024", "Durasi: 2 Malam", 1, "Total Biaya: Rp70.000", R.drawable.tenda_dome_nsm4 ))
//        data.add(ItemsViewModelBasket("Tenda Dome NSM 4", "06/03/2024 - 08/03/2024", "Durasi: 2 Malam", 1, "Total Biaya: Rp70.000", R.drawable.tenda_dome_nsm4 ))
        // This will pass the ArrayList to our Adapter
        val adapter = BasketAdapter(data)

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
