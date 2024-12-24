package com.example.langitadventure

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var textViewUserNameProfile: TextView
    private lateinit var textViewUserEmailProfile: TextView
    private lateinit var imageViewProfilePicture: ImageView

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var notificationSwitch: SwitchCompat

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

        notificationSwitch = view.findViewById(R.id.notificationSwitch)
        val sharedPreferences = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE)
        notificationSwitch.isChecked = sharedPreferences.getBoolean("notifications_enabled", false)

        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("notifications_enabled", isChecked).apply()
            Toast.makeText(requireContext(), if (isChecked) "Notifikasi diaktifkan" else "Notifikasi dinonaktifkan", Toast.LENGTH_SHORT).show()
        }

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
            Toast.makeText(requireContext(), "Silahkan Login Terlebih Dahulu", Toast.LENGTH_SHORT).show()
        }

        // Temukan ImageView untuk gambar profil
        imageViewProfilePicture = view.findViewById(R.id.imageViewProfilePicture)

        textViewUserNameProfile = view.findViewById(R.id.textViewUserNameProfile)
        textViewUserEmailProfile = view.findViewById(R.id.textViewUserEmailProfile)

        db = FirebaseFirestore.getInstance()

        updateProfileData() // Panggil ini untuk memuat data pengguna


        // Menggunakan findViewById pada view yang dihasilkan oleh onCreateView
        val buttonClick = view.findViewById<Button>(R.id.buttonKeluar)
        buttonClick.setOnClickListener {
            try {
                // Proses sign out
                Firebase.auth.signOut()

                // Jika sign out berhasil, tampilkan toast berhasil
                Toast.makeText(requireContext(), "Sign out berhasil", Toast.LENGTH_SHORT).show()

                // Setelah sign out berhasil, pindah ke LoginActivity
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish()  // Agar pengguna tidak bisa kembali ke fragment ini

            } catch (e: Exception) {
                // Jika ada kegagalan, tampilkan toast gagal
                Toast.makeText(requireContext(), "Sign out gagal: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e(TAG, "Error saat sign out: ${e.message}")
            }
        }


        val buttonClick1 = view.findViewById<ImageButton>(R.id.imageButtonRightArrowProfile)
        buttonClick1.setOnClickListener {
            // Menggunakan requireActivity() sebagai konteks
            val intent = Intent(requireActivity(), ProfileEditActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun updateProfileData() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            // Ambil referensi ke dokumen pengguna di Firestore berdasarkan UID pengguna yang saat ini masuk
            val userDocRef = db.collection("users").document(currentUser.uid)

            // Ambil data pengguna dari Firestore
            userDocRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Dokumen pengguna ditemukan, perbarui TextView dengan data yang ada di Firestore
                        val nama = document.getString("username") ?: ""
                        val email = document.getString("email") ?: ""
                        val profileImageUrl = document.getString("profileImage") ?: "" // Ambil URL gambar profil

                        // Perbarui TextView
                        textViewUserNameProfile.text = nama
                        textViewUserEmailProfile.text = email

                        // Muat gambar profil menggunakan Picasso atau Glide
                        if (profileImageUrl.isNotEmpty()) {
                            Glide.with(this).load(profileImageUrl).into(imageViewProfilePicture)
                        }
                    } else {
                        // Dokumen pengguna tidak ditemukan atau tidak ada data
                        Log.d(TAG, "Dokumen pengguna tidak ditemukan.")
                    }
                }
                .addOnFailureListener { exception ->
                    // Gagal mengambil data pengguna dari Firestore
                    Log.d(TAG, "Gagal mengambil data pengguna: $exception")
                }
        }
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
