package com.miche.krak.kBot.database

import com.miche.krak.kBot.objects.*
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
            SchemaUtils.create(Users, Groups, GroupUsers, TrackedObjects)
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
     * Add users to group with defined status
     * If already present, update status
     */
    fun addGroupUsers(groupId : Long, usersId : List<Int>, statusK : Status) {
        try {
            transaction {
                GroupUsers.batchInsert(usersId) { userId ->
                    this[GroupUsers.group] = groupId
                    this[GroupUsers.user] = userId
                    this[GroupUsers.status] = statusK.name
                }
            }
        } catch (e: Exception) { //if a user in the list is already present, add one at a time
            transaction {
                for (userId in usersId) { //Does not use addGroupUser, so that we use a single transaction
                    try {
                        GroupUsers.insert {
                            it[group] = groupId
                            it[user] = userId
                            it[status] = statusK.name
                        }
                    } catch (ee : Exception) {
                        GroupUsers.update({GroupUsers.group eq groupId and (GroupUsers.user eq userId)}) {
                            it[status] = statusK.name
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

    /**
     * Update status of users in a group whose status match oldStatus
     */
    fun updateGroupUsersStatus(groupId: Long, oldStatus : Status, newStatus: Status) {
        transaction {
            GroupUsers.update({GroupUsers.group eq groupId and (GroupUsers.status eq oldStatus.name)}) {
                it[status] = newStatus.name
            }
        }
    }

    /*
    OBJECT TRACKING MANAGEMENT
     */
    /**
     * Add tracked object to DB, update if already present
     */
    fun addTrackedObject(userIdK : Int, objectIdK : String, storeK : String, domainK : String, targetPriceK : Float,
                         forceSellerK : Boolean = false, forceShippingK : Boolean = false) {
        transaction {
            try {
                TrackedObjects.insert {
                    it[userId] = userIdK
                    it[objectId] = objectIdK
                    it[store] = storeK
                    it[domain] = domainK
                    it[targetPrice] = targetPriceK
                    it[forceSeller] = forceSellerK
                    it[forceShipping] = forceShippingK
                }
            } catch (e: Exception) {
                updateTrackedObject(userIdK, objectIdK, storeK, domainK, targetPriceK, forceSellerK, forceShippingK)
            }
        }
    }

    /**
     * Update tracked object
     */
    fun updateTrackedObject(userIdK : Int, objectIdK : String, storeK : String, domainK : String, targetPriceK : Float,
                         forceSellerK : Boolean = false, forceShippingK : Boolean = false) {
        transaction {
            TrackedObjects.update({TrackedObjects.userId eq userIdK and (TrackedObjects.objectId eq objectIdK) and
                        (TrackedObjects.store eq storeK) and (TrackedObjects.domain eq domainK)}) {
                it[targetPrice] = targetPriceK
                it[forceSeller] = forceSellerK
                it[forceShipping] = forceShippingK
            }
        }
    }

    /**
     * Get all tracked objects
     */
    fun getAllTrackedObjects() : List<TrackedObject> {
        return transaction {
            TrackedObjects.selectAll().map {
                TrackedObject(user = it[TrackedObjects.userId], objectId = it[TrackedObjects.objectId],
                store = it[TrackedObjects.store], domain = it[TrackedObjects.domain], targetPrice = it[TrackedObjects.targetPrice],
                forceSellerK = it[TrackedObjects.forceSeller], forceShippingK = it[TrackedObjects.forceShipping])
            }.toList()
        }
    }

    /**
     * Remove a tracked object
     */
    fun removeTrackedObject(trackedObj : TrackedObject) {
        transaction {
            TrackedObjects.deleteWhere { TrackedObjects.userId eq trackedObj.user and (TrackedObjects.objectId eq trackedObj.objectId) and
                    (TrackedObjects.store eq trackedObj.store) and (TrackedObjects.domain eq trackedObj.domain)}
        }
    }
}