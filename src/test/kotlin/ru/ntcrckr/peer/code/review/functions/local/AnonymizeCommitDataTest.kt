package ru.ntcrckr.peer.code.review.functions.local

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import ru.ntcrckr.peer.code.review.functions.*
import ru.ntcrckr.peer.code.review.functions.LocalRepositoryPair.Companion.DEFAULT_REMOTE_NAME
import ru.ntcrckr.peer.code.review.functions.LocalRepositoryPair.Companion.DEFAULT_USER_EMAIL
import ru.ntcrckr.peer.code.review.functions.LocalRepositoryPair.Companion.DEFAULT_USER_NAME

class AnonymizeCommitDataTest : RemoveRepositoriesAfterTest {
    @Test
    fun `clone repository and remove user info from commits`() {
        // given
        val pair = LocalRepositoryPair.createNew(testSourcePath, testCopyPath, "sourceRepo", "master", Config(true))
        pair.localSource.commitWithTestFile()
        pair.localCopy.copyFromLocalRemote(DEFAULT_REMOTE_NAME)
//        pair.copy.repository
        // when
        pair.localCopy.anonymizeCommitInfo(DEFAULT_USER_NAME, DEFAULT_USER_EMAIL)
        // then
        assertAll(
            { assertEquals(DEFAULT_USER_NAME, pair.localCopy.log().first().authorIdent.name) },
            { assertEquals(DEFAULT_USER_EMAIL, pair.localCopy.log().first().authorIdent.emailAddress) },
//            { assertEquals(DEFAULT_USER_NAME, pair.copy.log().first().committerIdent.name) },
//            { assertEquals(DEFAULT_USER_EMAIL, pair.copy.log().first().committerIdent.emailAddress) },
        )
    }
}