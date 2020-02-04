package com.kraktun.kbot.data

import com.kraktun.kbot.objects.GroupK
import com.kraktun.kbot.objects.GroupStatus
import com.kraktun.kbot.objects.Status
import com.kraktun.kbot.objects.UserK
import com.kraktun.kbot.utils.isGroupOrSuper
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User

interface DataManager {

    /**
     * Returns status of user according to the passed chat
     */
    fun getDBStatus(user: User?, chat: Chat): Status {
        return when {
            user == null -> Status.NOT_REGISTERED
            chat.isUserChat -> getUser(user.id)?.status ?: Status.NOT_REGISTERED
            chat.isGroupOrSuper() -> getGroupUserStatus(groupId = chat.id, userId = user.id)
            else -> Status.NOT_REGISTERED
        }
    }

    /*
    USER MANAGEMENT
     */

    /**
     * Insert single user in DB
     */
    fun addUser(user: User, userStatus: Status, info: String? = null)

    /**
     * Insert list of users in DB. If already present, update its status.
     */
    fun addUser(list: List<UserK>)

    /**
     * Get user from DB
     */
    fun getUser(userId: Int): UserK?

    /*
    GROUP MANAGEMENT
     */

    /**
     * Return true if group exists inside the DB
     */
    fun groupExists(groupId: Long): Boolean

    /**
     * Insert group in DB
     */
    fun addGroup(groupId: Long)

    /**
     * Get group and all the members with a custom status
     */
    fun getGroup(groupId: Long): GroupK?

    /**
     * Get group status
     */
    fun getGroupStatus(groupId: Long): GroupStatus

    /**
     * Update status of a group
     */
    fun updateGroup(groupId: Long, newStatus: GroupStatus)

    /*
    GROUP USER MANAGEMENT
     */

    /**
     * Add user to group with defined status
     */
    fun addGroupUser(groupId: Long, userId: Int, statusK: Status)

    /**
     * Add users to group with defined status
     * If already present, update status
     */
    fun addGroupUsers(groupId: Long, usersId: List<Int>, statusK: Status)

    /**
     * Get user status in a group
     */
    fun getGroupUserStatus(groupId: Long, userId: Int): Status

    /**
     * Update status for user in group
     */
    fun updateGroupUser(groupId: Long, userId: Int, newStatus: Status)

    /**
     * Update status of users in a group whose status match oldStatus
     */
    fun updateGroupUsersStatus(groupId: Long, oldStatus: Status, newStatus: Status)
}
