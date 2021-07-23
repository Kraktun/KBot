package com.kraktun.kbot.data

import com.kraktun.kbot.objects.GroupStatus
import com.kraktun.kbot.objects.Status
import com.kraktun.kbot.utils.isGroupOrSuper
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User

interface DataManager {

    /**
     * Returns status of user according to the passed chat
     */
    fun getUserStatus(user: User?, chat: Chat): Status {
        return when {
            user == null -> Status.NOT_REGISTERED
            chat.isUserChat -> getUserStatus(user.id)
            chat.isGroupOrSuper() -> getGroupUserStatus(groupId = chat.id, userId = user.id)
            else -> Status.NOT_REGISTERED
        }
    }

    /**
     * Get user from DB
     */
    fun getUserStatus(userId: Long): Status

    /**
     * Get user status in a group
     */
    fun getGroupUserStatus(groupId: Long, userId: Long): Status

    /**
     * Get status of a group
     */
    fun getGroupStatus(groupId: Long): GroupStatus
}
