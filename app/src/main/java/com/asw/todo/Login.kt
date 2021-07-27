package com.asw.todo

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()

        val sharedPreferences = getSharedPreferences("Data", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val etPass:EditText = findViewById(R.id.etPass)
        val etMail:EditText = findViewById(R.id.etEmail)
        val btnSignIn:TextView = findViewById(R.id.btnSignIn)
        val btnSignUp:Button = findViewById(R.id.btnSignUp)
        val btnForgetPass:Button = findViewById(R.id.btnForgetPass)
        val tvForgetPassword:TextView = findViewById(R.id.tvForget)
        val tv:TextView = findViewById(R.id.tV)
        val tv1:TextView = findViewById(R.id.tV1)
        val tvOr:TextView = findViewById(R.id.tvOr)

        btnSignIn.setOnClickListener {
            val mail = etMail.text.toString().trim()
            val pass = etPass.text.toString().trim()

            auth.signInWithEmailAndPassword(mail,pass)
                .addOnCompleteListener(this){task->

                    if (task.isSuccessful){
                        Toast.makeText(this,"Sign in success", Toast.LENGTH_LONG).show()
                        editor.putBoolean("SignedIn",true)
                        editor.apply()
                        startActivity(Intent(this,MainActivity::class.java))
                    }
                    else{
                        Toast.makeText(this,"Invalid credentials", Toast.LENGTH_LONG).show()
                    }

                }
        }

        btnSignUp.setOnClickListener {
                startActivity(Intent(this,Register::class.java))
        }

        tvForgetPassword.setOnClickListener {
            btnForgetPass.visibility = VISIBLE
            btnSignIn.visibility = INVISIBLE
            btnSignUp.visibility = INVISIBLE
            etPass.visibility = INVISIBLE
            tv.visibility = INVISIBLE
            tv1.visibility = INVISIBLE
            tvOr.visibility = INVISIBLE
        }

        btnForgetPass.setOnClickListener {
            val mail = etMail.text.toString().trim()
            if (mail == "") {
                Toast.makeText(this, "Enter Mail ", Toast.LENGTH_LONG).show()
            } else {
                auth.sendPasswordResetEmail(mail)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this,"Link to Reset has been sent to Your Mail",Toast.LENGTH_LONG).show()
                            btnForgetPass.visibility = INVISIBLE
                            btnSignIn.visibility = VISIBLE
                            btnSignUp.visibility = VISIBLE
                            etPass.visibility = VISIBLE
                            tv.visibility = VISIBLE
                            tv1.visibility = VISIBLE
                            tvOr.visibility = VISIBLE

                        } else {
                            Toast.makeText(this, "Not valid Mail", Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }
    }

    override fun onBackPressed() {
       //Do Nothing
        finishAffinity()
    }

}