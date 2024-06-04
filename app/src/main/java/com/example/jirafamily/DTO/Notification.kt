package com.example.jirafamily.DTO

data class Notification(

    val Notification: String = "",
    val nameOfFamily: String = "",

) {
    constructor() : this("", "")
}

