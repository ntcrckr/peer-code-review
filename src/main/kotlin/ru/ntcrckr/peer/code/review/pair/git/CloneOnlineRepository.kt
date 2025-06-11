package ru.ntcrckr.peer.code.review.pair.git

import com.github.syari.kgit.KGit
import org.eclipse.jgit.transport.CredentialsProvider
import ru.ntcrckr.peer.code.review.pair.source.IOnlineSource
import ru.ntcrckr.peer.code.review.pair.source.LocalSource.Companion.DEFAULT_SOURCE_ONLINE_REMOTE_NAME
import java.nio.file.Path

fun cloneOnlineRepository(
    sshUrl: String,
    path: Path,
    onlineSource: IOnlineSource,
    credentialsProvider: CredentialsProvider,
    onlineRemoteName: String = DEFAULT_SOURCE_ONLINE_REMOTE_NAME,
): KGit = KGit.cloneRepository {
    setURI(sshUrl)
    setDirectory(path.resolve(onlineSource.coordinates.repo()).toFile())
    setRemote(onlineRemoteName)
    setCredentialsProvider(credentialsProvider)
}