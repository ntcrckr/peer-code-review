package ru.ntcrckr.peer.code.review.functions

import java.nio.file.Path

data class Source(
    val online: Online,
    val local: Local,
) {
    data class Online(
        val username: String,
        val repositoryName: String,
        val pullRequestNumber: Int,
    ) {
        val sshUrl: String get() = sshUrl(username, repositoryName)
    }

    data class Local(
        val folder: Path,
        val onlineRemoteName: String = DEFAULT_SOURCE_ONLINE_REMOTE_NAME,
    ) {
        companion object {
            const val DEFAULT_SOURCE_ONLINE_REMOTE_NAME = "remote"
        }
    }
}
