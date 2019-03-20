package com.miche.krak.kBot.objects

data class UserK (val id : Int, var username : String = "") {

    override fun equals(other : Any?) : Boolean {
        return other is UserK && id == other.id
    }
}