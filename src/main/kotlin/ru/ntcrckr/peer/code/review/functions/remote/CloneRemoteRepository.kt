package ru.ntcrckr.peer.code.review.functions.remote

import com.github.syari.kgit.KGit
import java.nio.file.Path

fun cloneRemoteRepository(
    repositoryUrl: String,
    localFolder: Path,
    localName: String,
    remoteName: String,
): KGit = KGit.cloneRepository {
    setURI(repositoryUrl)
    setDirectory(localFolder.resolve(localName).toFile())
    setRemote(remoteName)
    setCloneAllBranches(true)
}