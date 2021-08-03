package com.asw.todo


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
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

    val CHANNEL_ID = "Channel_ID"
    val channel_name = "channel_name"
    lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()
        supportActionBar?.hide()

        var id = 1;
        val addBtn:FloatingActionButton = findViewById(R.id.addBtn)
        val btnLogOut:Button = findViewById(R.id.btnLogOut)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        //LayoutManager for the recylcerView
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        //Shared preference
        val sharedPreferences = getSharedPreferences("Data", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        if (! sharedPreferences.getBoolean("SignedIn",false)){
            startActivity(Intent(this,Login::class.java))
        }

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        val database = Firebase.database

        try {
            database.setPersistenceEnabled(true)
        }
        catch (e: RuntimeException) { }




        val myref = database.getReference("NewToDo").child("${currentUser?.uid}")
        val keys  = arrayListOf<String>()
        val data = ArrayList<toDoData>()


        //Function for reading data from fireBase
        myref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                data.removeAll(data)

                for (snap in snapshot.children) {

                    //Cast data from database to data class
                    val todo = snap.getValue(toDoData::class.java) as toDoData

                    //Adding data from data base to list (Data)
                    data.add(todo)

                    //Adding keys of the child to arraylist of keys
                    keys.add(snap.key.toString())
                    val adapter = CustomAdapter(data)
                    recyclerView.adapter = adapter
                }


            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Unable to load", Toast.LENGTH_SHORT).show()
            }
        })



        val adapter = CustomAdapter(data)

        //Function for Delete from database
        fun deleteFromDatabase(id: Int) {
            myref.child(keys[id]).removeValue()
            NotificationManagerCompat.from(this).notify(0,createNotification("Task removed","Tap here to view tasks").build())
        }

        //Function for Swipe
        val swipeGesture = object :SwipeGesture(){

            //On Move
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
               val fromPosition = viewHolder.adapterPosition
               val toPosition = target.adapterPosition

                Collections.swap(data, fromPosition, toPosition)
                recyclerView.adapter?.notifyItemMoved(fromPosition, toPosition)

                return false
            }
            //On swiped
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

        //Logging out user
        btnLogOut.setOnClickListener {
            auth.signOut()

            editor.putBoolean("SignedIn",false)
            editor.apply()

            startActivity(Intent(this,Login::class.java))
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


        recyclerView.adapter = adapter


    }

    override fun onBackPressed() {
        this.finish()
        super.onBackPressed()
    }

    //Functions for Notifications
    private  fun createNotification(title:String,text:String): NotificationCompat.Builder {

        val fullScreenIntent = Intent(this, MainActivity::class.java)
        val fullScreenPendingIntent = PendingIntent.getActivity(this, 0,
                fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        return NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.mn)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setFullScreenIntent(fullScreenPendingIntent,true)
                .setAutoCancel(true)
    }
    //For Notification Channel
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = channel_name
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


}