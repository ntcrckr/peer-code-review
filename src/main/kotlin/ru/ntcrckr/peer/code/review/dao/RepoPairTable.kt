package ru.ntcrckr.peer.code.review.dao

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import ru.ntcrckr.peer.code.review.pair.users.Performer
import ru.ntcrckr.peer.code.review.pair.users.Reviewer
import ru.ntcrckr.peer.code.review.pair.users.Teacher
import java.nio.file.Path

object RepoPairTable : Table() {
    val id = integer("id").autoIncrement()
    override val primaryKey = PrimaryKey(id)
    val performerId = integer("performer_id").references(PerformerTable.id)
    val teacherId = integer("teacher_id").references(TeacherTable.id)
    val reviewerId = integer("reviewer_id").references(ReviewerTable.id)
    val localSourcePath = varchar("local_source_path", 100)
    val localCopyPath = varchar("local_copy_path", 100)
//    val lastProcessedCopyCommentId = integer("last_processed_copy_comment_id")
//    val lastProcessedSourceCommentId = integer("last_processed_source_comment_id")

    fun RepoPairTable.getId(performer: Performer, reviewer: Reviewer): Int? =
        leftJoin(PerformerTable).leftJoin(ReviewerTable)
            .select {
                PerformerTable.username eq performer.username
                PerformerTable.repoName eq performer.repoName
                ReviewerTable.username eq reviewer.username
            }
            .limit(1)
            .singleOrNull()
            ?.let { it[id] }

    fun RepoPairTable.insertRepoPair(
        performer: Performer,
        teacher: Teacher,
        reviewer: Reviewer,
        localSourcePath: Path,
        localCopyPath: Path,
    ): Int {
        val performerId = PerformerTable.insert {
            it[username] = performer.username
            it[repoName] = performer.repoName
            it[pullId] = performer.pullId
        } get PerformerTable.id
        val teacherId = TeacherTable.insert {
            it[username] = teacher.username
        } get TeacherTable.id
        val reviewerId = ReviewerTable.insert {
            it[username] = reviewer.username
        } get ReviewerTable.id
        return insert {
            it[this.performerId] = performerId
            it[this.teacherId] = teacherId
            it[this.reviewerId] = reviewerId
            it[this.localSourcePath] = localSourcePath.toAbsolutePath().toString()
            it[this.localCopyPath] = localCopyPath.toAbsolutePath().toString()
        } get id
    }
}