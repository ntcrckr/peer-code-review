package ru.ntcrckr.peer.code.review.dao

import org.jetbrains.exposed.sql.Table

object PerformerTable : Table() {
    val id = integer("id").autoIncrement()
    override val primaryKey = PrimaryKey(id)
    val username = varchar("username", 50)
    val repoName = varchar("repo_name", 50)
    val pullId = integer("pull_id")
}