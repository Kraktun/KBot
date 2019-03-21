package com.miche.krak.kBot.objects

import com.miche.krak.kBot.utils.Status

data class UserK (val id : Int, var username : String? = "", var status : Status, var userInfo : String? = "") {

    override fun equals(other : Any?) : Boolean {
        return other is UserK && id == other.id
    }
}