package com.example.jirafamily.DTO

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class UserItem @JvmOverloads constructor(
    var id: String? = null,
    var name: String = "",
    var lastName: String = "",
    var avatar: String = "",
    var nameOfFamily: String = "",
    var email: String = "",
    var inviteToken: String = "",
    var adminId: String = "",
    var isAdmin: Boolean = false
) : Serializable
