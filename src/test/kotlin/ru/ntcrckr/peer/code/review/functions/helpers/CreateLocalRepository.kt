package ru.ntcrckr.peer.code.review.functions.helpers

import com.github.syari.kgit.KGit
import java.nio.file.Path

fun createLocalRepository(
    path: Path,
    repositoryName: String,
    initialBranchName: String,
): KGit =
    KGit.init {
        setDirectory(path.resolve(repositoryName).toFile())
        setInitialBranch(initialBranchName)
    }