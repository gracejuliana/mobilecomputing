package com.example.gracemc.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.gracemc.R
import com.example.gracemc.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class MasukActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var lAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lAuth = FirebaseAuth.getInstance()



        binding.register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.forget.setOnClickListener {
            val intent = Intent(this, ForgetActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            val email: String = binding.inputEmail.text.toString().trim()
            val pass: String = binding.inputPass.text.toString().trim()

            if (email.isEmpty()) {
                showErrorSnackbar("Email Tidak Boleh Kosong", android.R.drawable.ic_dialog_alert)
                binding.inputPass.requestFocus()
                return@setOnClickListener
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showErrorSnackbar("Email Tidak Valid", android.R.drawable.ic_dialog_alert)
                binding.inputEmail.requestFocus()
                return@setOnClickListener
            } else if (pass.isEmpty() || pass.length < 8) {
                showErrorSnackbar("Maksimal 8 karakter dan Tidak boleh kosong", android.R.drawable.ic_dialog_alert)
                binding.inputPass.requestFocus()
                return@setOnClickListener
            } else {
                loginUser(email, pass)
            }
        }
    }

    //  Function untuk menampilkan Snackbar dengan pesan kustom dan ikon
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

    //  Function untuk melakukan Login dan melakukan pengecekan pada database firebase
    private fun loginUser(email: String, pass: String) {
        lAuth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(applicationContext, "Selamat Datang", Toast.LENGTH_SHORT).show()
                    Intent(this, HomeActivity::class.java).also{
                        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(it)
                    }
                } else {
                    val exception = task.exception
                    if (exception is FirebaseAuthInvalidUserException || exception is FirebaseAuthInvalidCredentialsException) {
                        showErrorSnackbar("Email atau kata sandi salah !!!", android.R.drawable.ic_dialog_alert)
                    } else if (exception is FirebaseAuthInvalidCredentialsException) {
                        showErrorSnackbar("Registrasi terlebih dahulu !!!", android.R.drawable.ic_dialog_alert)
                    } else {
                        showErrorSnackbar("Terjadi kesalahan saat login !!!", android.R.drawable.ic_dialog_alert)
                    }
                }
            }
    }
}
