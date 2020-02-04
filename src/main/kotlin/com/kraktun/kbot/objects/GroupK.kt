package com.kraktun.kbot.objects

data class GroupK(val id: Long, val users: List<UserK>, val status: GroupStatus = GroupStatus.NORMAL) {

    override fun equals(other: Any?): Boolean {
        return other is GroupK && id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
