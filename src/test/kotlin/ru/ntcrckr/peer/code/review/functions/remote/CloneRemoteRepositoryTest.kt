package ru.ntcrckr.peer.code.review.functions.remote

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import ru.ntcrckr.peer.code.review.functions.RemoveRepositoriesAfterTest
import ru.ntcrckr.peer.code.review.functions.path
import ru.ntcrckr.peer.code.review.functions.remoteBy
import ru.ntcrckr.peer.code.review.functions.testRepositoriesPath
import kotlin.io.path.exists

class CloneRemoteRepositoryTest : RemoveRepositoriesAfterTest {
    @Test
    fun `clone notion-widgets repository from github`() {
        // given
        val repositoryName = "notion_widgets"
        val username = "ntcrckr"
        val url = "https://github.com/$username/$repositoryName.git"
        val remoteName = "github"
        // when
        val repository = cloneRemoteRepository(url, testRepositoriesPath, repositoryName, remoteName)
        // then
        assertAll(
            { assertTrue(repository.path.exists()) },
            { assertTrue(repository.remoteBy(remoteName).urIs.first().toString() == url) },
        )
    }
}