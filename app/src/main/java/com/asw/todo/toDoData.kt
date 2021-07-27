package com.asw.todo

data class toDoData(val todoTitle:String ,val todoContent:String , val priority:String){
    constructor():this("title","contnt",  "af"){}
    //constructor(contnt:String,priority:String):this("title",contnt,priority ){}
    //constructor(title:String,priority:String):this(title,"op",  priority){}
}
