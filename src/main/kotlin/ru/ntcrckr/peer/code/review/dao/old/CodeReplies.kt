package ru.ntcrckr.peer.code.review.dao.old

import com.jcabi.github.PullComment
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import ru.ntcrckr.peer.code.review.dao.old.CodeReplies.Inserter
import ru.ntcrckr.peer.code.review.pair.github.id
import ru.ntcrckr.peer.code.review.pair.github.replyId

object CodeReplies : Table() {
    val id = integer("id").autoIncrement()
    override val primaryKey = PrimaryKey(id)
    val repoPairId = integer("repo_pair_id").references(RepoPairTable.id)
    val sourceReplyId = long("source_reply_id")
    val sourceRepliedToId = long("source_replied_to_id")
    val copyReplyId = long("copy_reply_id")
    val copyRepliedToId = long("copy_replied_to_id")

    fun CodeReplies.getOtherIds(thisIds: List<Long>, isSource: Boolean): List<Long> {
        val (thisColumn, otherColumn) = thisAndOtherReplyColumns(isSource)
        return select { thisColumn inList thisIds }
            .map { row -> row[otherColumn] }
    }

    fun CodeReplies.getOtherToThisIdMapping(otherIds: List<Long>, isSource: Boolean): Map<Long, Long> {
        val (thisColumn, otherColumn) = thisAndOtherReplyColumns(isSource)
        return select { otherColumn inList otherIds }
            .associate { row -> row[otherColumn] to row[thisColumn] }
    }

    fun interface Inserter {
        fun insert(thisReply: PullComment, otherReply: PullComment)
    }

    fun CodeReplies.getInserterFor(pairId: Int, isSource: Boolean): Inserter {
        val (thisReplyColumn, otherReplyColumn) = thisAndOtherReplyColumns(isSource)
        val (thisRepliedToColumn, otherRepliedToColumn) = thisAndOtherRepliedToColumns(isSource)
        return Inserter { thisReply, otherReply ->
            insert { table ->
                table[repoPairId] = pairId
                table[thisReplyColumn] = thisReply.id
                table[thisRepliedToColumn] = thisReply.replyId
                table[otherReplyColumn] = otherReply.id
                table[otherRepliedToColumn] = otherReply.replyId
            }
        }
    }

    private fun thisAndOtherReplyColumns(isSource: Boolean) = when (isSource) {
        true -> sourceReplyId to copyReplyId
        false -> copyReplyId to sourceReplyId
    }

    private fun thisAndOtherRepliedToColumns(isSource: Boolean) = when (isSource) {
        true -> sourceRepliedToId to copyRepliedToId
        false -> copyRepliedToId to sourceRepliedToId
    }
}