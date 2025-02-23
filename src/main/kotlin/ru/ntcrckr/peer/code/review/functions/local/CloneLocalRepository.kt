package ru.ntcrckr.peer.code.review.functions.local

import com.github.syari.kgit.KGit
import ru.ntcrckr.peer.code.review.functions.path
import java.nio.file.Path

fun cloneLocalRepository(
    sourcePath: Path,
    copyFolder: Path,
    copyName: String,
    sourceRemoteName: String,
): KGit = KGit.cloneRepository {
    setURI(sourcePath.toString())
    setDirectory(copyFolder.resolve(copyName).toFile())
    setRemote(sourceRemoteName)
    setCloneAllBranches(true)
}

fun cloneLocalRepository(sourceRepository: KGit, copyFolder: Path, copyName: String, sourceRemoteName: String): KGit =
    cloneLocalRepository(sourceRepository.path, copyFolder, copyName, sourceRemoteName)