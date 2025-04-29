package ru.ntcrckr.peer.code.review.functions.local

import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.ntcrckr.peer.code.review.functions.*
import kotlin.io.path.exists

class CloneLocalRepositoryPairTest : RemoveRepositoriesBeforeAndAfterTest {
    @Test
    fun `clone dummy repository`() {
        // given
        val repositoryName = "dummyRepo"
        val copy = Copy(
            Copy.Local(
                testCopyPath,
                repositoryName,
            ),
            Copy.Online(repositoryName, 1, 0),
        )

        val sourceRepositoryName = "sourceRepo"
        val sourceInitialBranchName = "master"
        val sourceRepository = createTestLocalRepository(sourceRepositoryName, sourceInitialBranchName)

        val testFileName = "testFile"
        val testText = "test text"
        val testMessage = "test message"
        sourceRepository.addAndCommit(testFileName, testText, testMessage)
        // when
        val repository = cloneLocalRepository(sourceRepository, copy)
        // then
        val remote = repository.remoteList().single()
        assertAll(
            { assertTrue(repository.repository.remoteNames.contains("source")) },
            { assertTrue(remote.urIs.single().path == sourceRepository.path.toString()) },
            { assertTrue(remote.name == copy.local.localRemoteName) },
            { assertTrue(repository.path.resolve(testFileName).exists()) },
            { assertTrue(repository.path.resolve(testFileName).toFile().readText() == testText) },
            { assertTrue(repository.log().first().fullMessage == testMessage) },
        )
    }
}