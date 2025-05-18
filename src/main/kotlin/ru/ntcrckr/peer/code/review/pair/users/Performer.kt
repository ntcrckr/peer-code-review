package ru.ntcrckr.peer.code.review.pair.users

import ru.ntcrckr.peer.code.review.pair.sshUrl

data class Performer(
    val username: String,
    val repoName: String,
    val pullId: Int,
) {
    val sshUrl: String = sshUrl(username, repoName)
}
