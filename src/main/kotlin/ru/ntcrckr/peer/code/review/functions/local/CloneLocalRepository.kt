package ru.ntcrckr.peer.code.review.functions.local

import com.github.syari.kgit.KGit
import ru.ntcrckr.peer.code.review.functions.Copy
import ru.ntcrckr.peer.code.review.functions.path

fun cloneLocalRepository(
    sourceRepository: KGit,
    copy: Copy,
): KGit =
    KGit.cloneRepository {
        setURI(sourceRepository.path.toString())
        setDirectory(copy.local.folder.resolve(copy.local.name).toFile())
        setRemote(copy.local.localRemoteName)
        setCloneAllBranches(true)
    }