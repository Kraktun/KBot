package com.miche.krak.kBot.database

import com.miche.krak.kBot.utils.printlnK
import org.jetbrains.exposed.sql.SqlLogger
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.statements.StatementContext
import org.jetbrains.exposed.sql.statements.expandArgs

private const val TAG = "DATABASE_MANAGER"

object BotLoggerK : SqlLogger {

    override fun log(context: StatementContext, transaction: Transaction) {
        printlnK(TAG, context.expandArgs(transaction))
    }
}