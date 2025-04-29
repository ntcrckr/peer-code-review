package ru.ntcrckr.peer.code.review.functions

import com.github.syari.kgit.KGit
import com.jcabi.github.*
import org.slf4j.LoggerFactory
import ru.ntcrckr.peer.code.review.functions.local.*
import ru.ntcrckr.peer.code.review.functions.online.cloneOnlineRepository
import ru.ntcrckr.peer.code.review.functions.online.updateRemote
import java.nio.file.Path
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.TimeUnit

class LocalRepositoryPair private constructor(
    val teacher: Teacher,
    val source: Source,
    val copy: Copy,
    val localSource: KGit,
    val localCopy: KGit,
    private val config: Config,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val gitHub: Github = RtGithub(teacher.githubToken)
    private val gitHubSource: Repo
        get() = gitHub.repos()[Coordinates.Simple(source.online.username, source.online.repositoryName)]
    private val gitHubCopy: Repo
        get() = gitHub.repos()[Coordinates.Simple(teacher.userName, copy.online.repositoryName)]

    private val scheduler = Executors.newScheduledThreadPool(1)

    fun setup() {
        createOnlineCopyIfNotExists()
        copyPullRequestFromSourceToCopyIfNotExists()
    }

    fun startUpdateCycle(delay: Duration = Duration.ofMinutes(1L)) {
        try {
            logger.info("Starting update cycle")
            scheduler.scheduleAtFixedRate(
                {
                    updateLocalSourceFromOnlineSource()
                    updateLocalCopyFromLocalSource()
                    updateOnlineCopyFromLocalCopy()
                    copyPullRequestFromSourceToCopy()
                    updateOnlineSourceFromOnlineCopy()
                },
                0L,
                delay.seconds,
                TimeUnit.SECONDS,
            )
        } catch (e: RejectedExecutionException) {
            logger.info("Update Cycle Job is already running")
        }
    }

    fun stopUpdateCycle() {
        logger.info("Stopping update cycle")
        scheduler.shutdown()
    }

    fun updateLocalSourceFromOnlineSource() {
        localSource.copyFromRemote(source.local.onlineRemoteName, teacher)
    }

    fun updateLocalCopyFromLocalSource() {
        localCopy.copyFromRemote(copy.local.localRemoteName)
            .runIf(config.anonymizeCommitInfo) { anonymizeCommitInfo(ANON_USER_NAME, ANON_USER_EMAIL) }
    }

    fun createOnlineCopy() {
        gitHub.repos().create(Repos.RepoCreate(gitHubSource.coordinates().repo(), true))
    }

    fun createOnlineCopyIfNotExists() {
        try {
            createOnlineCopy()
        } catch (e: Throwable) {
            logger.info("Creating online copy failed, ignoring:")
            logger.error(e.stackTraceToString())
        }
    }

    fun addOnlineCopyRemote() {
        localCopy.addOnlineRemote(
            Coordinates.Simple(teacher.userName, copy.online.repositoryName),
            copy.local.onlineRemoteName,
        )
    }

    fun updateOnlineCopyFromLocalCopy() {
        localCopy.updateRemote(copy.local.onlineRemoteName, teacher)
    }

    fun copyPullRequestFromSourceToCopy() {
        val sourcePullRequest = gitHubSource.pulls().get(source.online.pullRequestNumber).smart()
        val baseRef = sourcePullRequest.base().ref()
        val headRef = sourcePullRequest.head().ref()
        gitHubCopy.pulls().create(sourcePullRequest.title(), baseRef, headRef)
    }

    fun copyPullRequestFromSourceToCopyIfNotExists() {
        try {
            copyPullRequestFromSourceToCopy()
        } catch (e: Throwable) {
            logger.info("Creating pull request copy failed, ignoring:")
            logger.error(e.stackTraceToString())
        }
    }

    fun updateOnlineSourceFromOnlineCopy() {
        val (replies, comments) = gitHubCopy.pulls()[copy.online.pullRequestNumber]
            .smart().comments().iterate(copy.online.lastComment, mapOf())
            .map { it.smart() }
            .partition { runCatching { it.reply() }.isSuccess }
        val sourceComments = gitHubSource.pulls()[source.online.pullRequestNumber].smart().comments()
        replies.forEach {
            sourceComments.reply(it.body(), it.reply())
        }
        comments.forEach {
            sourceComments.post(it.body(), it.commitId(), it.json().getString("path"), it.json().getInt("position"))
        }
    }

    companion object {
        const val ANON_USER_NAME = "Some User"
        const val ANON_USER_EMAIL = "some@user.com"

        fun createNew(
            teacher: Teacher,
            source: Source,
            copy: Copy,
            localSourceFolder: Path,
            repositoryName: String,
            initialBranchName: String,
            config: Config,
        ): LocalRepositoryPair {
            val sourceCopy = createLocalRepository(localSourceFolder, repositoryName, initialBranchName)
            val localCopy = cloneLocalRepository(sourceCopy, copy)
            return LocalRepositoryPair(teacher, source, copy, sourceCopy, localCopy, config)
        }

        fun fromExisting(
            teacher: Teacher,
            source: Source,
            copy: Copy,
            config: Config,
        ): LocalRepositoryPair {
            val localSource = KGit.open(source.local.folder.toFile())
            val localCopy = KGit.open(copy.local.folder.toFile())
            return LocalRepositoryPair(teacher, source, copy, localSource, localCopy, config)
        }

        fun fromLocalSource(
            teacher: Teacher,
            source: Source,
            copy: Copy,
            sourceRepository: KGit,
            config: Config,
        ): LocalRepositoryPair {
            val localCopy =
                cloneLocalRepository(sourceRepository, copy)
            return LocalRepositoryPair(teacher, source, copy, sourceRepository, localCopy, config)
        }

        fun fromOnlineSource(
            teacher: Teacher,
            source: Source,
            copy: Copy,
            config: Config,
        ): LocalRepositoryPair {
            val localSource = cloneOnlineRepository(source, teacher)
            val localCopy = cloneLocalRepository(localSource, copy)
            return LocalRepositoryPair(teacher, source, copy, localSource, localCopy, config)
        }
    }
}
