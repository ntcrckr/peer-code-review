//package ru.ntcrckr.peer.code.review.functions
//
//import com.github.syari.kgit.KGit
//import com.jcabi.github.*
//import org.slf4j.LoggerFactory
//import ru.ntcrckr.peer.code.review.pair.copy.Copy
//import ru.ntcrckr.peer.code.review.pair.source.Source
//import ru.ntcrckr.peer.code.review.functions.local.*
//import ru.ntcrckr.peer.code.review.functions.online.cloneOnlineRepository
//import ru.ntcrckr.peer.code.review.functions.online.updateRemote
//import ru.ntcrckr.peer.code.review.pair.users.Reviewer
//import ru.ntcrckr.peer.code.review.pair.users.Teacher
//import java.nio.file.Path
//import java.time.Duration
//import java.util.concurrent.Executors
//import java.util.concurrent.RejectedExecutionException
//import java.util.concurrent.TimeUnit
//
//class LocalRepositoryPair private constructor(
//    val teacher: Teacher,
//    val reviewer: Reviewer,
//    val source: Source,
//    val copy: Copy,
//    val localSource: KGit,
//    val localCopy: KGit,
//    private val config: Config,
//) {
//    private val logger = LoggerFactory.getLogger(this::class.java)
//    private val gitHub: Github = RtGithub(teacher.githubToken)
//
//    private val scheduler = Executors.newScheduledThreadPool(1)
//
//    fun setup() {
//        updateLocalSourceFromOnlineSource()
//        updateLocalCopyFromLocalSource()
//        createOnlineCopyIfNotExists()
//        addOnlineCopyRemote()
//        updateOnlineCopyFromLocalCopy()
//        updateOnlineSourceFromOnlineCopy()
//        addReviewerAsCollaboratorIfNotExists()
//    }
//
//    fun startUpdateCycle(delay: Duration = Duration.ofMinutes(1L)) {
//        try {
//            logger.info("Starting update cycle")
//            scheduler.scheduleAtFixedRate(
//                {
//                    logger.info("Another update cycle")
//                    updateLocalSourceFromOnlineSource()
//                    updateLocalCopyFromLocalSource()
//                    updateOnlineCopyFromLocalCopy()
//                    updateOnlineSourceFromOnlineCopy()
//                },
//                0L,
//                delay.seconds,
//                TimeUnit.SECONDS,
//            )
//        } catch (e: RejectedExecutionException) {
//            logger.info("Update Cycle Job is already running")
//        }
//    }
//
//    fun stopUpdateCycle() {
//        logger.info("Stopping update cycle")
//        scheduler.shutdown()
//    }
//
//    fun updateLocalSourceFromOnlineSource() {
//        localSource.copyFromRemote(source.local.onlineRemoteName, teacher.credentialsProvider)
//        localSource.checkoutAllRemoteBranches(source.local.onlineRemoteName)
//    }
//
//    fun updateLocalCopyFromLocalSource() {
//        localCopy.copyFromRemote(copy.local.localRemoteName)
//        localCopy.checkoutAllRemoteBranches(copy.local.localRemoteName)
//        if (config.anonymizeCommitInfo)
//            localCopy.anonymizeCommitInfo(ANON_USER_NAME, ANON_USER_EMAIL)
//    }
//
//    fun createOnlineCopy() {
//        gitHub.repos().create(Repos.RepoCreate(source.online.repoCoordinates.repo(), true))
//    }
//
//    fun createOnlineCopyIfNotExists() {
//        try {
//            createOnlineCopy()
//        } catch (e: Throwable) {
//            logger.info("Creating online copy failed, ignoring:")
//            logger.debug(e.stackTraceToString())
//        }
//    }
//
//    fun addOnlineCopyRemote() {
//        localCopy.addOnlineRemote(
//            Coordinates.Simple(teacher.username, copy.online.repositoryName),
//            copy.local.onlineRemoteName,
//        )
//    }
//
//    fun updateOnlineCopyFromLocalCopy() {
//        localCopy.updateRemote(copy.local.onlineRemoteName, teacher.credentialsProvider)
//    }
//
//    fun addReviewerAsCollaborator() {
//        copy.online.addAsCollaboratorIfNotExists(reviewer.username)
//    }
//
//    fun addReviewerAsCollaboratorIfNotExists() {
//        try {
//            addReviewerAsCollaborator()
//        } catch (e: Throwable) {
//            logger.info("Adding reviewer to online copy failed, ignoring:")
//            logger.debug(e.stackTraceToString())
//        }
//    }
//
//    fun updateOnlineSourceFromOnlineCopy() {
//        val copyPull = copy.online.pullRequest
//        val pullComments = copyPull
//            .issue().smart()
//            .comments().iterate(copyPull.createdAt())
//            .map { it.smart() }
//        val (replies, comments) = copyPull
//            .comments().iterate(mapOf())
//            .map { it.smart() }
//            .partition { runCatching { it.reply() }.isSuccess }
//
//        val sourcePull = source.online.pullRequest
//        val sourcePullComments = sourcePull.issue().comments()
//        pullComments.forEach {
//            sourcePullComments.post(it.body())
//        }
//        val sourceComments = sourcePull.comments()
//        replies.forEach {
//            sourceComments.reply(it.body(), it.reply())
//        }
//        comments.forEach {
//            sourceComments.post(it.body(), it.commitId(), it.json().getString("path"), it.json().getInt("position"))
//        }
//    }
//
//    companion object {
//        const val ANON_USER_NAME = "Some User"
//        const val ANON_USER_EMAIL = "some@user.com"
//
//        fun createNew(
//            teacher: Teacher,
//            reviewer: Reviewer,
//            source: Source,
//            copy: Copy,
//            localSourceFolder: Path,
//            repositoryName: String,
//            initialBranchName: String,
//            config: Config,
//        ): LocalRepositoryPair {
//            val sourceCopy = createLocalRepository(localSourceFolder, repositoryName, initialBranchName)
//            val localCopy = cloneLocalRepository(sourceCopy, copy.local)
//            return LocalRepositoryPair(teacher, reviewer, source, copy, sourceCopy, localCopy, config)
//        }
//
//        fun fromExisting(
//            teacher: Teacher,
//            reviewer: Reviewer,
//            source: Source,
//            copy: Copy,
//            config: Config,
//        ): LocalRepositoryPair {
//            val localSource = KGit.open(source.local.path.toFile())
//            val localCopy = KGit.open(copy.local.path.toFile())
//            return LocalRepositoryPair(teacher, reviewer, source, copy, localSource, localCopy, config)
//        }
//
//        fun fromLocalSource(
//            teacher: Teacher,
//            reviewer: Reviewer,
//            source: Source,
//            copy: Copy,
//            sourceRepository: KGit,
//            config: Config,
//        ): LocalRepositoryPair {
//            val localCopy = cloneLocalRepository(sourceRepository, copy.local)
//            return LocalRepositoryPair(teacher, reviewer, source, copy, sourceRepository, localCopy, config)
//        }
//
//        fun fromOnlineSource(
//            teacher: Teacher,
//            reviewer: Reviewer,
//            source: Source,
//            copy: Copy,
//            config: Config,
//        ): LocalRepositoryPair {
//            val localSource = cloneOnlineRepository(source, teacher)
//            val localCopy = cloneLocalRepository(localSource, copy.local)
//            return LocalRepositoryPair(teacher, reviewer, source, copy, localSource, localCopy, config)
//        }
//    }
//}
