package ru.ntcrckr.peer.code.review.dao

import org.jetbrains.exposed.sql.Table

object ReviewerTable : Table() {
    val id = integer("id").autoIncrement()
    override val primaryKey = PrimaryKey(id)
    val username = varchar("username", 50)
}