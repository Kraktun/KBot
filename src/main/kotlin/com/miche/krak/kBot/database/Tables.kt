package com.miche.krak.kBot.database

import org.jetbrains.exposed.sql.*

// https://www.kotlinresources.com/library/exposed/

object Users : Table() {
    val id = integer("id").primaryKey()
    val name = text("username")
}



