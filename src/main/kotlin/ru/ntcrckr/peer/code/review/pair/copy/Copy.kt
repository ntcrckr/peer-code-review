package ru.ntcrckr.peer.code.review.pair.copy

import com.jcabi.github.Github
import ru.ntcrckr.peer.code.review.pair.Config
import ru.ntcrckr.peer.code.review.pair.source.Source
import ru.ntcrckr.peer.code.review.pair.users.Reviewer
import ru.ntcrckr.peer.code.review.pair.users.Teacher
import java.nio.file.Path

class Copy(
    pairId: Int,
    github: Github,
    repoName: String,
    teacher: Teacher,
    reviewer: Reviewer,
    source: Source,
    localCopyPath: Path,
    config: Config,
) {
    val local: LocalCopy =
        LocalCopy.openOrCreate(localCopyPath, repoName, source.local, teacher, config)
            .also { it.updateFromLocalSource() }
    val online: OnlineCopy =
        OnlineCopy.from(
            pairId,
            github,
            teacher,
            reviewer,
            source.online.coordinates.repo(),
            local,
            source.online.pullRequest
        )
}