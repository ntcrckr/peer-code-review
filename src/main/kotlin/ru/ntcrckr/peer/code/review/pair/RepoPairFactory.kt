package ru.ntcrckr.peer.code.review.pair

import com.github.syari.kgit.KGit
import com.jcabi.github.Coordinates
import com.jcabi.github.Github
import com.jcabi.github.Repo
import com.jcabi.github.RtGithub
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.slf4j.LoggerFactory
import ru.ntcrckr.peer.code.review.dao.*
import ru.ntcrckr.peer.code.review.pair.copy.*
import ru.ntcrckr.peer.code.review.pair.git.cloneLocalRepository
import ru.ntcrckr.peer.code.review.pair.git.cloneOnlineRepository
import ru.ntcrckr.peer.code.review.pair.git.path
import ru.ntcrckr.peer.code.review.pair.github.*
import ru.ntcrckr.peer.code.review.pair.source.*

data class RepoPairInitRequest(
    val lessonId: Int,
    val sourcePullUrl: String,
    val reviewerUsername: String,
) {
    val performerUsername: String
    val sourceRepoName: String
    val sourcePrId: Int

    init {
        val (username, repoName, prId) = sourcePullUrl.parseGitHubPrUrl()
        performerUsername = username
        sourceRepoName = repoName
        sourcePrId = prId
    }

    val sourceSshUrl: String = sshUrl(performerUsername, sourceRepoName)
}

class RepoPairFactory(
    private val appSetupEntity: AppSetupEntity,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val github: Github = RtGithub(appSetupEntity.githubToken)
    private val credentialsProvider =
        UsernamePasswordCredentialsProvider(appSetupEntity.teacherUsername, appSetupEntity.githubToken)

    suspend fun createRepoPair(request: RepoPairInitRequest): RepoPair {
        val source = constructSource(request)
        val copy = constructCopy(request, source, ConfigEntity(false))
        val repoPairEntity = RepoPairEntity(
            performer = UserEntity(request.performerUsername),
            sourceRepo = RepoEntity(request.sourceRepoName, request.sourcePrId),
            sourceLocalRepo = LocalRepoEntity(source.local.repo.path),
            reviewer = UserEntity(request.reviewerUsername),
            copyRepo = RepoEntity(copy.online.repo.coordinates().repo(), copy.online.pullId),
            copyLocalRepo = LocalRepoEntity(copy.local.repo.path),
            config = ConfigEntity(false),
        )
        val repoPairId = suspendPcrpTransaction { RepoPairs.insert(request.lessonId, repoPairEntity) }
        val bareRepoPair = BareRepoPair(source, copy)
        return bareRepoPair.toFull(repoPairId)
    }

    fun existingRepoPair(repoPairId: Int): RepoPair? {
        val entity = RepoPairs.get(repoPairId) ?: return null
        val source = Source(
            online = OnlineSource(
                pairId = repoPairId,
                repo = github.repos()[Coordinates.Simple(entity.performer.username, entity.sourceRepo.name)],
                pullId = entity.sourceRepo.pullId,
            ),
            local = LocalSource(
                repo = KGit.open(entity.sourceLocalRepo.path.toFile()),
                credentialsProvider = credentialsProvider,
            ),
        )
        val copy = Copy(
            local = LocalCopy(
                repo = KGit.open(entity.copyLocalRepo.path.toFile()),
                credentialsProvider = credentialsProvider,
                config = entity.config,
            ),
            online = github.repos()[Coordinates.Simple(appSetupEntity.teacherUsername, entity.copyRepo.name)]
                .let { repo ->
                    OnlineCopy(
                        pairId = entity.id,
                        repo = repo,
                        pullId = repo.pulls().firstByTitle(source.online.pullRequest.title()).id
                    )
                },
        )
        return RepoPair(
            source = source,
            copy = copy,
        )
    }

    private fun constructSource(request: RepoPairInitRequest): BareSource {
        val onlineSource = BareOnlineSource(
            repo = github.repos()[Coordinates.Simple(request.performerUsername, request.sourceRepoName)],
            pullId = request.sourcePrId,
        )
        val localSource = LocalSource(
            repo = cloneOnlineRepository(
                request.sourceSshUrl,
                appSetupEntity.sourceReposFolder,
                onlineSource,
                credentialsProvider,
            ),
            credentialsProvider = credentialsProvider,
        )
        localSource.updateFromOnline()
        return BareSource(onlineSource, localSource)
    }

    private fun constructCopy(request: RepoPairInitRequest, source: BareSource, config: ConfigEntity): BareCopy {
        val nameOfCopy = request.sourceRepoName.nameOfCopy("")
        val localCopy = LocalCopy(
            repo = cloneLocalRepository(
                source.local.repo,
                appSetupEntity.copyReposFolder,
                nameOfCopy,
            ),
            credentialsProvider = credentialsProvider,
            config = config,
        )
        localCopy.updateFromLocalSource()
        localCopy.addRemote(Coordinates.Simple(appSetupEntity.teacherUsername, nameOfCopy))
        localCopy.updateOnlineCopy()
        val onlineRepo = github.getOnlineRepo(appSetupEntity.teacherUsername, nameOfCopy)
        val sourcePullRequest = source.online.repo.pulls().get(source.online.pullId).smart()
        val onlineCopy = BareOnlineCopy(
            repo = onlineRepo,
            pullId = runCatching { onlineRepo.copyPull(sourcePullRequest).number() }
                .getOrElse {
                    logger.info("Creating pull request failed, ignoring:")
                    logger.debug(it.stackTraceToString())
                    onlineRepo.pulls().firstByTitle(sourcePullRequest.title()).id
                },
        )
        runCatching { onlineCopy.repo.addCollaborator(request.reviewerUsername) }.getOrElse {
            logger.info("Adding reviewer to online copy failed, ignoring:")
            logger.debug(it.stackTraceToString())
        }
        return BareCopy(localCopy, onlineCopy)
    }

    private fun Github.getOnlineRepo(username: String, repoName: String): Repo =
        runCatching { createRepo(repoName) }.getOrElse {
            logger.info("Creating online copy failed, ignoring:")
            logger.debug(it.stackTraceToString())
            repos()[Coordinates.Simple(username, repoName)]
        }
}