package ru.ntcrckr.peer.code.review.dao

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.JoinType.INNER
import org.jetbrains.exposed.sql.ReferenceOption.CASCADE
import ru.ntcrckr.peer.code.review.dao.Configs.toConfigEntity
import ru.ntcrckr.peer.code.review.dao.LocalRepos.copyLocalRepo
import ru.ntcrckr.peer.code.review.dao.LocalRepos.sourceLocalRepo
import ru.ntcrckr.peer.code.review.dao.LocalRepos.toLocalRepoEntity
import ru.ntcrckr.peer.code.review.dao.Repos.copyRepo
import ru.ntcrckr.peer.code.review.dao.Repos.sourceRepo
import ru.ntcrckr.peer.code.review.dao.Repos.toRepoEntity
import ru.ntcrckr.peer.code.review.dao.Users.performer
import ru.ntcrckr.peer.code.review.dao.Users.reviewer
import ru.ntcrckr.peer.code.review.dao.Users.toUserEntity
import kotlin.io.path.absolutePathString

data class RepoPairEntity(
    val id: Int = -1,
    val performer: UserEntity,
    val sourceRepo: RepoEntity,
    val sourceLocalRepo: LocalRepoEntity,
    val reviewer: UserEntity,
    val copyRepo: RepoEntity,
    val copyLocalRepo: LocalRepoEntity,
    val config: ConfigEntity,
)

object RepoPairs : IntIdTable() {
    val lessonId = integer("lesson_id").references(Lessons.id)
    val performerId = integer("performer_id").references(Users.id, onDelete = CASCADE)
    val sourceRepoId = integer("source_repo_id").references(Repos.id, onDelete = CASCADE)
    val sourceLocalRepoId = integer("source_local_repo_id").references(LocalRepos.id, onDelete = CASCADE)
    val reviewerId = integer("reviewer_id").references(Users.id, onDelete = CASCADE)
    val copyRepoId = integer("copy_repo_id").references(Repos.id, onDelete = CASCADE)
    val copyLocalRepoId = integer("copy_local_repo_id").references(LocalRepos.id, onDelete = CASCADE)
    val configId = integer("config_id").references(Configs.id, onDelete = CASCADE)

    fun getAll(): List<RepoPairEntity> = withJoins
        .selectAll()
        .map { it.toRepoPairEntity() }

    fun getAllForLesson(lessonId: Int): List<RepoPairEntity> = withJoins
        .select { RepoPairs.lessonId eq lessonId }
        .map { it.toRepoPairEntity() }

    fun get(repoPairId: Int): RepoPairEntity? = withJoins
        .select { RepoPairs.id eq repoPairId }
        .limit(1)
        .singleOrNull()
        ?.toRepoPairEntity()

    fun getId(
        lessonId: Int,
        entity: RepoPairEntity,
    ): Int? = withJoins
        .select {
            AndOp(
                listOf(
                    RepoPairs.lessonId eq lessonId,
                    performer[Users.username] eq entity.performer.username,
                    sourceRepo[Repos.name] eq entity.sourceRepo.name,
                    sourceRepo[Repos.pullId] eq entity.sourceRepo.pullId,
                    sourceLocalRepo[LocalRepos.path] eq entity.sourceLocalRepo.path.absolutePathString(),
                    reviewer[Users.username] eq entity.reviewer.username,
                    copyRepo[Repos.name] eq entity.copyRepo.name,
                    copyRepo[Repos.pullId] eq entity.copyRepo.pullId,
                    copyLocalRepo[LocalRepos.path] eq entity.copyLocalRepo.path.absolutePathString(),
                )
            )
        }
        .limit(1)
        .singleOrNull()
        ?.let { it[id].value }

    fun insert(lessonId: Int, entity: RepoPairEntity): Int = insert {
        it[RepoPairs.lessonId] = lessonId
        it[performerId] = Users.getIdOrInsert(entity.performer)
        it[sourceRepoId] = Repos.getIdOrInsert(entity.sourceRepo)
        it[sourceLocalRepoId] = LocalRepos.getIdOrInsert(entity.sourceLocalRepo)
        it[reviewerId] = Users.getIdOrInsert(entity.reviewer)
        it[copyRepoId] = Repos.getIdOrInsert(entity.copyRepo)
        it[copyLocalRepoId] = LocalRepos.getIdOrInsert(entity.copyLocalRepo)
        it[configId] = Configs.insert(entity.config)
    }[id].value

    private val RepoPairs.withJoins: Join
        get() = join(performer, INNER, performerId, performer[Users.id])
            .join(sourceRepo, INNER, sourceRepoId, sourceRepo[Repos.id])
            .join(sourceLocalRepo, INNER, sourceLocalRepoId, sourceLocalRepo[LocalRepos.id])
            .join(reviewer, INNER, reviewerId, reviewer[Users.id])
            .join(copyRepo, INNER, copyRepoId, copyRepo[Repos.id])
            .join(copyLocalRepo, INNER, copyLocalRepoId, copyLocalRepo[LocalRepos.id])
            .join(Configs, INNER, configId, Configs.id)

    private fun ResultRow.toRepoPairEntity() = RepoPairEntity(
        id = this[id].value,
        performer = toUserEntity(performer),
        sourceRepo = toRepoEntity(sourceRepo),
        sourceLocalRepo = toLocalRepoEntity(sourceLocalRepo),
        reviewer = toUserEntity(reviewer),
        copyRepo = toRepoEntity(copyRepo),
        copyLocalRepo = toLocalRepoEntity(copyLocalRepo),
        config = toConfigEntity(),
    )
}