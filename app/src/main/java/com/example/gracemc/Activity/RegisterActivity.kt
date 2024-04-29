package com.example.gracemc.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.gracemc.R
import com.example.gracemc.databinding.ActivityRegisterBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var rAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rAuth = FirebaseAuth.getInstance()

        binding.login.setOnClickListener {
            val intent = Intent(this, MasukActivity::class.java)
            startActivity(intent)
        }

        binding.btnRegister.setOnClickListener {
            val email: String = binding.rEmail.text.toString().trim()
            val pass: String = binding.rPass.text.toString().trim()
            val confirm: String = binding.rConfirm.text.toString().trim()

            if (email.isEmpty()) {
                showErrorSnackbar("Silahkan mengisi alamat email", android.R.drawable.ic_dialog_alert)
                binding.rEmail.requestFocus()
                return@setOnClickListener
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showErrorSnackbar("Masukkan Alamat Email yang valid", android.R.drawable.ic_dialog_alert)
                binding.rEmail.requestFocus()
                return@setOnClickListener
            }

            if (pass.isEmpty() || pass.length < 8) {
                showErrorSnackbar("Maksimal 8 karakter dan Tidak boleh kosong", android.R.drawable.ic_dialog_alert)
                binding.rPass.requestFocus()
                return@setOnClickListener
            }else if (pass != confirm){
                showErrorSnackbar("Password tidak sama !!!", android.R.drawable.ic_dialog_alert)
                binding.rConfirm.requestFocus()
                return@setOnClickListener
            }
            registerUser(email, pass)
        }
    }

    private fun registerUser(email: String, pass: String) {
        rAuth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result
                    if (result != null && result.signInMethods != null && result.signInMethods!!.isNotEmpty()) {
                        showErrorSnackbar("Alamat email sudah terdaftar.", android.R.drawable.ic_dialog_alert)
                    } else {
                        rAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { registrationTask ->
                            if (registrationTask.isSuccessful) {
                                Toast.makeText(applicationContext, "Berhasil Mendaftar, Silahkan Login !!!", Toast.LENGTH_SHORT).show()
                                Intent(this, MasukActivity::class.java).also{
                                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(it)
                                }
                                finish()
                            } else {
                                val errorMessage = registrationTask.exception?.message ?: "Terjadi kesalahan saat mendaftar"
                                showErrorSnackbar(errorMessage, android.R.drawable.ic_dialog_alert)
                            }
                        }
                    }
                } else {
                    // Terjadi kesalahan saat memeriksa email
                    showErrorSnackbar("Terjadi kesalahan saat memeriksa email.", android.R.drawable.ic_dialog_alert)
                }
            }
    }


    private fun showErrorSnackbar(message: String, iconResId: Int) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
        val snackbarView = snackbar.view
        val drawable = ContextCompat.getDrawable(this, iconResId)
        drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        snackbarView.setPadding(0, 0, 0, 0)

        val textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
        textView.compoundDrawablePadding = resources.getDimensionPixelOffset(R.dimen.snackbar_icon_text_spacing)
        textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        textView.gravity = Gravity.CENTER_VERTICAL
        snackbar.show()
    }
}