package ru.ntcrckr.peer.code.review.functions

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import ru.ntcrckr.peer.code.review.functions.LocalRepositoryPair.Companion.DEFAULT_REMOTE_NAME
import ru.ntcrckr.peer.code.review.functions.remote.cloneRemoteRepository
import kotlin.io.path.exists

class MainTest : RemoveRepositoriesAfterTest {
    @Test
    fun `test main lifecycle`() {
        val repositoryName = "notion_widgets"
        val username = "ntcrckr"
        val url = "https://github.com/$username/$repositoryName.git"
        val remoteName = "github"
        val sourceRepository = cloneRemoteRepository(url, testSourcePath, repositoryName, remoteName)

        val localRepositoryPair =
            LocalRepositoryPair.fromLocalSource(sourceRepository, testCopyPath, repositoryName, Config(true))

        val testFileName = "testFile"
        val testText = "test text"
        val testMessage = "test message"
        localRepositoryPair.localSource.addAndCommit(testFileName, testText, testMessage)

        localRepositoryPair.updateLocalCopy()

        val remote = localRepositoryPair.localCopy.remoteList().single()
        assertAll(
            { assertTrue(localRepositoryPair.localCopy.repository.remoteNames.contains("source")) },
            { assertEquals(sourceRepository.path.toString(), remote.urIs.single().path) },
            { assertEquals(DEFAULT_REMOTE_NAME, remote.name) },
            { assertTrue(localRepositoryPair.localCopy.path.resolve(testFileName).exists()) },
            { assertEquals(testText, localRepositoryPair.localCopy.path.resolve(testFileName).toFile().readText()) },
            { assertEquals(testMessage.trim(), localRepositoryPair.localCopy.log().first().fullMessage.trim()) },
        )
    }
}