package ru.ntcrckr.peer.code.review.pair

import com.jcabi.github.Github
import com.jcabi.github.RtGithub
import org.slf4j.LoggerFactory
import ru.ntcrckr.peer.code.review.dao.RepoEntity
import ru.ntcrckr.peer.code.review.dao.RepoPairEntity
import ru.ntcrckr.peer.code.review.dao.RepoPairs
import ru.ntcrckr.peer.code.review.dao.UserEntity
import ru.ntcrckr.peer.code.review.pair.copy.Copy
import ru.ntcrckr.peer.code.review.pair.source.Source
import ru.ntcrckr.peer.code.review.pair.users.Performer
import ru.ntcrckr.peer.code.review.pair.users.Reviewer
import ru.ntcrckr.peer.code.review.pair.users.Teacher
import java.nio.file.Path
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.TimeUnit

class RepoPair(
    performer: Performer,
    teacher: Teacher,
    reviewer: Reviewer,
    localSourcePath: Path,
    localCopyPath: Path,
    config: Config,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val scheduler = Executors.newScheduledThreadPool(1)
    private val github: Github = RtGithub(teacher.githubToken)

    private val pairId: Int = pcrpTransaction {
        RepoPairs.getIdOrInsert(
            -1,
            RepoPairEntity(
                teacher = UserEntity(teacher.username),
                performer = UserEntity(performer.username),
                sourceRepo = RepoEntity(performer.repoName, performer.pullId),
                reviewer = UserEntity(reviewer.username),
                copyRepo = TODO(),
            )
        )
    }

    val source = Source.init(pairId, github, performer, teacher.credentialsProvider, localSourcePath)
    val copy = Copy(pairId, github, performer.repoName.nameOfCopy(), teacher, reviewer, source, localCopyPath, config)

    fun startUpdateCycle(delay: Duration = Duration.ofMinutes(1L)) {
        runCatching {
            logger.info("Starting update cycle")
            scheduler.scheduleAtFixedRate(::updateCycle, 0L, delay.seconds, TimeUnit.SECONDS)
        }.getOrElse {
            when (it) {
                is RejectedExecutionException -> logger.info("Update Cycle Job is already running")
                else -> {
                    logger.info("Starting update cycle failed, ignoring:")
                    logger.debug(it.stackTraceToString())
                }
            }
        }
    }

    fun stopUpdateCycle() {
        logger.info("Stopping update cycle")
        scheduler.shutdown()
    }

    private fun updateCycle() {
        logger.info("Another update cycle")
        source.local.updateFromOnline()
        copy.local.updateFromLocalSource()
        copy.local.updateOnlineCopy()
        copyCommentsFromCopyToSource()
        copyCommentsFromSourceToCopy()
    }

    private fun copyCommentsFromCopyToSource() {
        val pullComments = copy.online.pullComments
        val allCodeComments = copy.online.pullCodeComments
        pcrpTransaction {
            source.online.addPullComments(pullComments)
            source.online.addCodeComments(allCodeComments)
        }
    }

    private fun copyCommentsFromSourceToCopy() {
        val pullComments = copy.online.pullComments
        val allCodeComments = copy.online.pullCodeComments
        pcrpTransaction {
            source.online.addPullComments(pullComments)
            source.online.addCodeComments(allCodeComments)
        }
    }
}