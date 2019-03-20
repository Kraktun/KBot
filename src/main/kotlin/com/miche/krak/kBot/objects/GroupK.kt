package com.miche.krak.kBot.objects

data class GroupK (val id : Int) {

    override fun equals(other : Any?) : Boolean {
        return other is GroupK && id == other.id
    }
}