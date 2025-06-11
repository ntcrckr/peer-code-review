package ru.ntcrckr.peer.code.review.pair.source

import com.github.syari.kgit.KGit
import org.eclipse.jgit.transport.CredentialsProvider
import ru.ntcrckr.peer.code.review.pair.git.checkoutAllRemoteBranches
import ru.ntcrckr.peer.code.review.pair.git.copyFromRemote

class LocalSource(
    val repo: KGit,
    private val credentialsProvider: CredentialsProvider,
    val onlineRemoteName: String = DEFAULT_SOURCE_ONLINE_REMOTE_NAME,
) {
    fun updateFromOnline() {
        repo.copyFromRemote(onlineRemoteName, credentialsProvider)
        repo.checkoutAllRemoteBranches(onlineRemoteName)
    }

    companion object {
        const val DEFAULT_SOURCE_ONLINE_REMOTE_NAME = "remote"
    }
}