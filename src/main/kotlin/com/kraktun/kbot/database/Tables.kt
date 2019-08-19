package com.kraktun.kbot.database

import org.jetbrains.exposed.sql.*

// https://www.kotlinresources.com/library/exposed/
// DSL

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
A user in a group does not need to start the bot in a private chat
 */
object GroupUsers : Table() {
    val group = reference("group", Groups.id, onDelete = ReferenceOption.CASCADE).primaryKey(0)
    val user = integer("user").primaryKey(1)
    val status = text("status")
}

object TrackedObjects : Table() {
    val name = text("name")
    val userId = reference("userId", Users.id, onDelete = ReferenceOption.CASCADE).primaryKey(0)
    val objectId = text("objectId").primaryKey(1)
    val store = text("store").primaryKey(2)
    val extraKey = text("extraKey").primaryKey(3).default("") // extra key if necessary
    val targetPrice = float("targetPrice")
    val data = text("data") // Encoding and decoding must be done in calling classes (aka tracking services\jobs)
}