package com.asw.todo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class Register : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()
        val etMail:TextInputEditText = findViewById(R.id.etRegisterMail)
        val etPass:TextInputEditText = findViewById(R.id.etRegisterPass)
        val register:Button = findViewById(R.id.btnRegister)

        register.setOnClickListener {
            val mail = etMail.text.toString().trim()
            val pass = etPass.text.toString().trim()

            if (pass.length < 6){
                etPass.error = "Password must be more than 6 digit "
            }
            else{
                auth.createUserWithEmailAndPassword(mail,pass)
                    .addOnCompleteListener(this){task->
                        if (task.isSuccessful){
                            Toast.makeText(this,"Registered SuccessFully ,Login Now!",Toast.LENGTH_LONG).show()
                            startActivity(Intent(this,Login::class.java))
                        }else{
                         Toast.makeText(this,"Unable To register",Toast.LENGTH_LONG).show()
                        }
                }
            }

        }

    }
}