package ru.ntcrckr.peer.code.review.functions.local

import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.ntcrckr.peer.code.review.functions.*
import kotlin.io.path.exists

class CopyFromLocalRemoteTest : RemoveRepositoriesAfterTest {
    @Test
    fun `copy commits from local remote`() {
        // given
        val sourceRepositoryName = "sourceRepo"
        val sourceInitialBranchName = "master"
        val sourceRepository = createTestLocalRepository(sourceRepositoryName, sourceInitialBranchName)

        val repositoryName = "dummyRepo"
        val remoteName = "source"
        val repository = cloneLocalRepository(sourceRepository, testRepositoriesPath, repositoryName, remoteName)

        val testFileName = "testFile"
        val testText = "test text"
        val testMessage = "test message"
        sourceRepository.addAndCommit(testFileName, testText, testMessage)
        // when
        repository.copyFromLocalRemote(remoteName)
        // then
        val remote = repository.remoteList().single()
        assertAll(
            { assertTrue(repository.repository.remoteNames.contains("source")) },
            { assertTrue(remote.urIs.single().path == sourceRepository.path.toString()) },
            { assertTrue(remote.name == remoteName) },
            { assertTrue(repository.path.resolve(testFileName).exists()) },
            { assertTrue(repository.path.resolve(testFileName).toFile().readText() == testText) },
            { assertTrue(repository.log().first().fullMessage == testMessage) },
        )
    }
}