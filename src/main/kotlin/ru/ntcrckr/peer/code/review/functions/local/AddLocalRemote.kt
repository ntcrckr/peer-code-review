package ru.ntcrckr.peer.code.review.functions.local

import com.github.syari.kgit.KGit
import org.eclipse.jgit.transport.URIish
import java.nio.file.Path

fun KGit.addLocalRemote(
    path: Path,
    remoteName: String,
): KGit = also {
    remoteAdd {
        setUri(URIish(path.toString()))
        setName(remoteName)
    }

}