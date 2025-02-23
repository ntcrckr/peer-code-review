package ru.ntcrckr.peer.code.review.functions.local

import com.github.syari.kgit.KGit
import ru.ntcrckr.peer.code.review.functions.executeIn
import ru.ntcrckr.peer.code.review.functions.path

fun KGit.anonymizeCommitInfo(
    username: String,
    email: String,
): KGit = also {
    arrayOf(
        "git", "rebase", "-r", "--root", "--exec",
        "git commit --amend --no-edit --author '$username <$email>'"
    ).executeIn(it.path)
}