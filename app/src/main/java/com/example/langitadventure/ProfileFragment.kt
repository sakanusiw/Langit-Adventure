package com.example.langitadventure

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var authManager: AuthManager

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
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        authManager = AuthManager(requireContext())
        authManager.fetchUserProfile()

        // Menggunakan findViewById pada view yang dihasilkan oleh onCreateView
        val buttonClick = view.findViewById<Button>(R.id.buttonKeluar)
        buttonClick.setOnClickListener {
            authManager.logout()
            // Menggunakan requireActivity() sebagai konteks
//            val intent = Intent(requireActivity(), LoginActivity::class.java)
//            startActivity(intent)
        }

        val buttonClick1 = view.findViewById<ImageButton>(R.id.imageButtonRightArrowProfile)
        buttonClick1.setOnClickListener {
            // Menggunakan requireActivity() sebagai konteks
            val intent = Intent(requireActivity(), ProfileEditActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
