package ru.ntcrckr.peer.code.review.pair.copy

import com.jcabi.github.Repo
import ru.ntcrckr.peer.code.review.pair.Online

class OnlineCopy(
    pairId: Int,
    override val repo: Repo,
    val pullId: Int,
) : Online(isSource = false, pairId, pullId)

class BareOnlineCopy(
    val repo: Repo,
    val pullId: Int,
) {
    fun toFull(repoPairId: Int): OnlineCopy =
        OnlineCopy(
            pairId = repoPairId,
            repo = repo,
            pullId = pullId,
        )
}