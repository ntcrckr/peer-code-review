package ru.ntcrckr.peer.code.review.pair.git

import com.github.syari.kgit.KGit
import org.eclipse.jgit.revwalk.RevCommit

fun KGit.getCommits(): Set<RevCommit> =
    branchList()
        .flatMap { branch -> log { add(branch.objectId) } }
        .distinctBy { it.name }
        .toSet()