package ru.ntcrckr.peer.code.review.dao

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert

data class ConfigEntity(
    val anonymizeCommitInfo: Boolean,
)

object Configs : IntIdTable() {
    val anonymizeCommitInfo = bool("anonymize_commit_info")

    fun ResultRow.toConfigEntity(): ConfigEntity =
        ConfigEntity(this[anonymizeCommitInfo])

    fun insert(entity: ConfigEntity): Int =
        (insert { it[anonymizeCommitInfo] = entity.anonymizeCommitInfo } get id).value
}