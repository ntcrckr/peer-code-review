package ru.ntcrckr.peer.code.review.functions.local

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import ru.ntcrckr.peer.code.review.functions.*
import ru.ntcrckr.peer.code.review.functions.Copy.Local.Companion.DEFAULT_SOURCE_LOCAL_REMOTE_NAME
import ru.ntcrckr.peer.code.review.functions.LocalRepositoryPair.Companion.ANON_USER_EMAIL
import ru.ntcrckr.peer.code.review.functions.LocalRepositoryPair.Companion.ANON_USER_NAME

class AnonymizeCommitDataTest : RemoveRepositoriesBeforeAndAfterTest {
    @Test
    fun `clone repository and remove user info from commits`() {
        // given
        val source = Source(
            Source.Online("pcrp-student1", "some_repository", 1),
            Source.Local(testSourcePath),
        )
        val copy = Copy(
            Copy.Local(testCopyPath, source.online.repositoryName.nameOfCopy()),
            Copy.Online("TODO", 1),
        )
        val pair = LocalRepositoryPair.createNew(
            Teacher("pcrp-teacher", getGitHubToken()),
            source,
            copy,
            testSourcePath,
            "sourceRepo",
            "master",
            Config(true),
        )
        pair.localSource.commitWithTestFile()
        pair.localCopy.copyFromRemote(DEFAULT_SOURCE_LOCAL_REMOTE_NAME)
//        pair.copy.repository
        // when
        pair.localCopy.anonymizeCommitInfo(ANON_USER_NAME, ANON_USER_EMAIL)
        // then
        assertAll(
            { assertEquals(ANON_USER_NAME, pair.localCopy.log().first().authorIdent.name) },
            { assertEquals(ANON_USER_EMAIL, pair.localCopy.log().first().authorIdent.emailAddress) },
//            { assertEquals(DEFAULT_USER_NAME, pair.copy.log().first().committerIdent.name) },
//            { assertEquals(DEFAULT_USER_EMAIL, pair.copy.log().first().committerIdent.emailAddress) },
        )
    }
}