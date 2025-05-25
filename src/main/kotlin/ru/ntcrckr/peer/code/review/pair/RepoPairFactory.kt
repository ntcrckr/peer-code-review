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
import java.nio.file.Path

data class RepoPairInitRequest(
    val lessonId: Int,
    val sourcePullUrl: String,
    val reviewerUsername: String,
    val config: Config,
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
    private val teacher: UserEntity,
    private val localSourceReposFolder: Path,
    private val localCopyReposFolder: Path,
    githubToken: String,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val github: Github = RtGithub(githubToken)
    private val credentialsProvider = UsernamePasswordCredentialsProvider(teacher.username, githubToken)

    fun createRepoPair(request: RepoPairInitRequest): RepoPair {
        val source = constructSource(request)
        val copy = constructCopy(request, source)
        val repoPairEntity = RepoPairEntity(
            teacher = teacher,
            performer = UserEntity(request.performerUsername),
            sourceRepo = RepoEntity(request.sourceRepoName, request.sourcePrId),
            sourceLocalRepo = LocalRepoEntity(source.local.repo.path),
            reviewer = UserEntity(request.reviewerUsername),
            copyRepo = RepoEntity(copy.online.repo.coordinates().repo(), copy.online.pullId),
            copyLocalRepo = LocalRepoEntity(copy.local.repo.path),
        )
        val repoPairId = RepoPairs.insert(request.lessonId, repoPairEntity)
        val bareRepoPair = BareRepoPair(source, copy)
        return bareRepoPair.toFull(repoPairId)
    }

    fun existingRepoPair(repoPairId: Int, config: Config): RepoPair? {
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
                config = config,
            ),
            online = github.repos()[Coordinates.Simple(entity.teacher.username, entity.copyRepo.name)]
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
                localSourceReposFolder,
                onlineSource,
                credentialsProvider,
            ),
            credentialsProvider = credentialsProvider,
        )
        localSource.updateFromOnline()
        return BareSource(onlineSource, localSource)
    }

    private fun constructCopy(request: RepoPairInitRequest, source: BareSource): BareCopy {
        val nameOfCopy = request.sourceRepoName.nameOfCopy("")
        val localCopy = LocalCopy(
            repo = cloneLocalRepository(
                source.local.repo,
                localCopyReposFolder,
                nameOfCopy,
            ),
            credentialsProvider = credentialsProvider,
            config = request.config,
        )
        localCopy.updateFromLocalSource()
        localCopy.addRemote(Coordinates.Simple(teacher.username, nameOfCopy))
        localCopy.updateOnlineCopy()
        val onlineRepo = github.getOnlineRepo(teacher.username, nameOfCopy)
        val sourcePullRequest = onlineRepo.pulls().get(source.online.pullId).smart()
        val onlineCopy = BareOnlineCopy(
            repo = onlineRepo,
            pullId = runCatching { onlineRepo.copyPull(sourcePullRequest).number() }
                .getOrElse { onlineRepo.pulls().firstByTitle(sourcePullRequest.title()).id },
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