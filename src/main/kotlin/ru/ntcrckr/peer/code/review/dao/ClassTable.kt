package ru.ntcrckr.peer.code.review.dao

import org.jetbrains.exposed.sql.Table

object ClassTable : Table() {
    val id = integer("id").autoIncrement()
    override val primaryKey = PrimaryKey(id)
    val name = varchar("name", 50)
}