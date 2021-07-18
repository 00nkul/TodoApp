package com.example.todo

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.TextView

class dialouge(context: Context, title:String,desc:String) : Dialog(context) {

    init {
        setCancelable(true)
    }
    val title = title
    val desc = desc
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.diaouge)

        val tvHead :TextView = findViewById(R.id.tvTitle)
        val tvDesc :TextView = findViewById(R.id.tvDesc)

        tvHead.text = title
        tvDesc.text = desc

    }
}