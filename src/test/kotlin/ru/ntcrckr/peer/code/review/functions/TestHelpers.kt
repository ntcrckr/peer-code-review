package ru.ntcrckr.peer.code.review.functions

import com.github.syari.kgit.KGit
import ru.ntcrckr.peer.code.review.functions.helpers.createLocalRepository
import ru.ntcrckr.peer.code.review.pair.git.path
import java.nio.file.Path
import kotlin.io.path.Path

val testRepositoriesPath: Path = Path("").toAbsolutePath()
    .resolve("src/test/resources").resolve("testRepositories")

val testSourcePath: Path = testRepositoriesPath.resolve("source")

val testCopyPath: Path = testRepositoriesPath.resolve("copy")

fun createTestLocalRepository(
    repositoryName: String,
    initialBranchName: String = "master",
) = createLocalRepository(testRepositoriesPath, repositoryName, initialBranchName)
    .addAndCommit("initFile", "init text", "init commit")

fun KGit.addAndCommit(
    fileName: String,
    text: String,
    message: String,
): KGit = also {
    path.resolve(fileName).toFile()
        .also { it.createNewFile() }
        .writeText(text)
    add { addFilepattern(".") }
    commit { this.message = message }
}

fun getGitHubToken(): String = System.getenv("GITHUB_TOKEN")