package ru.ntcrckr.peer.code.review.pair.users

import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider

data class Teacher(
    val username: String,
    val githubToken: String,
) {
    val credentialsProvider: UsernamePasswordCredentialsProvider =
        UsernamePasswordCredentialsProvider(username, githubToken)
}
