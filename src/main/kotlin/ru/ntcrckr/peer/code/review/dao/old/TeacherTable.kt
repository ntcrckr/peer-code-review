package ru.ntcrckr.peer.code.review.dao.old

import org.jetbrains.exposed.sql.Table

object TeacherTable : Table() {
    val id = integer("id").autoIncrement()
    override val primaryKey = PrimaryKey(id)
    val username = varchar("username", 50)
}