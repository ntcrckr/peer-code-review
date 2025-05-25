package ru.ntcrckr.peer.code.review.dao

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString

data class LocalRepoEntity(
    val path: Path,
)

object LocalRepos : IntIdTable() {
    val path = varchar("path", 100)

    val sourceLocalRepo = LocalRepos.alias("source_local_repo")
    val copyLocalRepo = LocalRepos.alias("copy_local_repo")

    fun ResultRow.toLocalRepoEntity(alias: Alias<LocalRepos>): LocalRepoEntity =
        LocalRepoEntity(Path(this[alias[path]]))

    fun getId(entity: LocalRepoEntity): Int? = select { path eq entity.path.absolutePathString() }
        .limit(1)
        .singleOrNull()
        ?.let { it[id].value }

    fun insert(entity: LocalRepoEntity): Int = insert {
        it[path] = entity.path.absolutePathString()
    }[id].value

    fun getIdOrInsert(entity: LocalRepoEntity): Int = getId(entity) ?: insert(entity)
}