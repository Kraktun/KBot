package com.kraktun.kbot.objects

data class UserK(val id: Long, var username: String? = "", var status: Status, var userInfo: String? = "") {

    override fun equals(other: Any?): Boolean {
        return other is UserK && id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
