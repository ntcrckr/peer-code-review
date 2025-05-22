package ru.ntcrckr.peer.code.review.dao

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.AndOp
import org.jetbrains.exposed.sql.Join
import org.jetbrains.exposed.sql.JoinType.INNER
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import ru.ntcrckr.peer.code.review.dao.Repos.copyRepo
import ru.ntcrckr.peer.code.review.dao.Repos.sourceRepo
import ru.ntcrckr.peer.code.review.dao.Repos.toRepoEntity
import ru.ntcrckr.peer.code.review.dao.Users.performer
import ru.ntcrckr.peer.code.review.dao.Users.reviewer
import ru.ntcrckr.peer.code.review.dao.Users.teacher
import ru.ntcrckr.peer.code.review.dao.Users.toUserEntity

data class RepoPairEntity(
    val teacher: UserEntity,
    val performer: UserEntity,
    val sourceRepo: RepoEntity,
    val reviewer: UserEntity,
    val copyRepo: RepoEntity,
)

object RepoPairs : IntIdTable() {
    val lessonId = integer("lesson_id").references(Lessons.id)
    val teacherId = integer("teacher_id").references(Users.id)
    val performerId = integer("performer_id").references(Users.id)
    val sourceRepoId = integer("source_repo_id").references(Repos.id)
    val reviewerId = integer("reviewer_id").references(Users.id)
    val copyRepoId = integer("copy_repo_id").references(Repos.id)

    fun getAllForLesson(lessonId: Int): List<RepoPairEntity> = withJoins
        .select { RepoPairs.lessonId eq lessonId }
        .map {
            RepoPairEntity(
                teacher = it.toUserEntity(teacher),
                performer = it.toUserEntity(performer),
                sourceRepo = it.toRepoEntity(sourceRepo),
                reviewer = it.toUserEntity(reviewer),
                copyRepo = it.toRepoEntity(copyRepo),
            )
        }

    fun getId(
        lessonId: Int,
        entity: RepoPairEntity,
    ): Int? = withJoins
        .select {
            AndOp(
                listOf(
                    RepoPairs.lessonId eq lessonId,
                    teacher[Users.username] eq entity.teacher.username,
                    performer[Users.username] eq entity.performer.username,
                    sourceRepo[Repos.name] eq entity.sourceRepo.name,
                    sourceRepo[Repos.pullId] eq entity.sourceRepo.pullId,
                    reviewer[Users.username] eq entity.reviewer.username,
                    copyRepo[Repos.name] eq entity.copyRepo.name,
                    copyRepo[Repos.pullId] eq entity.copyRepo.pullId,
                )
            )
        }
        .limit(1)
        .singleOrNull()
        ?.let { it[id].value }

    fun insert(lessonId: Int, entity: RepoPairEntity): Int = insert {
        it[RepoPairs.lessonId] = lessonId
        it[teacherId] = Users.getIdOrInsert(entity.teacher)
        it[performerId] = Users.getIdOrInsert(entity.performer)
        it[sourceRepoId] = Repos.getIdOrInsert(entity.sourceRepo)
        it[performerId] = Users.getIdOrInsert(entity.reviewer)
        it[copyRepoId] = Repos.getIdOrInsert(entity.copyRepo)
    }[id].value

    fun getIdOrInsert(lessonId: Int, entity: RepoPairEntity): Int = getId(lessonId, entity) ?: insert(lessonId, entity)

    private val RepoPairs.withJoins: Join
        get() = join(teacher, INNER, teacherId, teacher[Users.id])
            .join(performer, INNER, performerId, performer[Users.id])
            .join(sourceRepo, INNER, sourceRepoId, sourceRepo[Repos.id])
            .join(reviewer, INNER, reviewerId, reviewer[Users.id])
            .join(copyRepo, INNER, copyRepoId, copyRepo[Repos.id])
}