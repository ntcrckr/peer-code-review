package ru.ntcrckr.peer.code.review.dao.old

import com.jcabi.github.Comment
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import ru.ntcrckr.peer.code.review.dao.old.PullCommentsTable.Inserter
import ru.ntcrckr.peer.code.review.pair.github.id

object PullCommentsTable : Table() {
    val id = integer("id").autoIncrement()
    override val primaryKey = PrimaryKey(id)
    val repoPairId = integer("repo_pair_id").references(RepoPairTable.id)
    val sourceCommentId = long("source_comment_id")
    val copyCommentId = long("copy_comment_id")

    fun PullCommentsTable.getOtherIds(thisIds: List<Long>, isSource: Boolean): List<Long> {
        val (thisColumn, otherColumn) = thisAndOtherColumns(isSource)
        return select { thisColumn inList thisIds }
            .map { row -> row[otherColumn] }
    }

    fun interface Inserter {
        fun insert(thisComment: Comment, otherComment: Comment)
    }

    fun PullCommentsTable.getInserterFor(pairId: Int, isSource: Boolean): Inserter {
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