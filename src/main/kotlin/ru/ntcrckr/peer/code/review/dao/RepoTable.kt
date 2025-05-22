package ru.ntcrckr.peer.code.review.dao

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.alias

object RepoTable : Table() {
    val id = integer("id").autoIncrement()
    override val primaryKey = PrimaryKey(id)
    val repoName = varchar("repo_name", 50)
    val pullId = integer("pull_id")

    val r1 = RepoTable.alias("r1")
    val r2 = RepoTable.alias("r2")
}