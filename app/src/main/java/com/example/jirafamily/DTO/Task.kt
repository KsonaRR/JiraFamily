package com.example.jirafamily.DTO

data class Task(
    val title: String = "",
    val description: String = "",
    val avatarUrl: String? = null,
    val status: String = "",
    val priority: Int?
) {
    constructor() : this("", "", null, "", null)
}

