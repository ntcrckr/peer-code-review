package ru.ntcrckr.peer.code.review.pair.git

import com.github.syari.kgit.KGit
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM
import org.eclipse.jgit.api.ListBranchCommand.ListMode.REMOTE

fun KGit.checkoutAllRemoteBranches(
    remoteName: String,
): KGit = also {
    val localObjectIds = it.branchList().map { it.objectId }
    val remotes = it.branchList { setListMode(REMOTE) }
    val currentBranch = it.repository.branch
    remotes
        .filter { remote -> remote.objectId !in localObjectIds }
        .forEach { branch ->
            it.checkout {
                setCreateBranch(true)
                setName(branch.name.substringAfter("refs/remotes/$remoteName/"))
                setUpstreamMode(SET_UPSTREAM)
                setStartPoint(branch.name.substringAfter("refs/remotes/"))
            }
        }
    it.checkout { setName(currentBranch) }
}