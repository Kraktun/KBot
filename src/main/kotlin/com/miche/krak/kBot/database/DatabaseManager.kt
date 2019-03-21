package com.miche.krak.kBot.database

import com.miche.krak.kBot.objects.UserK
import com.miche.krak.kBot.utils.Status
import com.miche.krak.kBot.utils.getMainFolder
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.telegram.telegrambots.meta.api.objects.User
import java.sql.Connection

class DatabaseManager private constructor() {

    companion object {
        val instance by lazy { DatabaseManager() }
    }

    init {
        connectDB()
    }

    private fun connectDB() {
        val dbLink = getMainFolder() + "\\KBotDB.db"
        print(dbLink)
        Database.connect("jdbc:sqlite:$dbLink", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE // Or Connection.TRANSACTION_READ_UNCOMMITTED

        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(Users, Groups, GroupUsers)
        }
    }

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

    fun insertUser(list : List<UserK>) {
        transaction {
            Users.batchInsert(list) {user ->
                this[Users.id] = user.id
                this[Users.username] = user.username
                this[Users.status] = user.status.name
                this[Users.statusInfo] = user.userInfo
            }
        }
    }

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
}