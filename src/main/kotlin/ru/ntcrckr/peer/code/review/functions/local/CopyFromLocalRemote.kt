package ru.ntcrckr.peer.code.review.functions.local

import com.github.syari.kgit.KGit
import org.eclipse.jgit.transport.FetchResult
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import ru.ntcrckr.peer.code.review.functions.Teacher

fun KGit.copyFromRemote(
    remoteName: String,
    teacher: Teacher? = null,
): KGit = also {
    fetch {
        this.remote = remoteName
        isForceUpdate = true
        teacher?.githubToken?.also {
            setCredentialsProvider(UsernamePasswordCredentialsProvider(teacher.userName, teacher.githubToken))
        }
    }.updatedRemotes
        .parallelStream()
        .forEach {
            pull {
                remote = remoteName
                remoteBranchName = it.substringAfter("refs/remotes/$remoteName/")
                teacher?.githubToken?.also {
                    setCredentialsProvider(UsernamePasswordCredentialsProvider(teacher.userName, teacher.githubToken))
                }
            }
        }
}

private val FetchResult.updatedRemotes: List<String> get() = trackingRefUpdates.map { it.localName }