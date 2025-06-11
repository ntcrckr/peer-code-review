package ru.ntcrckr.peer.code.review.dao

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import java.net.URI
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString

data class AppSetupEntity(
    val teacherUsername: String,
    val githubToken: String,
    val sourceReposFolder: Path,
    val copyReposFolder: Path,
) {
    val userUrl: URI = URI("https://github.com/$teacherUsername")
}

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
                sourceReposFolder = Path(it[sourceReposFolder]),
                copyReposFolder = Path(it[copyReposFolder]),
            )
        }

    fun insert(entity: AppSetupEntity): Int = insert {
        it[teacherUsername] = entity.teacherUsername
        it[githubToken] = entity.githubToken
        it[sourceReposFolder] = entity.sourceReposFolder.absolutePathString()
        it[copyReposFolder] = entity.copyReposFolder.absolutePathString()
    }[id].value

    fun update(entity: AppSetupEntity): Int = update({ id eq 1 }) {
        it[teacherUsername] = entity.teacherUsername
        it[githubToken] = entity.githubToken
        it[sourceReposFolder] = entity.sourceReposFolder.absolutePathString()
        it[copyReposFolder] = entity.copyReposFolder.absolutePathString()
    }

    fun upsert(entity: AppSetupEntity): Int = get()
        ?.let { update(entity) }
        ?: insert(entity)
}