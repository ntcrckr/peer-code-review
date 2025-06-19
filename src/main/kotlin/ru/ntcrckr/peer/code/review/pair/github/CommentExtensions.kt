package ru.ntcrckr.peer.code.review.pair.github

import com.jcabi.github.Comment
import com.jcabi.github.PullComment

val Comment.id: Long get() = json().getJsonNumber("id").longValue()

val PullComment.id: Long get() = json().getJsonNumber("id").longValue()

val PullComment.replyId: Long get() = json().getJsonNumber("in_reply_to_id").longValue()