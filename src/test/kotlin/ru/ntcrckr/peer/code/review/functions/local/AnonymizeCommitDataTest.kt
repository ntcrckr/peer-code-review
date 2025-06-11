//package ru.ntcrckr.peer.code.review.functions.local
//
//import org.junit.jupiter.api.Assertions.assertEquals
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.assertAll
//import ru.ntcrckr.peer.code.review.pair.RepoPair
//import ru.ntcrckr.peer.code.review.functions.*
//import ru.ntcrckr.peer.code.review.functions.LocalRepositoryPair.Companion.ANON_USER_EMAIL
//import ru.ntcrckr.peer.code.review.functions.LocalRepositoryPair.Companion.ANON_USER_NAME
//import ru.ntcrckr.peer.code.review.pair.Config
//import ru.ntcrckr.peer.code.review.pair.git.anonymizeCommitInfo
//import ru.ntcrckr.peer.code.review.pair.users.Performer
//import ru.ntcrckr.peer.code.review.pair.users.Reviewer
//import ru.ntcrckr.peer.code.review.pair.users.Teacher
//
//class AnonymizeCommitDataTest : RemoveRepositoriesBeforeAndAfterTest {
//    @Test
//    fun `clone repository and remove user info from commits`() {
//        // given
//        val repoPair = RepoPair(
//            performer = Performer("pcrp-student1", "some_repository", 1),
//            teacher = Teacher("pcrp-teacher", getGitHubToken()),
//            reviewer = Reviewer("pcrp-student2"),
//            localSourcePath = testSourcePath,
//            localCopyPath = testCopyPath,
//            config = Config(anonymizeCommitInfo = false),
//        )
//        // when
//        repoPair.copy.local.repo.anonymizeCommitInfo(ANON_USER_NAME, ANON_USER_EMAIL)
//        // then
//        assertAll(
//            { assertEquals(ANON_USER_NAME, repoPair.copy.local.repo.log().first().authorIdent.name) },
//            { assertEquals(ANON_USER_EMAIL, repoPair.copy.local.repo.log().first().authorIdent.emailAddress) },
////            { assertEquals(DEFAULT_USER_NAME, repoPair.copy.local.repo.log().first().committerIdent.name) },
////            { assertEquals(DEFAULT_USER_EMAIL, repoPair.copy.local.repo.log().first().committerIdent.emailAddress) },
//        )
//    }
//}