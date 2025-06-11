package ru.ntcrckr.peer.code.review.pair.github

import com.jcabi.github.Comment
import com.jcabi.github.Pull
import com.jcabi.github.PullComment
import com.jcabi.github.Pulls

val Pull.id: Int get() = smart().url().path.substringAfterLast('/').toInt()

fun Pulls.firstByTitle(title: String): Pull =
    iterate(emptyMap()).first { it.smart().title() == title }

fun Pull.Smart.pullComments(): List<Comment.Smart> =
    issue().smart()
        .comments().iterate(createdAt())
        .map { it.smart() }

fun Pull.Smart.codeComments(): List<PullComment.Smart> =
    comments().iterate(emptyMap())
        .map { it.smart() }