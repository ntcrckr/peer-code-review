package ru.ntcrckr.peer.code.review.pair.git

import com.github.syari.kgit.KGit
import com.jcabi.github.Coordinates
import org.eclipse.jgit.transport.URIish

fun KGit.addOnlineRemote(
    coordinates: Coordinates,
    remoteName: String,
): KGit = also {
    remoteAdd {
        setUri(URIish("git@github.com:${coordinates.user()}/${coordinates.repo()}.git"))
        setName(remoteName)
    }
}