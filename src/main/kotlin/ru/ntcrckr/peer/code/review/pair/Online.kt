package ru.ntcrckr.peer.code.review.pair

import com.jcabi.github.*
import ru.ntcrckr.peer.code.review.dao.CodeComments
import ru.ntcrckr.peer.code.review.dao.CodeComments.getInserterFor
import ru.ntcrckr.peer.code.review.dao.CodeComments.getOtherIds
import ru.ntcrckr.peer.code.review.dao.CodeComments.getOtherToThisIdMapping
import ru.ntcrckr.peer.code.review.dao.CodeReplies
import ru.ntcrckr.peer.code.review.dao.CodeReplies.getInserterFor
import ru.ntcrckr.peer.code.review.dao.CodeReplies.getOtherIds
import ru.ntcrckr.peer.code.review.dao.CodeReplies.getOtherToThisIdMapping
import ru.ntcrckr.peer.code.review.dao.PullCommentsTable
import ru.ntcrckr.peer.code.review.dao.PullCommentsTable.getInserterFor
import ru.ntcrckr.peer.code.review.dao.PullCommentsTable.getOtherIds
import ru.ntcrckr.peer.code.review.pair.github.*

abstract class Online(
    private val isSource: Boolean,
    pairId: Int,
    private val pullId: Int,
) {
    abstract val repo: Repo

    val pullRequest: Pull.Smart
        get() = repo.pulls().get(pullId).smart()

    val pullComments: List<Comment.Smart>
        get() = pullRequest.pullComments()

    val pullCodeComments: List<PullComment.Smart>
        get() = pullRequest.codeComments()

    private val pullCommentInserter: PullCommentsTable.Inserter = PullCommentsTable.getInserterFor(pairId, isSource)
    private val codeCommentInserter: CodeComments.Inserter = CodeComments.getInserterFor(pairId, isSource)
    private val codeReplyInserter: CodeReplies.Inserter = CodeReplies.getInserterFor(pairId, isSource)

    fun addPullComments(otherComments: List<Comment.Smart>) {
        val comments = pullRequest.issue().comments()
        val existingThisIds = comments.iterate(pullRequest.createdAt()).map { it.id }
        val existingOtherIds = PullCommentsTable.getOtherIds(existingThisIds, isSource)
        otherComments
            .filter { it.id !in existingOtherIds }
            .forEach { otherComment ->
                val thisComment = comments.post(otherComment.body())
                pullCommentInserter.insert(thisComment, otherComment)
            }
    }

    fun addCodeComments(allOtherCodeComments: List<PullComment.Smart>) {
        val (otherCodeComments, otherCodeReplies) = allOtherCodeComments.partition(::isNotReply)
        val thisComments = pullRequest.comments()
        thisComments.addCodeComments(otherCodeComments)
        thisComments.addCodeReplies(otherCodeReplies)
    }

    private fun PullComments.addCodeComments(otherComments: List<PullComment.Smart>) {
        val existingThisIds = iterate(emptyMap()).map { it.smart() }.filter(::isNotReply).map { it.id }
        val existingOtherIds = CodeComments.getOtherIds(existingThisIds, isSource)
        otherComments
            .filter { it.id !in existingOtherIds }
            .forEach { otherComment ->
                val thisComment = addCopyOf(otherComment)
                codeCommentInserter.insert(thisComment, otherComment)
            }
    }

    private fun PullComments.addCodeReplies(otherReplies: List<PullComment.Smart>) {
        val existingThisIds = iterate(emptyMap()).map { it.smart() }.filter(::isReply).map { it.id }
        val existingOtherIds = CodeReplies.getOtherIds(existingThisIds, isSource)
        val otherToThisIds = getOtherToThisIds(otherReplies)
        otherReplies
            .filter { it.id !in existingOtherIds }
            .forEach { otherReply ->
                val thisReply = reply(otherReply.body(), otherToThisIds[otherReply.replyId]!!.toInt())
                codeReplyInserter.insert(thisReply, otherReply)
            }
    }

    private fun getOtherToThisIds(otherReplies: List<PullComment.Smart>): Map<Long, Long> {
        val otherToThisCommentIds = CodeComments.getOtherToThisIdMapping(otherReplies.map { it.replyId }, isSource)
        val otherToThisReplyIds = CodeReplies.getOtherToThisIdMapping(otherReplies.map { it.replyId }, isSource)
        val otherToThisIds = otherToThisCommentIds + otherToThisReplyIds
        return otherToThisIds
    }

    private fun isReply(comment: PullComment.Smart): Boolean = runCatching { comment.replyId }.isSuccess

    private fun isNotReply(comment: PullComment.Smart): Boolean = !isReply(comment)
}