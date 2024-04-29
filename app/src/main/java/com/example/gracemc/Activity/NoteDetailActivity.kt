package com.example.gracemc.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.gracemc.Model.ModelNotes
import com.example.gracemc.R
import com.example.gracemc.R.layout.update_dialog
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class NoteDetailActivity : AppCompatActivity() {

    private lateinit var back: LinearLayout
    private lateinit var delete: LinearLayout
    private lateinit var edit: LinearLayout
    private lateinit var tvDjudul: TextView
    private lateinit var tvDnote: TextView
    private lateinit var tvDdate: TextView
    private lateinit var tvId : TextView
    private lateinit var dbRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_detail)

        initView()
        setValuesToView()

        back.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        edit.setOnClickListener {
            val nodeId = intent.getStringExtra("nodeId").toString()
            openUpdateDialog(
                nodeId
            )
        }

         delete.setOnClickListener {
            val nodeId = intent.getStringExtra("nodeId").toString()
            deleteRecord(nodeId)
        }

    }

    private fun deleteRecord(id: String) {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Hapus Notes")
            .setMessage("Apakah Anda yakin ingin menghapus Notes ini?")
            .setPositiveButton("Hapus") { _, _ ->
                val dbRef = FirebaseDatabase.getInstance().getReference("Notes").child(id)
                val mTask = dbRef.removeValue()

                mTask.addOnSuccessListener {
                    Toast.makeText(applicationContext, "Notes berhasil dihapus", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, HomeActivity::class.java)
                    finish()
                    startActivity(intent)
                }.addOnFailureListener { error ->
                    Toast.makeText(applicationContext, "Gagal menghapus Notes: ${error.message}", Toast.LENGTH_LONG).show()
                }
            }
            .setNegativeButton("Batal", null)
            .create()

        alertDialog.show()
    }

    private fun initView() {
        tvDjudul = findViewById(R.id.tvDJudul)
        tvDnote = findViewById(R.id.tvDNotes)
        tvDdate = findViewById(R.id.tvDdate)
        tvId = findViewById(R.id.tvDid)

        back = findViewById(R.id.Dback)
        edit = findViewById(R.id.Dedit)
        delete = findViewById(R.id.Dtrash)

    }

    private fun setValuesToView() {
        tvId.text = intent.getStringExtra("nodeId")
        tvId.visibility = View.GONE
        tvDjudul.text = intent.getStringExtra("noteJudul")
        tvDnote.text = intent.getStringExtra("noteNotes")
        tvDdate.text = intent.getStringExtra("noteDate")
    }

    private fun openUpdateDialog(nodeId: String) {
        val noteDialog = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val noteDialogView = inflater.inflate(update_dialog, null)

        noteDialog.setView(noteDialogView)

        val eInputJudul = noteDialogView.findViewById<EditText>(R.id.editJudul)
        val eInputNote = noteDialogView.findViewById<EditText>(R.id.editNotes)
        val btnUpdate = noteDialogView.findViewById<Button>(R.id.btnUpdate)

        eInputJudul.setText(tvDjudul.text.toString())
        eInputNote.setText(tvDnote.text.toString())

        val alertDialog = noteDialog.create()
        alertDialog.show()

        btnUpdate.setOnClickListener {
            val currentDate = getCurrentDateString()
            val newJudul = eInputJudul.text.toString()
            val newNote = eInputNote.text.toString()

            UpdateNoteData(nodeId, newJudul, newNote, currentDate) { isSuccess ->
                if (isSuccess) {
                    tvDjudul.text = newJudul
                    tvDnote.text = newNote
                    tvDdate.text = currentDate
                    Toast.makeText(applicationContext, "Berhasil Mengupdate Notes", Toast.LENGTH_LONG).show()
                    alertDialog.dismiss()
                } else {
                    alertDialog.dismiss()
                    Toast.makeText(applicationContext, "Gagal mengupdate data", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun UpdateNoteData(
        id: String,
        judul: String,
        notes: String,
        date: String,
        callback: (Boolean) -> Unit
    ) {
        dbRef = FirebaseDatabase.getInstance().getReference("Notes").child(id)
        val noteInfo = ModelNotes(id, judul, notes, date)
        dbRef.setValue(noteInfo)
            .addOnSuccessListener {
                callback(true)
                Toast.makeText(applicationContext, "Berhasil Mengupdate Notes", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                callback(false)
                Toast.makeText(applicationContext, "Gagal mengupdate data: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun getCurrentDateString(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy, HH:mm", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}
