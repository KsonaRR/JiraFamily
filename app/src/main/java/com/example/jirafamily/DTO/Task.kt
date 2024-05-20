package com.example.jirafamily.DTO

data class Task(
    val title: String,
    val description: String,
    val avatarUrl: String?,
    val attachmentUrl: String?,
    val status: String,
    val priority: String
)

