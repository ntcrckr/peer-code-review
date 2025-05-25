package ru.ntcrckr.peer.code.review.pair.copy

class Copy(
    val local: LocalCopy,
    val online: OnlineCopy,
)

class BareCopy(
    val local: LocalCopy,
    val online: BareOnlineCopy,
) {
    fun toFull(repoPairId: Int): Copy =
        Copy(
            local = local,
            online = online.toFull(repoPairId),
        )
}