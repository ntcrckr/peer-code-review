package ru.ntcrckr.peer.code.review.pair.git

import com.github.syari.kgit.KGit
import org.slf4j.LoggerFactory
import java.nio.file.Path
import java.util.concurrent.TimeUnit

val KGit.path: Path get() = repository.workTree.toPath()

private val executeLog = LoggerFactory.getLogger("SHELL_EXECUTOR")
fun Array<String>.executeIn(workingDir: Path): String {
    executeLog.info("Starting $this")
    val pb = ProcessBuilder(*this)
        .directory(workingDir.toFile())
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
    pb.environment().put("FILTER_BRANCH_SQUELCH_WARNING", "1")
    val proc = pb.start()

    proc.waitFor(10, TimeUnit.MINUTES)
    proc.errorStream
        .bufferedReader().readText()
        .ifNotEmpty { println("ERROR: $it") }
    executeLog.info("Ending $this")
    return proc.inputStream.bufferedReader().readText()
}

inline fun String.ifNotEmpty(block: (String) -> Unit) {
    if (isNotEmpty())
        block(this)
}