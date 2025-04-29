package ru.ntcrckr.peer.code.review.functions.local

import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertTrue
import ru.ntcrckr.peer.code.review.functions.RemoveRepositoriesBeforeAndAfterTest
import ru.ntcrckr.peer.code.review.functions.createTestLocalRepository
import ru.ntcrckr.peer.code.review.functions.path
import kotlin.test.Test

class AddLocalRemoteTest : RemoveRepositoriesBeforeAndAfterTest {
    @Test
    fun `add source local remote`() {
        // given
        val sourceRepositoryName = "sourceRepo"
        val sourceInitialBranchName = "master"
        val sourceRepository = createTestLocalRepository(sourceRepositoryName, sourceInitialBranchName)
        val repositoryName = "dummyRepo"
        val initialBranchName = "master"
        val repository = createTestLocalRepository(repositoryName, initialBranchName)
        val remoteName = "source"
        // when
        repository.addLocalRemote(sourceRepository.path, remoteName)
        // then
        val remote = repository.remoteList().single()
        assertAll(
            { assertTrue(repository.repository.remoteNames.contains("source")) },
            { assertTrue(remote.urIs.single().path == sourceRepository.path.toString()) },
            { assertTrue(remote.name == remoteName) },
        )
    }
}