package ru.ntcrckr.peer.code.review.pair.copy

import com.github.syari.kgit.KGit
import com.jcabi.github.Coordinates
import org.eclipse.jgit.transport.CredentialsProvider
import ru.ntcrckr.peer.code.review.pair.ANON_USER_EMAIL
import ru.ntcrckr.peer.code.review.pair.ANON_USER_NAME
import ru.ntcrckr.peer.code.review.pair.Config
import ru.ntcrckr.peer.code.review.pair.git.*

class LocalCopy(
    val repo: KGit,
    private val credentialsProvider: CredentialsProvider,
    private val config: Config,
    private val localRemoteName: String = DEFAULT_SOURCE_LOCAL_REMOTE_NAME,
    private val onlineRemoteName: String = DEFAULT_COPY_ONLINE_REMOTE_NAME,
) {
    fun updateFromLocalSource() {
        repo.copyFromRemote(localRemoteName)
        repo.checkoutAllRemoteBranches(localRemoteName)
        if (config.anonymizeCommitInfo)
            repo.anonymizeCommitInfo(ANON_USER_NAME, ANON_USER_EMAIL)
    }

    fun addRemote(coordinates: Coordinates) = repo.addOnlineRemote(coordinates, onlineRemoteName)

    fun updateOnlineCopy() = repo.updateRemote(onlineRemoteName, credentialsProvider)

    companion object {
        const val DEFAULT_SOURCE_LOCAL_REMOTE_NAME = "source"
        const val DEFAULT_COPY_ONLINE_REMOTE_NAME = "remote"
    }
}