package ru.ntcrckr.peer.code.review.pair.source

import com.jcabi.github.Coordinates
import com.jcabi.github.Github
import com.jcabi.github.Repo
import ru.ntcrckr.peer.code.review.pair.Online
import ru.ntcrckr.peer.code.review.pair.users.Performer

class OnlineSource(
    pairId: Int,
    override val repo: Repo,
    pullId: Int,
) : Online(isSource = true, pairId, pullId) {
    val coordinates: Coordinates = repo.coordinates()

    companion object {
        fun from(
            pairId: Int,
            gitHub: Github,
            performer: Performer,
        ): OnlineSource =
            OnlineSource(
                pairId = pairId,
                repo = gitHub.repos()[Coordinates.Simple(performer.username, performer.repoName)],
                pullId = performer.pullId,
            )
    }
}