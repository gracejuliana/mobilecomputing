package com.example.gracemc.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.gracemc.Model.ModelNotes
import com.example.gracemc.R
import com.example.gracemc.databinding.ActivityTambahBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TambahActivity : AppCompatActivity() {

    private  lateinit var binding : ActivityTambahBinding
    private  lateinit var dbNotes : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbNotes = FirebaseDatabase.getInstance().getReference("Notes")

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        binding.btnSimpan.setOnClickListener {
            saveNodeData()
        }
    }

    private fun saveNodeData() {
        val judul = binding.inputJudul.text.toString()
        val notes = binding.inputNotes.text.toString()

        if (judul.isEmpty()){
            showErrorSnackbar("Judul Tidak Boleh Kosong", android.R.drawable.ic_dialog_alert)
            binding.inputJudul.requestFocus()
            return
        }
        if (notes.isEmpty()){
            showErrorSnackbar("Silahkan masukkan Notes anda !!!", android.R.drawable.ic_dialog_alert)
            binding.inputJudul.requestFocus()
            return
        }

        val noteId = dbNotes.push().key!!
        val currentDate = getCurrentDateString()

        val Notes = ModelNotes(noteId, judul, notes, currentDate)

        dbNotes.child(noteId).setValue(Notes)
            .addOnCompleteListener {
                Toast.makeText(applicationContext, "Berhasil Membuat Notes", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }.addOnFailureListener {
                showErrorSnackbar("Gagal Membuat Notes", android.R.drawable.ic_dialog_alert)
            }
    }

    private fun getCurrentDateString(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy, HH:mm", Locale.getDefault())
        return dateFormat.format(calendar.time)
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