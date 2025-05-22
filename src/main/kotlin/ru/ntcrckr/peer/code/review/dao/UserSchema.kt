package ru.ntcrckr.peer.code.review.dao

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*

data class UserEntity(
    val username: String,
)

object Users : IntIdTable() {
    val username = varchar("username", 50)

    val teacher = Users.alias("teacher")
    val performer = Users.alias("performer")
    val reviewer = Users.alias("reviewer")

    fun ResultRow.toUserEntity(alias: Alias<Users>): UserEntity =
        UserEntity(this[alias[username]])

    fun getId(entity: UserEntity): Int? = select { username eq entity.username }
        .limit(1)
        .singleOrNull()
        ?.let { it[id].value }

    fun insert(entity: UserEntity): Int = (insert { it[username] = entity.username } get id).value

    fun getIdOrInsert(entity: UserEntity): Int = getId(entity) ?: insert(entity)
}