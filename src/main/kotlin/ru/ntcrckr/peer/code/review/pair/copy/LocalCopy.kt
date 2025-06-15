package ru.ntcrckr.peer.code.review.pair.copy

import com.github.syari.kgit.KGit
import com.jcabi.github.Coordinates
import org.eclipse.jgit.transport.CredentialsProvider
import ru.ntcrckr.peer.code.review.dao.ConfigEntity
import ru.ntcrckr.peer.code.review.pair.ANON_USER_EMAIL
import ru.ntcrckr.peer.code.review.pair.ANON_USER_NAME
import ru.ntcrckr.peer.code.review.pair.git.*

typealias SourceToCopyCommitHashes = List<Pair<String, String>>

class LocalCopy(
    val repo: KGit,
    private val credentialsProvider: CredentialsProvider,
    private val config: ConfigEntity,
    private val localRemoteName: String = DEFAULT_SOURCE_LOCAL_REMOTE_NAME,
    private val onlineRemoteName: String = DEFAULT_COPY_ONLINE_REMOTE_NAME,
) {
    fun updateFromLocalSource(): SourceToCopyCommitHashes {
        repo.copyFromRemote(localRemoteName)
        repo.checkoutAllRemoteBranches(localRemoteName)
        if (config.anonymizeCommitInfo) {
            val newCommits = repo.getCommits()
                .filter { commit ->
                    commit.authorIdent.let { it.name != ANON_USER_NAME && it.emailAddress != ANON_USER_EMAIL }
                }
            repo.anonymizeCommitInfo()
            val newCommitsTimes = newCommits.map { it.commitTime }
            val anonymizedNewCommits = repo.getCommits()
                .filter { commit ->
                    commit.commitTime in newCommitsTimes
                }
                .associateBy { it.commitTime }
            return newCommits
                .mapNotNull {
                    val anonymizedCommit = anonymizedNewCommits[it.commitTime] ?: return@mapNotNull null
                    it.name to anonymizedCommit.name
                }
        }
        return emptyList()
    }

    fun addRemote(coordinates: Coordinates) = repo.addOnlineRemote(coordinates, onlineRemoteName)

    fun updateOnlineCopy() = repo.updateRemote(onlineRemoteName, credentialsProvider)

    companion object {
        const val DEFAULT_SOURCE_LOCAL_REMOTE_NAME = "source"
        const val DEFAULT_COPY_ONLINE_REMOTE_NAME = "remote"
    }
}