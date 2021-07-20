package com.example.todo

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.math.log

class CustomAdapter (val data:ArrayList<toDoData>):RecyclerView.Adapter<CustomAdapter.ViewHolder>(){

    //Item deletion
    fun deleteItem(i:Int){
        data.removeAt(i)
        notifyDataSetChanged()
    }

    //Mark as Done

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //Inflate ViewHolder
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_todo_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvHead.text =  data[position].todoTitle
        holder.tvDescription.text =  data[position].todoContent

        //Mark as Done
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                holder.tvHead.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                holder.tvDescription.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                //holder.tvHead.setTextColor(Color.GREEN)
//                holder.linear1.setBackgroundColor(Color.parseColor("#F2222222"))
//                holder.linera2.setBackgroundColor(Color.parseColor("#F2222222"))

                holder.card.setCardBackgroundColor(Color.parseColor("#1a3582"))
                holder.tvHead.setTextColor(Color.parseColor("#c9c9c9"))
                holder.tvDescription.setTextColor(Color.parseColor("#c9c9c9"))


            }else{
                holder.tvHead.paintFlags = 0
                holder.tvDescription.paintFlags = 0
                holder.card.setCardBackgroundColor(Color.parseColor("#041955"))

            }

        }

        holder.itemView.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {

                //Toast.makeText(v!!.context,"CLicked",Toast.LENGTH_LONG).show()
//                val main = MainActivity()
//                val builder = AlertDialog.Builder(v.context)
//                val inflater = main.layoutInflater
//                builder.setView(inflater.inflate(R.layout.diaouge,null))
//                builder.create()
                dialouge(v!!.context,data[position].todoTitle,data[position].todoContent).show()

                //Log.d("ClickedOn",45.toString())

            }
        })
    }

    override fun getItemCount(): Int {
        return  data.size
    }

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {

        //Get reference to the detailed view here
        val tvHead :TextView = itemView.findViewById(R.id.tvHead)
        val tvDescription :TextView = itemView.findViewById(R.id.tvDescription)
        val checkBox:CheckBox = itemView.findViewById(R.id.checkBox)
        val card:CardView  =itemView.findViewById(R.id.cardView)

    }


}