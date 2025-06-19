package ru.ntcrckr.peer.code.review.dao

import com.jcabi.github.Comment
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption.CASCADE
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import ru.ntcrckr.peer.code.review.dao.PullIssueComments.Inserter
import ru.ntcrckr.peer.code.review.pair.github.id

object PullIssueComments : IntIdTable() {
    val repoPairId = integer("repo_pair_id").references(RepoPairs.id, onDelete = CASCADE)
    val sourceCommentId = long("source_comment_id")
    val copyCommentId = long("copy_comment_id")

    fun getOtherIds(thisIds: List<Long>, isSource: Boolean): List<Long> {
        val (thisColumn, otherColumn) = thisAndOtherColumns(isSource)
        return select { thisColumn inList thisIds }
            .map { row -> row[otherColumn] }
    }

    fun interface Inserter {
        fun insert(thisComment: Comment, otherComment: Comment)
    }

    fun getInserterFor(pairId: Int, isSource: Boolean): Inserter {
        val (thisColumn, otherColumn) = thisAndOtherColumns(isSource)
        return Inserter { thisComment, otherComment ->
            insert { table ->
                table[repoPairId] = pairId
                table[thisColumn] = thisComment.id
                table[otherColumn] = otherComment.id
            }
        }
    }

    private fun thisAndOtherColumns(isSource: Boolean): Pair<Column<Long>, Column<Long>> =
        when (isSource) {
            true -> sourceCommentId to copyCommentId
            false -> copyCommentId to sourceCommentId
        }
}