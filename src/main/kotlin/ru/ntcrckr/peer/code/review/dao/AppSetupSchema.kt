package ru.ntcrckr.peer.code.review.dao

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update

data class AppSetupEntity(
    val teacherUsername: String,
    val githubToken: String,
    val sourceReposFolder: String,
    val copyReposFolder: String,
)

object AppSetup : IntIdTable() {
    val teacherUsername = varchar("teacher_username", 50)
    val githubToken = varchar("github_token", 100)
    val sourceReposFolder = varchar("source_repos_folder_varchar", 100)
    val copyReposFolder = varchar("copy_repos_folder_varchar", 100)

    fun get(): AppSetupEntity? = select { id eq 1 }
        .limit(1)
        .singleOrNull()
        ?.let {
            AppSetupEntity(
                teacherUsername = it[teacherUsername],
                githubToken = it[githubToken],
                sourceReposFolder = it[sourceReposFolder],
                copyReposFolder = it[copyReposFolder],
            )
        }

    fun insert(entity: AppSetupEntity): Int = insert {
        it[teacherUsername] = entity.teacherUsername
        it[githubToken] = entity.githubToken
        it[sourceReposFolder] = entity.sourceReposFolder
        it[copyReposFolder] = entity.copyReposFolder
    }[id].value

    fun update(entity: AppSetupEntity): Int = update({ id eq 1 }) {
        it[teacherUsername] = entity.teacherUsername
        it[githubToken] = entity.githubToken
        it[sourceReposFolder] = entity.sourceReposFolder
        it[copyReposFolder] = entity.copyReposFolder
    }

    fun upsert(entity: AppSetupEntity): Int = get()
        ?.let { update(entity) }
        ?: insert(entity)
}