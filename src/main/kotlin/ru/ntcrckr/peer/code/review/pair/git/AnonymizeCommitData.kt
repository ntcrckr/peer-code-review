package ru.ntcrckr.peer.code.review.pair.git

import com.github.syari.kgit.KGit

fun KGit.anonymizeCommitInfo(
    username: String,
    email: String,
): KGit = also {
    arrayOf(
        "git", "rebase", "-r", "--root", "--exec",
        "git commit --amend --no-edit --author '$username <$email>'"
    ).executeIn(it.path)
}