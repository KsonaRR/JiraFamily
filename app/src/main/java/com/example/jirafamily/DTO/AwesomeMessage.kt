package com.example.jirafamily.DTO

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class AwesomeMessage @JvmOverloads constructor(
    var name: String = "",
    var text: String = "",
    var imageUrl: String? = null
)
