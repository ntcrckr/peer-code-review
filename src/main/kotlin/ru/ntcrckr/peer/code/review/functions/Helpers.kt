package ru.ntcrckr.peer.code.review.functions

import com.github.syari.kgit.KGit
import com.jcabi.github.Pull
import com.jcabi.github.PullComment
import org.eclipse.jgit.transport.RemoteConfig
import java.lang.ProcessBuilder.Redirect.PIPE
import java.nio.file.Path
import java.util.*
import java.util.concurrent.TimeUnit

val KGit.path: Path get() = repository.workTree.toPath()

fun KGit.remoteBy(remoteName: String): RemoteConfig = remoteList().first { it.name == remoteName }

fun Array<String>.executeIn(workingDir: Path): String {
    val proc = ProcessBuilder(*this)
        .directory(workingDir.toFile())
        .redirectOutput(PIPE)
        .redirectError(PIPE)
        .start()

    proc.waitFor(10, TimeUnit.MINUTES)
    proc.errorStream
        .bufferedReader().readText()
        .ifNotEmpty { println("ERROR: $it") }
    return proc.inputStream.bufferedReader().readText()
}

inline fun String.ifNotEmpty(block: (String) -> Unit) {
    if (isNotEmpty())
        block(this)
}

fun Pull.smart(): Pull.Smart = Pull.Smart(this)

fun PullComment.smart(): PullComment.Smart = PullComment.Smart(this)

fun <T> T.runIf(predicate: Boolean, block: T.() -> T): T = when {
    predicate -> block()
    else -> this
}

fun String.nameOfCopy(prefix: String = "copyOf"): String =
    "$prefix${this.replaceFirstChar { it.titlecase(Locale.getDefault()) }}"

fun sshUrl(userName: String, repositoryName: String): String = "git@github.com:$userName/$repositoryName.git"