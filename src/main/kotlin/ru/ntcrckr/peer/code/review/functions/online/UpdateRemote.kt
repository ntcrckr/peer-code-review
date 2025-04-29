package ru.ntcrckr.peer.code.review.functions.online

import com.github.syari.kgit.KGit
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import ru.ntcrckr.peer.code.review.functions.Teacher

fun KGit.updateRemote(
    remoteName: String,
    teacher: Teacher?,
): KGit = also {
    push {
        remote = remoteName
        teacher?.githubToken?.also {
            setCredentialsProvider(UsernamePasswordCredentialsProvider(teacher.userName, teacher.githubToken))
        }
    }
}