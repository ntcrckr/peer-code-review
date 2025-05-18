//package ru.ntcrckr.peer.code.review.functions.online
//
//import org.junit.jupiter.api.Assertions.assertTrue
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.assertAll
//import ru.ntcrckr.peer.code.review.source.Source
//import ru.ntcrckr.peer.code.review.functions.*
//import ru.ntcrckr.peer.code.review.source.LocalSource
//import ru.ntcrckr.peer.code.review.source.OnlineSource
//import kotlin.io.path.exists
//
//class CloneRemoteRepositoryTest : RemoveRepositoriesBeforeAndAfterTest {
//    @Test
//    fun `clone notion-widgets repository from github`() {
//        // given
//        val teacher = Teacher("pcrp-teacher", getGitHubToken())
//        val source = Source(
//            OnlineSource("pcrp-student1", "some_repository", 1),
//            LocalSource(testSourcePath),
//        )
//        // when
//        val repository = cloneOnlineRepository(source, teacher)
//        // then
//        assertAll(
//            { assertTrue(repository.path.exists()) },
//            {
//                assertTrue(
//                    repository.remoteBy(source.local.onlineRemoteName).urIs.first().toString() == source.online.sshUrl
//                )
//            },
//        )
//    }
//}