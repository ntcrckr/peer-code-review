package ru.ntcrckr.peer.code.review.pair.github

import com.jcabi.github.Repo

fun Repo.addCollaborator(username: String): Unit =
    collaborators().add(username)