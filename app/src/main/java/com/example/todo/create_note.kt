package com.example.todo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class create_note : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)

        supportActionBar?.hide()

        val btnAdd :Button = findViewById(R.id.btnAdd)
        val etHead:EditText = findViewById(R.id.etHead)
        val etDescription:EditText = findViewById(R.id.etDescription)
        val ivBack:ImageView = findViewById(R.id.ivBack)

        var id:Int = intent.getIntExtra("id" , 1)
        val database = Firebase.database
        val myref = database.reference

        btnAdd.setOnClickListener {

            val head :String= etHead.text.toString()
            val desc :String= etDescription.text.toString()
            val dataObj = toDoData(head,desc,"low")

            //database.child("users").child(userId).setValue(user)

            //val newitem = myref.child("toDo").child("$id").push()
            val newitem = myref.child("toDo").push()
            newitem.setValue(dataObj)

            //database.child("users").child(userId).child("username").setValue(name) while edit

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


}