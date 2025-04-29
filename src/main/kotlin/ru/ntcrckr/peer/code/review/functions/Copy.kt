package ru.ntcrckr.peer.code.review.functions

import java.nio.file.Path

data class Copy(
    val local: Local,
    val online: Online,
) {
    data class Local(
        val folder: Path,
        val name: String,
        val localRemoteName: String = DEFAULT_SOURCE_LOCAL_REMOTE_NAME,
        val onlineRemoteName: String = DEFAULT_COPY_ONLINE_REMOTE_NAME,
    ) {
        companion object {
            const val DEFAULT_SOURCE_LOCAL_REMOTE_NAME = "source"
            const val DEFAULT_COPY_ONLINE_REMOTE_NAME = "remote"
        }
    }

    data class Online(
        val repositoryName: String,
        val pullRequestNumber: Int,
        val lastComment: Int,
    )
}
