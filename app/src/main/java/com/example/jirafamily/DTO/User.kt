package com.example.jirafamily.DTO

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User @JvmOverloads constructor(
    var name: String = "",
    var lastName: String = "",
    var avatar: String = "",
    var nameOfFamily: String = "",
    var email: String = "",
    var id: String? = null
)
