package com.miche.krak.kBot.objects

import com.miche.krak.kBot.utils.GroupStatus

data class GroupK (val id : Long, val users : List<UserK>, val status : GroupStatus = GroupStatus.NORMAL) {

    override fun equals(other : Any?) : Boolean {
        return other is GroupK && id == other.id
    }
}