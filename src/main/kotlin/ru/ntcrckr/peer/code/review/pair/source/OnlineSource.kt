package ru.ntcrckr.peer.code.review.pair.source

import com.jcabi.github.Coordinates
import com.jcabi.github.Repo
import ru.ntcrckr.peer.code.review.pair.Online

class OnlineSource(
    pairId: Int,
    override val repo: Repo,
    override val pullId: Int,
) : Online(isSource = true, pairId, pullId), IOnlineSource {
    override val coordinates: Coordinates = repo.coordinates()
}

class BareOnlineSource(
    override val repo: Repo,
    override val pullId: Int,
) : IOnlineSource {
    override val coordinates: Coordinates = repo.coordinates()

    fun toFull(repoPairId: Int): OnlineSource =
        OnlineSource(
            pairId = repoPairId,
            repo = repo,
            pullId = pullId,
        )
}

interface IOnlineSource {
    val repo: Repo
    val coordinates: Coordinates
    val pullId: Int
}