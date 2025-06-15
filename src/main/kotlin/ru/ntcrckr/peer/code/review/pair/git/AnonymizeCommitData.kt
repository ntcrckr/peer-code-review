package ru.ntcrckr.peer.code.review.pair.git

import com.github.syari.kgit.KGit
import ru.ntcrckr.peer.code.review.pair.ANON_USER_EMAIL
import ru.ntcrckr.peer.code.review.pair.ANON_USER_NAME

fun KGit.anonymizeCommitInfo(
    username: String = ANON_USER_NAME,
    email: String = ANON_USER_EMAIL,
): KGit = also {
    it.branchList().forEach { branch ->
        arrayOf(
            "git", "filter-branch", "-f", "--env-filter",
            "export GIT_AUTHOR_NAME=\"$username\";" +
                    "export GIT_AUTHOR_EMAIL=\"$email\";" +
                    "export GIT_COMMITTER_NAME=\"$username\";" +
                    "export GIT_COMMITTER_EMAIL=\"anon@example.com\"",
            branch.name
        ).executeIn(it.path)
    }
}