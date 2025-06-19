package ru.ntcrckr.peer.code.review.dao

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import ru.ntcrckr.peer.code.review.dao.Commits.Inserter

object Commits : IntIdTable() {
    val repoPairId = integer("repo_pair_id").references(RepoPairs.id)
    val sourceCommentId = varchar("source_comment_id", 50)
    val copyCommentId = varchar("copy_comment_id", 50)

    fun getOtherId(thisId: String, isSource: Boolean): String {
        val (thisColumn, otherColumn) = thisAndOtherColumns(isSource)
        return select { thisColumn eq thisId }
            .limit(1)
            .single()
            .let { row -> row[otherColumn] }
    }

    fun interface Inserter {
        fun insert(thisCommitHash: String, otherCommitHash: String)
    }

    fun getInserter(pairId: Int): Inserter {
        return Inserter { thisCommitHash, otherCommitHash ->
            insert { table ->
                table[PullIssueComments.repoPairId] = pairId
                table[sourceCommentId] = thisCommitHash
                table[copyCommentId] = otherCommitHash
            }
        }
    }

    private fun thisAndOtherColumns(isSource: Boolean): Pair<Column<String>, Column<String>> =
        when (isSource) {
            true -> sourceCommentId to copyCommentId
            false -> copyCommentId to sourceCommentId
        }
}