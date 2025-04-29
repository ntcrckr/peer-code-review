package ru.ntcrckr.peer.code.review.functions.local

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import ru.ntcrckr.peer.code.review.functions.RemoveRepositoriesBeforeAndAfterTest
import ru.ntcrckr.peer.code.review.functions.createTestLocalRepository
import ru.ntcrckr.peer.code.review.functions.path
import kotlin.io.path.exists

class CreateLocalRepositoryPairTest : RemoveRepositoriesBeforeAndAfterTest {
    @Test
    fun `create dummy repository`() {
        // given
        val repositoryName = "dummyRepo"
        val initialBranchName = "master"
        // when
        val repository = createTestLocalRepository(repositoryName, initialBranchName)
        // then
        assertAll(
            { assertTrue(repository.path.exists()) },
            { assertTrue(repository.path.resolve(".git").exists()) },
            { assertTrue(repository.repository.branch == initialBranchName) },
        )
    }
}