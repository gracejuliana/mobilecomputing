package com.example.gracemc.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gracemc.Model.ModelNotes
import com.example.gracemc.R
import java.text.SimpleDateFormat

class NotesAdapter (private val noteList : ArrayList<ModelNotes>) :
    RecyclerView.Adapter<NotesAdapter.ViewHolder>(){

    private lateinit var nListerner : onItemClickListener

    interface  onItemClickListener{
        fun onItemClick(position: Int)
    }

//    itemnya di klik
    fun setOnItemClickListener(clickListener: onItemClickListener){
        nListerner = clickListener
    }

//    membuat viewholder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.notes,parent,false)
        return ViewHolder(itemView, nListerner)
    }

//    view holder menampilkan data
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val currentNote = noteList[position]
        holder.tvdate.text = currentNote.noteDate
        holder.tvjudul.text = currentNote.noteJudul
    }
    override fun getItemCount(): Int {
        return noteList.size
    }

    class ViewHolder(itemView: View, clickListener: onItemClickListener) : RecyclerView.ViewHolder(itemView){
//        item yang di tampilkan pada recyclerView
        val tvjudul : TextView = itemView.findViewById(R.id.tvJudul)
        val tvdate : TextView = itemView.findViewById(R.id.tvDate)

        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(adapterPosition)
            }
        }
    }
}