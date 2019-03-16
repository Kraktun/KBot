package com.miche.krak.kBot.database

import com.miche.krak.kBot.utils.getMainFolder
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

class DatabaseManager private constructor() {

    companion object {
        @Volatile var instance = DatabaseManager()
    }

    init {
        connectDB()
    }

    private fun connectDB() {
        val dbLink = getMainFolder() + "\\KBotDB.db"
        print(dbLink)
        // Your connection properties here
        Database.connect("jdbc:sqlite:$dbLink", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE // Or Connection.TRANSACTION_READ_UNCOMMITTED

        transaction {
            SchemaUtils.create(Users)
        }
    }

}