package ru.ntcrckr.peer.code.review.pair.copy

import com.github.syari.kgit.KGit
import com.jcabi.github.Coordinates
import org.eclipse.jgit.transport.CredentialsProvider
import ru.ntcrckr.peer.code.review.pair.ANON_USER_EMAIL
import ru.ntcrckr.peer.code.review.pair.ANON_USER_NAME
import ru.ntcrckr.peer.code.review.pair.Config
import ru.ntcrckr.peer.code.review.pair.git.*
import ru.ntcrckr.peer.code.review.pair.source.LocalSource
import ru.ntcrckr.peer.code.review.pair.users.Teacher
import java.nio.file.Path
import kotlin.io.path.exists

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

        fun openOrCreate(
            path: Path,
            repoName: String,
            localSource: LocalSource,
            teacher: Teacher,
            config: Config,
            localRemoteName: String = DEFAULT_SOURCE_LOCAL_REMOTE_NAME,
            onlineRemoteName: String = DEFAULT_COPY_ONLINE_REMOTE_NAME,
        ): LocalCopy =
            LocalCopy(
                repo = when {
                    path.resolve(repoName).exists() -> KGit.open(path.toFile())
                    else -> cloneLocalRepository(localSource.repo, path, repoName, localRemoteName)
                },
                credentialsProvider = teacher.credentialsProvider,
                config = config,
                localRemoteName = localRemoteName,
                onlineRemoteName = onlineRemoteName,
            )
    }
}