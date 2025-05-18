package ru.ntcrckr.peer.code.review.pair.git

import com.github.syari.kgit.KGit
import org.eclipse.jgit.transport.CredentialsProvider
import ru.ntcrckr.peer.code.review.pair.source.OnlineSource
import java.nio.file.Path

fun cloneOnlineRepository(
    sshUrl: String,
    path: Path,
    onlineSource: OnlineSource,
    onlineRemoteName: String,
    credentialsProvider: CredentialsProvider,
): KGit = KGit.cloneRepository {
    setURI(sshUrl)
    setDirectory(path.resolve(onlineSource.coordinates.repo()).toFile())
    setRemote(onlineRemoteName)
    setCredentialsProvider(credentialsProvider)
}