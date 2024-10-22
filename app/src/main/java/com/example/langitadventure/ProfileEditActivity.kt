package com.example.langitadventure

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage

class ProfileEditActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var editTextNamaLengkap: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextKonfirmasiPassword: EditText
    private lateinit var buttonSimpan: Button
    private lateinit var imageButtonBack: ImageButton
    private lateinit var buttonCheckVerificationStatus: Button
    private lateinit var imageViewProfilePicture: ImageView
    private var currentProfileImageUrl: String? = null
    private val IMAGE_PICK_CODE = 1000
    private val CAMERA_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        auth = FirebaseAuth.getInstance()

        editTextNamaLengkap = findViewById(R.id.editTextNamaLengkap)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextKonfirmasiPassword = findViewById(R.id.editTextKonfirmasiPassword)
        buttonSimpan = findViewById(R.id.buttonSimpan)
        imageButtonBack = findViewById(R.id.imageButtonBack)
        buttonCheckVerificationStatus = findViewById(R.id.buttonCheckVerificationStatus)
        imageViewProfilePicture = findViewById(R.id.imageViewProfilePicture)

        // Sembunyikan tombol cek status verifikasi secara default
        buttonCheckVerificationStatus.visibility = View.GONE

        // Isi form dengan data pengguna yang sekarang
        loadUserData()

        // Menambahkan listener untuk gambar profil
        imageViewProfilePicture.setOnClickListener {
            openGallery() // Ganti dengan openCamera() jika ingin menggunakan kamera
        }

        buttonSimpan.setOnClickListener {
            updateUserProfile()
        }

        imageButtonBack.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // Tambahkan listener untuk tombol cek status verifikasi
        buttonCheckVerificationStatus.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            user?.reload()?.addOnCompleteListener { reloadTask ->
                if (reloadTask.isSuccessful) {
                    if (user.isEmailVerified) {
                        // Jika email terverifikasi, update profil
                        val newEmail = editTextEmail.text.toString().trim()
                        val newPassword = editTextPassword.text.toString().trim()
                        updateEmailAndPassword(user, newEmail, newPassword)
                    } else {
                        Toast.makeText(this, "Email belum diverifikasi. Silakan cek email Anda.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Gagal memuat status verifikasi.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                IMAGE_PICK_CODE -> {
                    val imageUri = data?.data
                    imageViewProfilePicture.setImageURI(imageUri) // Tampilkan gambar di ImageView

                    // Simpan gambar ke Firestore (misalnya, upload gambar ke Cloud Storage)
                    if (imageUri != null) {
                        Log.d("ProfileEditActivity", "Image URI: $imageUri")
                        uploadProfileImage(imageUri)
                    } else {
                        Log.e("ProfileEditActivity", "Image URI is null")
                    }

//                    uploadProfileImage(imageUri)
                }
            }
        }
    }

    private fun uploadProfileImage(imageUri: Uri?) {
        if (imageUri != null) {
            Log.d("ProfileEditActivity", "Current User ID: ${auth.currentUser?.uid}")
            // Simpan gambar ke Cloud Storage dan dapatkan URL-nya
            val storageRef = FirebaseStorage.getInstance().reference.child("profileImages/${auth.currentUser?.uid}")
            storageRef.putFile(imageUri)
                .addOnSuccessListener {
                    // Setelah upload selesai, dapatkan URL gambar
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        saveProfileImageUrlToFirestore(uri.toString())
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("ProfileEditActivity", "Upload failed: ${e.localizedMessage}")
                    Toast.makeText(this, "Gagal mengunggah gambar: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveProfileImageUrlToFirestore(imageUrl: String) {
        val user = auth.currentUser
        if (user != null) {
            val userDocRef = FirebaseFirestore.getInstance().collection("users").document(user.uid)
            userDocRef.update("profileImage", imageUrl)
                .addOnSuccessListener {
                    Toast.makeText(this, "Gambar profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
                    // Arahkan pengguna ke ProfileActivity
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    finish() // Tambahkan ini jika Anda ingin menutup ProfileEditActivity
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Gagal memperbarui gambar profil: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun loadUserData() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            editTextNamaLengkap.setText(currentUser.displayName)
            editTextEmail.setText(currentUser.email)
            // Load gambar profil dari Firestore jika ada
            loadProfileImageFromFirestore()
        }
    }

    private fun loadProfileImageFromFirestore() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userDocRef = FirebaseFirestore.getInstance().collection("users").document(userId)
            userDocRef.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val imageUrl = document.getString("profileImage")
                    currentProfileImageUrl = imageUrl // Simpan URL gambar profil
                    if (!imageUrl.isNullOrEmpty()) {
                        Glide.with(this).load(imageUrl).into(imageViewProfilePicture)
                    }
                }
            }
        }
    }

    private fun updateUserProfile() {
        val newName = editTextNamaLengkap.text.toString().trim()
        val newEmail = editTextEmail.text.toString().trim()
        val newPassword = editTextPassword.text.toString().trim()
        val confirmPassword = editTextKonfirmasiPassword.text.toString().trim()

        // Validasi input
        if (newName.isEmpty() || newEmail.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Mohon lengkapi semua kolom", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword != confirmPassword) {
            Toast.makeText(this, "Kata sandi tidak cocok", Toast.LENGTH_SHORT).show()
            return
        }

        val user = auth.currentUser
        if (user != null) {
            // Perbarui nama pengguna
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build()

            user.updateProfile(profileUpdates).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Kirim email verifikasi sebelum update email dan password
                    user.sendEmailVerification().addOnCompleteListener { emailTask ->
                        if (emailTask.isSuccessful) {
                            Toast.makeText(this, "Verifikasi email telah dikirim. Silakan cek email Anda.", Toast.LENGTH_SHORT).show()
                            // Tampilkan tombol cek status verifikasi
                            buttonCheckVerificationStatus.visibility = View.VISIBLE

                            // Simpan perubahan ke Firestore
                            val db = FirebaseFirestore.getInstance()
                            val userId = user.uid
                            val userData = hashMapOf(
                                "username" to newName,
                                "email" to newEmail,
                                "profileImage" to (currentProfileImageUrl ?: "") // Simpan URL gambar profil yang ada
                            )
                            db.collection("users").document(userId).set(userData, SetOptions.merge())
                                .addOnSuccessListener {
                                    Log.d("ProfileEditActivity", "Data pengguna berhasil diperbarui di Firestore")
                                }
                                .addOnFailureListener { e ->
                                    Log.w("ProfileEditActivity", "Gagal memperbarui data pengguna di Firestore", e)
                                }

                            // Jalankan manual check untuk melihat apakah email sudah terverifikasi
                            checkEmailVerification(user)
                        } else {
                            Toast.makeText(this, "Gagal mengirim verifikasi email", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Gagal memperbarui profil", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkEmailVerification(user: FirebaseUser) {
        // Tambahkan delay sebelum reload untuk memastikan Firebase punya waktu memproses perubahan
        Handler(Looper.getMainLooper()).postDelayed({
            // Force refresh the user data from Firebase
            user.reload().addOnCompleteListener { reloadTask ->
                if (reloadTask.isSuccessful) {
                    Log.d("ProfileEditActivity", "Reload berhasil, status email diverifikasi: ${user.isEmailVerified}")
                    if (user.isEmailVerified) {
                        // Email terverifikasi, lanjutkan pembaruan email dan password
                        val newEmail = editTextEmail.text.toString().trim()
                        val newPassword = editTextPassword.text.toString().trim()

                        updateEmailAndPassword(user, newEmail, newPassword)
                    } else {
                        Toast.makeText(this, "Email belum diverifikasi. Silakan verifikasi email Anda.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("ProfileEditActivity", "Reload gagal: ${reloadTask.exception}")
                    Toast.makeText(this, "Gagal memperbarui profil", Toast.LENGTH_SHORT).show()
                }
            }
        }, 3000) // Tambahkan delay 3 detik
    }


    private fun updateEmailAndPassword(user: FirebaseUser, newEmail: String, newPassword: String) {
        user.updateEmail(newEmail).addOnCompleteListener { emailTask ->
            if (emailTask.isSuccessful) {
                user.updatePassword(newPassword).addOnCompleteListener { passwordTask ->
                    if (passwordTask.isSuccessful) {
                        // Setelah sukses memperbarui email dan password, arahkan ke halaman profil
                        Toast.makeText(this, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, ProfileActivity::class.java)
                        startActivity(intent)
                    } else {
                        // Tangkap kesalahan jika gagal memperbarui password
                        Log.e("ProfileEditActivity", "Gagal memperbarui kata sandi: ${passwordTask.exception?.localizedMessage}")
                        Toast.makeText(this, "Gagal memperbarui kata sandi: ${passwordTask.exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                // Tangkap kesalahan jika gagal memperbarui email
                Log.e("ProfileEditActivity", "Gagal memperbarui email: ${emailTask.exception?.localizedMessage}")
                Toast.makeText(this, "Gagal memperbarui email: ${emailTask.exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
