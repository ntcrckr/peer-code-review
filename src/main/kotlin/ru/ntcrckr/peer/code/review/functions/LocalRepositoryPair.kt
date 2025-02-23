package ru.ntcrckr.peer.code.review.functions

import com.github.syari.kgit.KGit
import ru.ntcrckr.peer.code.review.functions.local.anonymizeCommitInfo
import ru.ntcrckr.peer.code.review.functions.local.cloneLocalRepository
import ru.ntcrckr.peer.code.review.functions.local.copyFromLocalRemote
import ru.ntcrckr.peer.code.review.functions.local.createLocalRepository
import java.nio.file.Path
import java.util.*

class LocalRepositoryPair private constructor(
    private val sourceRemoteName: String,
    val localSource: KGit,
    val localCopy: KGit,
    private val config: Config,
) {
    fun updateLocalSource() {
//        source.
    }

    fun updateLocalCopy() {
        localCopy.copyFromLocalRemote(sourceRemoteName)
            .runIf(config.anonymizeCommitInfo) { anonymizeCommitInfo(DEFAULT_USER_NAME, DEFAULT_USER_EMAIL) }
    }

    private fun <T> T.runIf(predicate: Boolean, block: T.() -> T): T = when {
        predicate -> block()
        else -> this
    }

    companion object {
        const val DEFAULT_REMOTE_NAME = "source"
        const val DEFAULT_USER_NAME = "Some User"
        const val DEFAULT_USER_EMAIL = "some@user.com"

        fun createNew(
            localSourceFolder: Path,
            localCopyFolder: Path,
            repositoryName: String,
            initialBranchName: String,
            config: Config,
            localSourceRemoteName: String = DEFAULT_REMOTE_NAME,
        ): LocalRepositoryPair {
            val source = createLocalRepository(localSourceFolder, repositoryName, initialBranchName)
            val localCopy =
                cloneLocalRepository(source, localCopyFolder, repositoryName.nameOfCopy(), localSourceRemoteName)
            return LocalRepositoryPair(localSourceRemoteName, source, localCopy, config)
        }

        fun from(
            sourceRepositoryPath: Path,
            copyRepositoryPath: Path,
            config: Config,
            localSourceRemoteName: String = DEFAULT_REMOTE_NAME,
        ): LocalRepositoryPair {
            val localSource = KGit.open(sourceRepositoryPath.toFile())
            val localCopy = KGit.open(copyRepositoryPath.toFile())
            return LocalRepositoryPair(localCopy.remoteBy(localSourceRemoteName).name, localSource, localCopy, config)
        }

        fun fromLocalSource(
            sourceRepository: KGit,
            copyFolder: Path,
            repositoryName: String,
            config: Config,
            localSourceRemoteName: String = DEFAULT_REMOTE_NAME,
        ): LocalRepositoryPair {
            val localCopy =
                cloneLocalRepository(sourceRepository, copyFolder, repositoryName.nameOfCopy(), localSourceRemoteName)
            return LocalRepositoryPair(localSourceRemoteName, sourceRepository, localCopy, config)
        }

        private fun String.nameOfCopy(prefix: String = "copyOf"): String =
            "$prefix${this.replaceFirstChar { it.titlecase(Locale.getDefault()) }}"
    }
}