package ru.ntcrckr.peer.code.review.pair.source

import com.jcabi.github.Github
import org.eclipse.jgit.transport.CredentialsProvider
import ru.ntcrckr.peer.code.review.pair.users.Performer
import java.nio.file.Path

class Source(
    val online: OnlineSource,
    val local: LocalSource,
) {
    companion object {
        fun init(
            pairId: Int,
            github: Github,
            performer: Performer,
            credentialsProvider: CredentialsProvider,
            localSourcePath: Path,
        ): Source {
            val online = OnlineSource.from(pairId, github, performer)
            return Source(
                online = online,
                local = LocalSource.openOrCreate(localSourcePath, performer, online, credentialsProvider)
                    .also { it.updateFromOnline() }
            )
        }
    }
}