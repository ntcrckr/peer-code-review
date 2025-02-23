package ru.ntcrckr.peer.code.review.functions.local

import com.github.syari.kgit.KGit

fun KGit.copyFromLocalRemote(
    remoteName: String,
): KGit = also {
    val fetchResult = fetch {
        this.remote = remoteName
        isForceUpdate = true
    }
    val updatedRefNames = fetchResult.trackingRefUpdates.map { it.localName }
    updatedRefNames.parallelStream().forEach { updatedRefName ->
        pull {
            remote = remoteName
            remoteBranchName = updatedRefName.substringAfter("refs/remotes/$remoteName/")
        }
    }
}