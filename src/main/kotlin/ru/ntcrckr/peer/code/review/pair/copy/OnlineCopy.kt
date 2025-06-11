package ru.ntcrckr.peer.code.review.pair.copy

import com.jcabi.github.Repo
import ru.ntcrckr.peer.code.review.pair.Online

class OnlineCopy(
    pairId: Int,
    override val repo: Repo,
    override val pullId: Int,
) : Online(isSource = false, pairId, pullId), IOnlineCopy

class BareOnlineCopy(
    override val repo: Repo,
    override val pullId: Int,
) : IOnlineCopy {
    fun toFull(repoPairId: Int): OnlineCopy =
        OnlineCopy(
            pairId = repoPairId,
            repo = repo,
            pullId = pullId,
        )
}

interface IOnlineCopy {
    val repo: Repo
    val pullId: Int
}