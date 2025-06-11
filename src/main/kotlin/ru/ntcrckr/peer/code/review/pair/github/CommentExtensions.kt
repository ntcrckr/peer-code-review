package ru.ntcrckr.peer.code.review.pair.github

import com.jcabi.github.Comment
import com.jcabi.github.PullComment
import com.jcabi.github.PullComments

val Comment.id: Long get() = json().getJsonNumber("id").longValue()

val PullComment.id: Long get() = json().getJsonNumber("id").longValue()

val PullComment.replyId: Long get() = json().getJsonNumber("in_reply_to_id").longValue()

fun PullComments.addCopyOf(comment: PullComment.Smart): PullComment =
    post(comment.body(), comment.commitId(), comment.json().getString("path"), comment.json().getInt("position"))