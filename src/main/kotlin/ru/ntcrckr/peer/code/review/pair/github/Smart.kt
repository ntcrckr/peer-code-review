package ru.ntcrckr.peer.code.review.pair.github

import com.jcabi.github.Comment
import com.jcabi.github.Issue
import com.jcabi.github.Pull
import com.jcabi.github.PullComment

fun Pull.smart(): Pull.Smart = Pull.Smart(this)

fun PullComment.smart(): PullComment.Smart = PullComment.Smart(this)

fun Comment.smart(): Comment.Smart = Comment.Smart(this)

fun Issue.smart(): Issue.Smart = Issue.Smart(this)