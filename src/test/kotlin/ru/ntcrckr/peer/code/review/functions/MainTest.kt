package ru.ntcrckr.peer.code.review.functions

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import ru.ntcrckr.peer.code.review.functions.Copy.Local.Companion.DEFAULT_SOURCE_LOCAL_REMOTE_NAME
import ru.ntcrckr.peer.code.review.functions.local.cloneLocalRepository
import ru.ntcrckr.peer.code.review.functions.online.cloneOnlineRepository
import java.time.Duration
import kotlin.io.path.exists
import kotlin.time.toKotlinDuration

class MainTest : RemoveRepositoriesBeforeAndAfterTest {
    @Test
    fun `create new`(): Unit = runBlocking {
        val teacher = Teacher("pcrp-teacher", getGitHubToken())
        val source = Source(
            Source.Online("pcrp-student1", "some_repository", 1),
            Source.Local(testSourcePath),
        )
        val copy = Copy(
            Copy.Local(testCopyPath, source.online.repositoryName.nameOfCopy()),
            Copy.Online("some_repository", 1),
        )
        val config = Config(true)
        val pair = LocalRepositoryPair.fromOnlineSource(teacher, source, copy, config)

        pair.setup()
        pair.startUpdateCycle()
        delay(Duration.ofMinutes(5).toKotlinDuration())
        pair.stopUpdateCycle()
    }

    @Test
    fun `from existing`() {
        val teacher = Teacher("pcrp-teacher", getGitHubToken())
        val source = Source(
            Source.Online("pcrp-student1", "some_repository", 1),
            Source.Local(testSourcePath),
        )
        val copy = Copy(
            Copy.Local(testCopyPath, source.online.repositoryName.nameOfCopy()),
            Copy.Online("some_repository", 1),
        )
        val config = Config(true)
        val localSource = cloneOnlineRepository(source, teacher)
        cloneLocalRepository(localSource, copy)

        val pair = LocalRepositoryPair.fromExisting(teacher, source, copy, config)
    }

    @Test
    fun `test main lifecycle`() {
        val teacher = Teacher("pcrp-teacher", getGitHubToken())
        val source = Source(
            Source.Online("pcrp-student1", "some_repository", 1),
            Source.Local(testSourcePath),
        )
        val copy = Copy(
            Copy.Local(testCopyPath, source.online.repositoryName.nameOfCopy()),
            Copy.Online("some_repository", 1),
        )
        val sourceRepository =
            cloneOnlineRepository(source, teacher)
        val localRepositoryPair = LocalRepositoryPair.fromLocalSource(
            teacher,
            source,
            copy,
            sourceRepository,
            Config(true),
        )

//        localRepositoryPair.createOnlineCopy()
        localRepositoryPair.addOnlineCopyRemote()
        localRepositoryPair.updateOnlineCopyFromLocalCopy()
        localRepositoryPair.copyPullRequestFromSourceToCopy()

        val testFileName = "testFile"
        val testText = "test text"
        val testMessage = "test message"
        localRepositoryPair.localSource.addAndCommit(testFileName, testText, testMessage)

        localRepositoryPair.updateLocalCopyFromLocalSource()

        val remote = localRepositoryPair.localCopy.remoteList().single()
        assertAll(
            { assertTrue(localRepositoryPair.localCopy.repository.remoteNames.contains("source")) },
            { assertEquals(sourceRepository.path.toString(), remote.urIs.single().path) },
            { assertEquals(DEFAULT_SOURCE_LOCAL_REMOTE_NAME, remote.name) },
            { assertTrue(localRepositoryPair.localCopy.path.resolve(testFileName).exists()) },
            { assertEquals(testText, localRepositoryPair.localCopy.path.resolve(testFileName).toFile().readText()) },
            { assertEquals(testMessage.trim(), localRepositoryPair.localCopy.log().first().fullMessage.trim()) },
        )
    }
}