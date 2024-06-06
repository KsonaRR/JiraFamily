package com.example.jirafamily.DTO

data class Task(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val avatarUrl: String? = null,
    val status: Int = 0,
    val priority: Int?,
    val nameOfFamily: String,
    val attachments: String
) {
    constructor() : this("","", "", null, 0, null, "", "")
}

