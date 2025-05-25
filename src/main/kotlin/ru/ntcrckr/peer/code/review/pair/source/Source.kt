package ru.ntcrckr.peer.code.review.pair.source

class Source(
    val online: OnlineSource,
    val local: LocalSource,
)

class BareSource(
    val online: BareOnlineSource,
    val local: LocalSource,
) {
    fun toFull(repoPairId: Int): Source =
        Source(
            online = online.toFull(repoPairId),
            local = local,
        )
}