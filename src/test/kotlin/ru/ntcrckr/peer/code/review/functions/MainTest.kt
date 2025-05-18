package ru.ntcrckr.peer.code.review.functions

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import ru.ntcrckr.peer.code.review.pair.Config
import ru.ntcrckr.peer.code.review.pair.RepoPair
import ru.ntcrckr.peer.code.review.pair.users.Performer
import ru.ntcrckr.peer.code.review.pair.users.Reviewer
import ru.ntcrckr.peer.code.review.pair.users.Teacher
import java.time.Duration
import kotlin.time.toKotlinDuration

class MainTest : TestSetup {
    @Test
    fun `main test`(): Unit = runBlocking {
        val repoPair = repoPairFrom("https://github.com/pcrp-student1/other_repository/pull/2")
        repoPair.startUpdateCycle()
        delay(Duration.ofMinutes(5).toKotlinDuration())
        repoPair.stopUpdateCycle()
    }

    private fun repoPairFrom(
        pullUrl: String,
    ): RepoPair {
        val urlParts = pullUrl.split('/')
        val repoName = urlParts[urlParts.size - 3]
        val pullId = urlParts[urlParts.size - 1].toInt()
        return RepoPair(
            performer = Performer("pcrp-student1", repoName, pullId),
            teacher = Teacher("pcrp-teacher", getGitHubToken()),
            reviewer = Reviewer("pcrp-student2"),
            localSourcePath = testSourcePath,
            localCopyPath = testCopyPath,
            config = Config(anonymizeCommitInfo = false),
        )
    }
}