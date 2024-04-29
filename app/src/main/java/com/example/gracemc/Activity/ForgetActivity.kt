package com.example.gracemc.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.Gravity
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.gracemc.R
import com.example.gracemc.databinding.ActivityForgetBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class ForgetActivity : AppCompatActivity() {

    private lateinit var binding : ActivityForgetBinding
    private lateinit var fAuth   : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fAuth =  FirebaseAuth.getInstance()

        binding.btnback.setOnClickListener {
            val intent = Intent(this, MasukActivity::class.java)
            startActivity(intent)
        }

        binding.btnReset.setOnClickListener {
//          Pendeklarasian variabel email
            val email: String = binding.inputEmail.text.toString().trim()

//            Kondisi Mengecek kolom Email apabila kosong
            if (email.isEmpty()) {
                showErrorSnackbar("Email Tidak Boleh Kosong", android.R.drawable.ic_dialog_alert)
                binding.inputEmail.requestFocus()
                return@setOnClickListener

//          Kondisi Mengecek kolom Email apabila tidak menginputkan @
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showErrorSnackbar("Email tidak Valid", android.R.drawable.ic_dialog_alert)
                binding.inputEmail.requestFocus()
                return@setOnClickListener

            } else {
//                Kondisi dimana Koondisi diatas tidak terpenuhi maka kondisi dibawa yang di eksekusi
               fAuth.sendPasswordResetEmail(email).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showErrorSnackbar("Berhasil reset passsword, Cek Email!!!", android.R.drawable.ic_dialog_alert)
                        Intent(this, MasukActivity::class.java).also {
                            it.flags = Intent.FLAG_ACTIVITY_TASK_ON_HOME or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(it)
                        }
                    } else {
//                        Pesan apabila kondisi diatas tidak terpenuhi
                        showErrorSnackbar("Terjadi kesalahan dalama mereset password !!!", android.R.drawable.ic_dialog_alert)
                    }
                }
            }
        }

    }

    private fun showErrorSnackbar(message: String, iconResId: Int) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
        val snackbarView = snackbar.view
        val drawable = ContextCompat.getDrawable(this, iconResId) // Mendapatkan Drawable dari resource
        drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        snackbarView.setPadding(0, 0, 0, 0)

        val textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
        textView.compoundDrawablePadding = resources.getDimensionPixelOffset(R.dimen.snackbar_icon_text_spacing)
        textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null) // Menambahkan ikon ke Snackbar
        textView.gravity = Gravity.CENTER_VERTICAL // Set gravity to center vertical
        snackbar.show()
    }
}