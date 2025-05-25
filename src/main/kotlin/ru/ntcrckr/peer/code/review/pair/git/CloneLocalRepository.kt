package ru.ntcrckr.peer.code.review.pair.git

import com.github.syari.kgit.KGit
import ru.ntcrckr.peer.code.review.pair.copy.LocalCopy.Companion.DEFAULT_SOURCE_LOCAL_REMOTE_NAME
import java.nio.file.Path

fun cloneLocalRepository(
    sourceRepository: KGit,
    clonePath: Path,
    cloneName: String,
    sourceRemoteName: String = DEFAULT_SOURCE_LOCAL_REMOTE_NAME,
): KGit =
    KGit.cloneRepository {
        setURI(sourceRepository.path.toString())
        setDirectory(clonePath.resolve(cloneName).toFile())
        setRemote(sourceRemoteName)
        setBranch(sourceRepository.repository.branch)
    }