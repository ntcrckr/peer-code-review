package ru.ntcrckr.peer.code.review.pair

import com.jcabi.github.*
import org.slf4j.LoggerFactory
import ru.ntcrckr.peer.code.review.dao.PullCodeComments
import ru.ntcrckr.peer.code.review.dao.PullCodeReplies
import ru.ntcrckr.peer.code.review.dao.PullIssueComments
import ru.ntcrckr.peer.code.review.pair.github.*

abstract class Online(
    private val isSource: Boolean,
    pairId: Int,
    private val pullId: Int,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    abstract val repo: Repo

    val pullRequest: Pull.Smart
        get() = repo.pulls().get(pullId).smart()

    val pullComments: List<Comment.Smart>
        get() = pullRequest.pullComments()

    val pullCodeComments: List<PullComment.Smart>
        get() = pullRequest.codeComments()

    private val pullCommentInserter = PullIssueComments.getInserterFor(pairId, isSource)
    private val codeCommentInserter = PullCodeComments.getInserterFor(pairId, isSource)
    private val codeReplyInserter = PullCodeReplies.getInserterFor(pairId, isSource)

    fun addPullComments(otherComments: List<Comment.Smart>) {
        val comments = pullRequest.issue().comments()
        val existingThisIds = comments.iterate(pullRequest.createdAt()).map { it.id }
        val existingOtherIds = PullIssueComments.getOtherIds(existingThisIds, isSource)
        otherComments
            .filter { it.id !in existingOtherIds }
            .also { logger.info("Adding ${it.size} pull comments") }
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
        val existingOtherIds = PullCodeComments.getOtherIds(existingThisIds, isSource)
        otherComments
            .filter { it.id !in existingOtherIds }
            .also { logger.info("Adding ${it.size} code comments") }
            .forEach { otherComment ->
                val thisComment = addCopyOf(otherComment)
                codeCommentInserter.insert(thisComment, otherComment)
            }
    }

    private fun PullComments.addCodeReplies(otherReplies: List<PullComment.Smart>) {
        val existingThisIds = iterate(emptyMap()).map { it.smart() }.filter(::isReply).map { it.id }
        val existingOtherIds = PullCodeReplies.getOtherIds(existingThisIds, isSource)
        val otherToThisIds = getOtherToThisIds(otherReplies)
        otherReplies
            .filter { it.id !in existingOtherIds }
            .also { logger.info("Adding ${it.size} code replies") }
            .forEach { otherReply ->
                val thisReply = reply(otherReply.body(), otherToThisIds[otherReply.replyId]!!.toInt())
                codeReplyInserter.insert(thisReply, otherReply)
            }
    }

    private fun getOtherToThisIds(otherReplies: List<PullComment.Smart>): Map<Long, Long> {
        val otherToThisCommentIds = PullCodeComments.getOtherToThisIdMapping(otherReplies.map { it.replyId }, isSource)
        val otherToThisReplyIds = PullCodeReplies.getOtherToThisIdMapping(otherReplies.map { it.replyId }, isSource)
        val otherToThisIds = otherToThisCommentIds + otherToThisReplyIds
        return otherToThisIds
    }

    private fun isReply(comment: PullComment.Smart): Boolean = runCatching { comment.replyId }.isSuccess

    private fun isNotReply(comment: PullComment.Smart): Boolean = !isReply(comment)
}