package ru.ntcrckr.peer.code.review.functions

import com.github.syari.kgit.KGit
import org.eclipse.jgit.transport.RemoteConfig
import java.lang.ProcessBuilder.Redirect.PIPE
import java.nio.file.Path
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

fun String.executeIn(workingDir: Path): String =
    split("\\s".toRegex()).toTypedArray<String>().executeIn(workingDir)

inline fun String.ifNotEmpty(block: (String) -> Unit) {
    if (isNotEmpty())
        block(this)
}