package com.miche.krak.kBot.database

import org.jetbrains.exposed.sql.*

// https://www.kotlinresources.com/library/exposed/

object Users : Table() {
    val id = integer("id").primaryKey()
    val username = text("username").nullable()
    val status = text("status")
    val statusInfo = text("status_info").nullable()
}

object Groups : Table() {
    val id = integer("id").primaryKey()
}

object GroupUsers : Table() {
    val group = reference("group", Groups.id).primaryKey(0)
    val user = reference("user", Users.id).primaryKey(1)
    val status = text("status")
}






