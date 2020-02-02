package com.kraktun.kbot.objects

data class UserK(val id: Int, var username: String? = "", var status: Status, var userInfo: String? = "") {

    override fun equals(other: Any?): Boolean {
        return other is UserK && id == other.id
    }
}