package ru.ntcrckr.peer.code.review.dao

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*

data class RepoEntity(
    val name: String,
    val pullId: Int,
)

object Repos : IntIdTable() {
    val name = varchar("name", 50)
    val pullId = integer("pull_id")

    val sourceRepo = Repos.alias("source_repo")
    val copyRepo = Repos.alias("copy_repo")

    fun ResultRow.toRepoEntity(alias: Alias<Repos>): RepoEntity =
        RepoEntity(this[alias[name]], this[alias[pullId]])

    fun getId(entity: RepoEntity): Int? = select { (name eq entity.name) and (pullId eq entity.pullId) }
        .limit(1)
        .singleOrNull()
        ?.let { it[id].value }

    fun insert(entity: RepoEntity): Int = insert {
        it[name] = entity.name
        it[pullId] = entity.pullId
    }[id].value

    fun getIdOrInsert(entity: RepoEntity): Int = getId(entity) ?: insert(entity)
}