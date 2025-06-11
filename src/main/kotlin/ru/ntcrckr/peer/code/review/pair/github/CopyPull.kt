package ru.ntcrckr.peer.code.review.pair.github

import com.jcabi.github.Pull
import com.jcabi.github.Repo

fun Repo.copyPull(sourcePull: Pull.Smart): Pull {
    val baseRef = sourcePull.base().ref()
    val headRef = sourcePull.head().ref()
    val pull = pulls().create(sourcePull.title(), headRef, baseRef)
    return pull
}