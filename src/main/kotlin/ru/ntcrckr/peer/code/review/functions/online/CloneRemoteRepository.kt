package ru.ntcrckr.peer.code.review.functions.online

import com.github.syari.kgit.KGit
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import ru.ntcrckr.peer.code.review.functions.Source
import ru.ntcrckr.peer.code.review.functions.Teacher

fun cloneOnlineRepository(
    source: Source,
    teacher: Teacher? = null,
): KGit = KGit.cloneRepository {
    setURI(source.online.sshUrl)
    setDirectory(source.local.folder.resolve(source.online.repositoryName).toFile())
    setRemote(source.local.onlineRemoteName)
    setCloneAllBranches(true)
    teacher?.githubToken?.also {
        setCredentialsProvider(UsernamePasswordCredentialsProvider(teacher.userName, teacher.githubToken))
    }
}