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
        printlnK(TAG, "DB is stored in: $dbLink")
        Database.connect("jdbc:sqlite:$dbLink", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE // Or Connection.TRANSACTION_READ_UNCOMMITTED

        transaction {
            addLogger(BotLoggerK)
            SchemaUtils.create(Users, Groups, GroupUsers)
        }
    }

    /*
    USER MANAGEMENT
     */

    /**
     * Insert single user in DB
     */
    fun addUser(user : User, userStatus : Status, info : String? = null) {
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
     * Insert list of users in DB. If already present, update its status.
     */
    fun addUser(list : List<UserK>) {
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
                    try { //Does not use addUser, so that we use a single transaction
                        Users.insert {
                            it[id] = user.id
                            it[username] = user.username
                            it[status] = user.status.name
                            it[statusInfo] = user.userInfo
                        }
                    } catch (ee : Exception) { //If user is already present, update its status
                        Users.update ({Users.id eq user.id}){
                            it[status] = user.status.name
                        }
                    }
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

    /*
    GROUP MANAGEMENT
     */

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
    fun addGroup(idd: Long) {
        transaction {
            Groups.insert {
                it[id] = idd
                it[status] = GroupStatus.NORMAL.name
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
     * Update status of a group
     */
    fun updateGroup(groupId : Long, newStatus: GroupStatus) {
        transaction {
            Groups.update ({Groups.id eq groupId}) {
                it[status] = newStatus.name
            }
        }
    }

    /*
    GROUP USER MANAGEMENT
     */

    /**
     * Add user to group with defined status
     */
    fun addGroupUser(groupId : Long, userId : Int, statusK : Status) {
        transaction {
            try {
                GroupUsers.insert {
                    it[group] = groupId
                    it[user] = userId
                    it[status] = statusK.name
                }
            } catch (e: Exception) {
                updateGroupUser(groupId, userId, statusK)
            }
        }
    }

    /**
     * Add admins to group
     * If already present, update status
     */
    fun addGroupAdmins(groupId : Long, admins : List<Int>) {
        try {
            transaction {
                GroupUsers.batchInsert(admins) { userId ->
                    this[GroupUsers.group] = groupId
                    this[GroupUsers.user] = userId
                    this[GroupUsers.status] = Status.ADMIN.name
                }
            }
        } catch (e: Exception) { //if a user in the list is already present, add one at a time
            transaction {
                for (userId in admins) { //Does not use addGroupUser, so that we use a single transaction
                    try {
                        GroupUsers.insert {
                            it[group] = groupId
                            it[user] = userId
                            it[status] = Status.ADMIN.name
                        }
                    } catch (ee : Exception) {
                        GroupUsers.update({GroupUsers.group eq groupId and (GroupUsers.user eq userId)}) {
                            it[status] = Status.ADMIN.name
                        }
                    }
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
     * Update status for user in group
     */
    fun updateGroupUser(groupId: Long, userId: Int, newStatus : Status) {
        transaction {
            GroupUsers.update({GroupUsers.group eq groupId and (GroupUsers.user eq userId)}) {
                it[status] = newStatus.name
            }
        }
    }
}