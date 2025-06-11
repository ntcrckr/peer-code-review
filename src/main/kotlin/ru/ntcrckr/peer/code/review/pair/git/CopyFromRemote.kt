package ru.ntcrckr.peer.code.review.pair.git

import com.github.syari.kgit.KGit
import org.eclipse.jgit.transport.CredentialsProvider

fun KGit.copyFromRemote(
    remoteName: String,
    credentialsProvider: CredentialsProvider? = null,
): KGit = also {
    fetch {
        this.remote = remoteName
        isForceUpdate = true
        credentialsProvider?.also { setCredentialsProvider(credentialsProvider) }
    }
}