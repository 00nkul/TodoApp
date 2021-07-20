package com.example.todo


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.lang.RuntimeException
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()


        var id = 1;
        val addBtn:FloatingActionButton = findViewById(R.id.addBtn)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        //LayoutManager for the recylcerView
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        val database = Firebase.database

        try {
            database.setPersistenceEnabled(true)
        }
        catch (e: RuntimeException) {
            Toast.makeText(this, "Unable to save note to FireBase", Toast.LENGTH_LONG).show()
        }
        val myref = database.getReference("toDo")

        val keys  = arrayListOf<String>()
        val data = ArrayList<toDoData>()


        //Function for reading data from fireBase
        myref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                data.removeAll(data)
                val value = snapshot.value
                //val todo :toDoData = value as toDoData
                for (snap in snapshot.children) {

                    //Cast data from database to data class
                    val todo = snap.getValue(toDoData::class.java) as toDoData

                    //Adding data from data base to list (Data)
                    data.add(todo)

                    //Adding keys of the child to arraylist of keys
                    keys.add(snap.key.toString())
                    val adapter = CustomAdapter(data)
                    recyclerView.adapter = adapter
                    //toDoData data = snap.getChildren(toDoData.class);
//                    val data = snap.value as toDoData
//                    Log.d("sNAP" , snap.toString())
//                    Log.d("toPrior" , todo.priority)
//                    Log.d("toTitle" , todo.todoTitle)
                    Log.d("toContent", snap.key.toString())
                }

                if (value != null) {
                    Log.d("valueofthevalue", value.toString())
                } else {
                    Toast.makeText(applicationContext, "Unable to load", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Unable to load", Toast.LENGTH_SHORT).show()
            }
        })

        val adapter = CustomAdapter(data)




        //Function for Delete
        fun deleteFromDatabase(id: Int) {
            val database2 = Firebase.database
            val refrence = database2.getReference("toDo")


                refrence.child(keys[id]).removeValue()
//            Log.d("delete$", keys[id])
//            Log.d("deleteditemf", keys[id])
//            Log.d("deleteFunLog", id.toString())

        }
        //Function for Swipe
        val swipeGesture = object :SwipeGesture(){

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
               val fromPosition = viewHolder.adapterPosition
               val toPosition = target.adapterPosition

                Collections.swap(data, fromPosition, toPosition)
                recyclerView.adapter?.notifyItemMoved(fromPosition, toPosition)

                return false
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when(direction){
                    ItemTouchHelper.LEFT -> {
                        data.removeAt(viewHolder.adapterPosition)
                        recyclerView.removeViewAt(viewHolder.adapterPosition)
                        adapter.notifyItemRemoved(viewHolder.adapterPosition)
                        adapter.notifyItemRangeChanged(viewHolder.adapterPosition, data.size)
                        adapter.notifyDataSetChanged()
                        deleteFromDatabase(viewHolder.adapterPosition)
                    }
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeGesture)
        itemTouchHelper.attachToRecyclerView(recyclerView)


        addBtn.setOnClickListener {
            val intent1:Intent = Intent(this, create_note::class.java)
            id = intent.getIntExtra("id", 1)
            intent1.putExtra("id", id)
            startActivity(intent1)
            Log.d("valueOfId", id.toString())
        }

        //Setup Date here
        val tvDate:TextView = findViewById(R.id.tvDate)
        val tvDayYear:TextView = findViewById(R.id.tvDayYear)
        val date: LocalDate = LocalDate.now()
        val month = date.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
        val day = date.dayOfMonth
        val year = date.year
        val dow = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)

        tvDate.text = day.toString()
        tvDayYear.text = "$dow \n$month ${year.toString()}"

        Log.d("datteIs", data.toString())
        Log.d("Monthis", month.toString())
        Log.d("dayis", day.toString())
        Log.d("yearIs", year.toString())
        Log.d("dayIs", dow)




        recyclerView.adapter = adapter



    }



}