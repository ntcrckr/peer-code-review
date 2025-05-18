package ru.ntcrckr.peer.code.review.pair.git

import com.github.syari.kgit.KGit
import java.nio.file.Path

fun cloneLocalRepository(
    sourceRepository: KGit,
    clonePath: Path,
    cloneName: String,
    sourceRemoteName: String,
): KGit =
    KGit.cloneRepository {
        setURI(sourceRepository.path.toString())
        setDirectory(clonePath.resolve(cloneName).toFile())
        setRemote(sourceRemoteName)
        setBranch(sourceRepository.repository.branch)
    }