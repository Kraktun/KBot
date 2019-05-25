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
    val id = long("id").primaryKey()
    val status = text("status")
}

/*
A user in a group does not need to have started the bot in a private chat
 */
object GroupUsers : Table() {
    val group = reference("group", Groups.id, onDelete = ReferenceOption.CASCADE).primaryKey(0)
    val user = integer("user").primaryKey(1)
    val status = text("status")
}






