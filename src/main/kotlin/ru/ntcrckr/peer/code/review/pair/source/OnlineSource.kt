package ru.ntcrckr.peer.code.review.pair.source

import com.jcabi.github.Coordinates
import com.jcabi.github.Repo
import ru.ntcrckr.peer.code.review.pair.Online

class OnlineSource(
    pairId: Int,
    override val repo: Repo,
    val pullId: Int,
) : Online(isSource = true, pairId, pullId)

class BareOnlineSource(
    val repo: Repo,
    val pullId: Int,
) {
    val coordinates: Coordinates = repo.coordinates()

    fun toFull(repoPairId: Int): OnlineSource =
        OnlineSource(
            pairId = repoPairId,
            repo = repo,
            pullId = pullId,
        )
}