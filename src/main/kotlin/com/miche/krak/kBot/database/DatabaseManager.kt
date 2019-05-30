package com.miche.krak.kBot.database

import com.miche.krak.kBot.objects.GroupK
import com.miche.krak.kBot.objects.UserK
import com.miche.krak.kBot.objects.GroupStatus
import com.miche.krak.kBot.objects.Status
import com.miche.krak.kBot.utils.getMainFolder
import com.miche.krak.kBot.utils.printlnK
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.telegram.telegrambots.meta.api.objects.User
import java.sql.Connection

private const val TAG = "DATABASE_MANAGER"

object DatabaseManager {

    init {
        connectDB()
    }

    private fun connectDB() {
        val dbLink = getMainFolder() + "\\KBotDB.db"
        printlnK(TAG, "DB is stored in: ")
        printlnK(TAG, dbLink)
        Database.connect("jdbc:sqlite:$dbLink", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE // Or Connection.TRANSACTION_READ_UNCOMMITTED

        transaction {
            addLogger(BotLoggerK)
            SchemaUtils.create(Users, Groups, GroupUsers)
        }
    }

    /**
     * Insert single user in DB
     */
    fun insertUser(user : User, userStatus : Status, info : String? = null) {
        transaction {
            Users.insert {
                it[id] = user.id
                it[username] = user.userName
                it[status] = userStatus.name
                it[statusInfo] = info
            }
        }
    }

    /**
     * Insert list of users in DB
     */
    fun insertUser(list : List<UserK>) {
        try {
            transaction {
                Users.batchInsert(list) { user ->
                    this[Users.id] = user.id
                    this[Users.username] = user.username
                    this[Users.status] = user.status.name
                    this[Users.statusInfo] = user.userInfo
                }
            }
        } catch (e: Exception) { //if a user in the list is already present, add one at a time
            transaction {
                for (user in list) {
                    try {
                        Users.insert {
                            it[id] = user.id
                            it[username] = user.username
                            it[status] = user.status.name
                            it[statusInfo] = user.userInfo
                        }
                    } catch (ee : Exception) {}
                }
            }
        }
    }

    /**
     * Get user from DB
     */
    fun getUser(userId : Int) : UserK? {
        var userK : UserK? = null
        transaction {
            Users.select {Users.id eq userId}
                .map {
                    userK = UserK(id = userId,
                        status = Status.valueOf(it[Users.status].toUpperCase()),
                        username = it[Users.username],
                        userInfo = it[Users.statusInfo])
                     }
        }
        return userK
    }

    /**
     * Return true if group exists inside the DB
     */
    fun groupExists(groupId: Long) : Boolean {
        var result = false
        transaction {
            result = Groups.select {Groups.id eq groupId}.count() > 0
        }
        return result
    }

    /**
     * Insert group in DB
     */
    fun insertGroup(idd: Long) {
        transaction {
            Groups.insert {
                it[id] = idd
                it[status] = GroupStatus.NORMAL.toString()
            }
        }
    }

    /**
     * Get group and all the members with a custom status
     */
    fun getGroup(groupId : Long) : GroupK? {
        var groupK : GroupK? = null
        val users : ArrayList<UserK> = arrayListOf()
        transaction {
            GroupUsers.select {GroupUsers.group eq groupId}
                .forEach {
                    users.add(UserK(id = it[GroupUsers.user],
                        status = Status.valueOf(it[GroupUsers.status].toUpperCase()))
                    )
                }
        }
        if (users.size > 0)
            groupK = GroupK(id = groupId, users = users)
        return groupK
    }

    /**
     * Add user to group with defined status
     */
    fun addGroupUser(groupId : Long, userId : Int, statusK : Status) {
        transaction {
            GroupUsers.insert {
                it[group] = groupId
                it[user] = userId
                it[status] = statusK.name
            }
        }
    }

    /**
     * Add admins to group
     * Exceptions are captured
     */
    fun addGroupAdmins(groupId : Long, admins : List<Int>) {
        try {
            transaction {
                GroupUsers.batchInsert(admins) { userId ->
                    this[GroupUsers.group] = groupId
                    this[GroupUsers.user] = userId
                    this[GroupUsers.status] = Status.ADMIN.toString()
                }
            }
        } catch (e: Exception) { //if a user in the list is already present, add one at a time
            transaction {
                for (userId in admins) {
                    try {
                        GroupUsers.insert {
                            it[group] = groupId
                            it[user] = userId
                            it[status] = Status.ADMIN.toString()
                        }
                    } catch (ee : Exception) {}
                }
            }
        }
    }

    /**
     * Get user status in a group
     */
    fun getGroupUserStatus(groupId : Long, userId : Int) : Status {
        var statusK : Status = Status.NOT_REGISTERED
        transaction {
            GroupUsers.select {GroupUsers.group eq groupId and (GroupUsers.user eq userId)}
                .map {
                    statusK = Status.valueOf(it[GroupUsers.status].toUpperCase())
                }
        }
        return statusK
    }

    /**
     * Get status of group
     */
    fun getGroupStatus(groupId : Long) : GroupStatus {
        var statusK : GroupStatus = GroupStatus.NORMAL
        transaction {
            Groups.select {Groups.id eq groupId}
                .map {
                    statusK = GroupStatus.valueOf(it[Groups.status].toUpperCase())
                }
        }
        return statusK
    }

    /**
     * Update status of a group
     */
    fun updateGroupStatus(groupId : Long, statusK: GroupStatus) {
        transaction {
            Groups.update ({Groups.id eq groupId}) {
                    it[status] = statusK.name
            }
        }
    }
}