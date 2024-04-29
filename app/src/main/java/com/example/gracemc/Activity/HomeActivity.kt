package com.example.gracemc.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gracemc.Adapter.NotesAdapter
import com.example.gracemc.Model.ModelNotes
import com.example.gracemc.databinding.ActivityHomeBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var noteRV: RecyclerView
    private lateinit var noteList: ArrayList<ModelNotes>
    private lateinit var dbRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        binding.newNotes.setOnClickListener {
            val intent = Intent(this, TambahActivity::class.java)
            startActivity(intent)
        }

        binding.exit.setOnClickListener {
            signOutUser()
        }

        noteRV = binding.tvRV
        noteRV.layoutManager = LinearLayoutManager(this)
        noteRV.setHasFixedSize(true)

        noteList = arrayListOf<ModelNotes>()
        getNoteData()

    }

    private fun getNoteData() {
        noteRV.visibility = View.GONE

//        pendeklarasian firebase database
        dbRef = FirebaseDatabase.getInstance().getReference("Notes")

        dbRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                noteList.clear()
                if(snapshot.exists()){
                    for(noteSnap in snapshot.children){
                        val noteData = noteSnap.getValue(ModelNotes::class.java)
                        noteList.add(noteData!!)
                    }
                    val nAdapter = NotesAdapter(noteList)
                    noteRV.adapter = nAdapter

//                    itemnya di klik
                    nAdapter.setOnItemClickListener(object : NotesAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {
                            val intent = Intent(this@HomeActivity,NoteDetailActivity::class.java)

//                            data dari firebase database
                            intent.putExtra("nodeId", noteList[position].nodeId)
                            intent.putExtra("noteJudul", noteList[position].noteJudul)
                            intent.putExtra("noteNotes", noteList[position].noteNotes)
                            intent.putExtra("noteDate", noteList[position].noteDate)
                            startActivity(intent)
                        }
                    })

                    noteRV.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

//    function keluar
    private fun signOutUser() {
        val snackbar = Snackbar.make(binding.root, "Anda akan logout.", Snackbar.LENGTH_LONG)
        snackbar.setAction("Logout") {
            mAuth.signOut()
            navigateToLoginPage()
        }
        snackbar.show()
    }

//    mendeteksi apabila aktivitas login
    private fun navigateToLoginPage() {
        val intent = Intent(this, MasukActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
