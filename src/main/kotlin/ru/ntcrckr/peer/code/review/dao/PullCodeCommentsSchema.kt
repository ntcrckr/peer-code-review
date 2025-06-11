package ru.ntcrckr.peer.code.review.dao

import com.jcabi.github.PullComment
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import ru.ntcrckr.peer.code.review.dao.PullCodeComments.Inserter
import ru.ntcrckr.peer.code.review.pair.github.id

object PullCodeComments : IntIdTable() {
    val repoPairId = integer("repo_pair_id").references(RepoPairs.id)
    val sourceCommentId = long("source_comment_id")
    val copyCommentId = long("copy_comment_id")

    fun getOtherIds(thisIds: List<Long>, isSource: Boolean): List<Long> {
        val (thisColumn, otherColumn) = thisAndOtherColumns(isSource)
        return select { thisColumn inList thisIds }
            .map { row -> row[otherColumn] }
    }

    fun getOtherToThisIdMapping(otherIds: List<Long>, isSource: Boolean): Map<Long, Long> {
        val (thisColumn, otherColumn) = thisAndOtherColumns(isSource)
        return select { otherColumn inList otherIds }
            .associate { row -> row[otherColumn] to row[thisColumn] }
    }

    fun interface Inserter {
        fun insert(thisComment: PullComment, otherComment: PullComment)
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