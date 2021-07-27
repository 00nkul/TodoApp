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
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class create_note : AppCompatActivity() {

    val CHANNEL_ID = "chanel_id"
    val channel_name = "channel_name"
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)

        createNotificationChannel()
        supportActionBar?.hide()

        val btnAdd :Button = findViewById(R.id.btnAdd)
        val etHead:EditText = findViewById(R.id.etHead)
        val etDescription:EditText = findViewById(R.id.etDescription)
        val ivBack:ImageView = findViewById(R.id.ivBack)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        var id:Int = intent.getIntExtra("id" , 1)
        val database = Firebase.database
        val myref = database.reference.child("NewToDo")

        btnAdd.setOnClickListener {

            val head :String= etHead.text.toString()
            val desc :String= etDescription.text.toString()
            val dataObj = toDoData(head,desc,"low")


            val newitem = myref.child("${currentUser?.uid}").push()
            newitem.setValue(dataObj)

            //database.child("users").child(userId).child("username").setValue(name) while edit


            //Create notification
            NotificationManagerCompat.from(this).notify(0,createNotification("Task Added ","Tap here to view tasks").build())
            val intent2 = Intent(this ,MainActivity::class.java)
            id++;
            intent2.putExtra("id",id)
            startActivity(intent2)
            Log.d("valueOfIdinCreateNote", id.toString())
        }

        ivBack.setOnClickListener {
                super.onBackPressed()
        }
    }

    private  fun createNotification(title:String,text:String): NotificationCompat.Builder {

        val fullScreenIntent = Intent(this, MainActivity::class.java)
        val fullScreenPendingIntent = PendingIntent.getActivity(this, 0,
                fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        return NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setFullScreenIntent(fullScreenPendingIntent,true)
                .setAutoCancel(true)
    }
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = channel_name
            //val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            // Register the channel with the system
            val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}