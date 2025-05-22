package ru.ntcrckr.peer.code.review.dao.old

import com.jcabi.github.PullComment
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import ru.ntcrckr.peer.code.review.pair.github.id

object CodeComments : Table() {
    val id = integer("id").autoIncrement()
    override val primaryKey = PrimaryKey(id)
    val repoPairId = integer("repo_pair_id").references(RepoPairTable.id)
    val sourceCommentId = long("source_comment_id")
    val copyCommentId = long("copy_comment_id")

    fun CodeComments.getOtherIds(thisIds: List<Long>, isSource: Boolean): List<Long> {
        val (thisColumn, otherColumn) = thisAndOtherColumns(isSource)
        return select { thisColumn inList thisIds }
            .map { row -> row[otherColumn] }
    }

    fun CodeComments.getOtherToThisIdMapping(otherIds: List<Long>, isSource: Boolean): Map<Long, Long> {
        val (thisColumn, otherColumn) = thisAndOtherColumns(isSource)
        return select { otherColumn inList otherIds }
            .associate { row -> row[otherColumn] to row[thisColumn] }
    }

    fun interface Inserter {
        fun insert(thisComment: PullComment, otherComment: PullComment)
    }

    fun CodeComments.getInserterFor(pairId: Int, isSource: Boolean): Inserter {
        val (thisColumn, otherColumn) = thisAndOtherColumns(isSource)
        return Inserter { thisComment, otherComment ->
            insert { table ->
                table[repoPairId] = pairId
                table[thisColumn] = thisComment.id
                table[otherColumn] = otherComment.id
            }
        }
    }

    private fun thisAndOtherColumns(isSource: Boolean) = when (isSource) {
        true -> sourceCommentId to copyCommentId
        false -> copyCommentId to sourceCommentId
    }
}