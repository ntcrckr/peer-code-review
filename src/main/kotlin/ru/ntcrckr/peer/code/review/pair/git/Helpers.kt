package ru.ntcrckr.peer.code.review.pair.git

import com.github.syari.kgit.KGit
import java.nio.file.Path
import java.util.concurrent.TimeUnit

val KGit.path: Path get() = repository.workTree.toPath()

fun Array<String>.executeIn(workingDir: Path): String {
    val proc = ProcessBuilder(*this)
        .directory(workingDir.toFile())
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
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