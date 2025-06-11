package ru.ntcrckr.peer.code.review.pair.git

import com.github.syari.kgit.KGit
import org.eclipse.jgit.transport.CredentialsProvider

fun KGit.updateRemote(
    remoteName: String,
    credentialsProvider: CredentialsProvider? = null,
): KGit = also {
    push {
        remote = remoteName
        setPushAll()
        credentialsProvider?.also { setCredentialsProvider(credentialsProvider) }
    }
}