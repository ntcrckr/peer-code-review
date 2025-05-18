package ru.ntcrckr.peer.code.review.pair.source

import com.github.syari.kgit.KGit
import org.eclipse.jgit.transport.CredentialsProvider
import ru.ntcrckr.peer.code.review.pair.git.checkoutAllRemoteBranches
import ru.ntcrckr.peer.code.review.pair.git.cloneOnlineRepository
import ru.ntcrckr.peer.code.review.pair.git.copyFromRemote
import ru.ntcrckr.peer.code.review.pair.users.Performer
import java.nio.file.Path
import kotlin.io.path.exists

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

        fun openOrCreate(
            path: Path,
            performer: Performer,
            online: OnlineSource,
            credentialsProvider: CredentialsProvider,
            onlineRemoteName: String = DEFAULT_SOURCE_ONLINE_REMOTE_NAME,
        ): LocalSource =
            LocalSource(
                repo = when {
                    path.resolve(".git").exists() -> KGit.open(path.toFile())
                    else -> cloneOnlineRepository(performer.sshUrl, path, online, onlineRemoteName, credentialsProvider)
                },
                credentialsProvider = credentialsProvider,
                onlineRemoteName = onlineRemoteName,
            )
    }
}