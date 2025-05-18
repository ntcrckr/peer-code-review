package ru.ntcrckr.peer.code.review.pair.copy

import com.jcabi.github.Coordinates
import com.jcabi.github.Github
import com.jcabi.github.Pull
import com.jcabi.github.Repo
import org.slf4j.LoggerFactory
import ru.ntcrckr.peer.code.review.pair.Online
import ru.ntcrckr.peer.code.review.pair.github.*
import ru.ntcrckr.peer.code.review.pair.users.Reviewer
import ru.ntcrckr.peer.code.review.pair.users.Teacher

class OnlineCopy(
    pairId: Int,
    override val repo: Repo,
    pullId: Int,
) : Online(isSource = false, pairId, pullId) {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)

        fun from(
            pairId: Int,
            gitHub: Github,
            teacher: Teacher,
            reviewer: Reviewer,
            repoName: String,
            localCopy: LocalCopy,
            sourcePull: Pull.Smart,
        ): OnlineCopy {
            val repo = gitHub.createOrGetRepo(teacher.username, repoName)
            localCopy.addRemote(Coordinates.Simple(teacher.username, repoName))
            localCopy.updateOnlineCopy()
            val online = OnlineCopy(pairId = pairId, repo = repo, pullId = repo.copyOrGetPullId(sourcePull))
            online.repo.addCollaboratorIfNotExists(reviewer)
            return online
        }

        private fun Github.createOrGetRepo(username: String, repoName: String): Repo =
            runCatching {
                createRepo(repoName)
            }.getOrElse {
                logger.info("Creating online copy failed, ignoring:")
                logger.debug(it.stackTraceToString())
                repos()[Coordinates.Simple(username, repoName)]
            }

        private fun Repo.copyOrGetPullId(sourcePull: Pull.Smart): Int =
            runCatching { copyPull(sourcePull).number() }
                .getOrElse { pulls().firstByTitle(sourcePull.title()).id }

        private fun Repo.addCollaboratorIfNotExists(reviewer: Reviewer): Unit =
            runCatching {
                addCollaborator(reviewer.username)
            }.getOrElse {
                logger.info("Adding reviewer to online copy failed, ignoring:")
                logger.debug(it.stackTraceToString())
            }
    }
}