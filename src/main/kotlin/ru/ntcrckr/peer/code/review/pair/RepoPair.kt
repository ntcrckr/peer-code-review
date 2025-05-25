package ru.ntcrckr.peer.code.review.pair

import org.slf4j.LoggerFactory
import ru.ntcrckr.peer.code.review.pair.copy.BareCopy
import ru.ntcrckr.peer.code.review.pair.copy.Copy
import ru.ntcrckr.peer.code.review.pair.source.BareSource
import ru.ntcrckr.peer.code.review.pair.source.Source
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.TimeUnit

class RepoPair(
    private val source: Source,
    private val copy: Copy,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val scheduler = Executors.newScheduledThreadPool(1)

    fun startUpdateCycle(delay: Duration = Duration.ofMinutes(1L)) {
        runCatching {
            logger.info("Starting update cycle")
            scheduler.scheduleAtFixedRate(::updateCycle, 0L, delay.seconds, TimeUnit.SECONDS)
        }.getOrElse {
            when (it) {
                is RejectedExecutionException -> logger.info("Update Cycle Job is already running")
                else -> {
                    logger.info("Starting update cycle failed, ignoring:")
                    logger.debug(it.stackTraceToString())
                }
            }
        }
    }

    fun stopUpdateCycle() {
        logger.info("Stopping update cycle")
        scheduler.shutdown()
    }

    private fun updateCycle() = runCatching {
        logger.info("Another update cycle")
        source.local.updateFromOnline()
        logger.info("Updated local source")
        copy.local.updateFromLocalSource()
        logger.info("Updated local copy")
        copy.local.updateOnlineCopy()
        logger.info("Updated online copy")
        copyCommentsFromCopyToSource()
        logger.info("Copy -> Source")
        copyCommentsFromSourceToCopy()
        logger.info("Source -> Copy")
    }.onFailure {
        logger.info("Exception in update cycle:")
        it.printStackTrace()
    }

    private fun copyCommentsFromCopyToSource() {
        val pullComments = copy.online.pullComments
        val allCodeComments = copy.online.pullCodeComments
        pcrpTransaction {
            source.online.addPullComments(pullComments)
            source.online.addCodeComments(allCodeComments)
        }
    }

    private fun copyCommentsFromSourceToCopy() {
        val pullComments = copy.online.pullComments
        val allCodeComments = copy.online.pullCodeComments
        pcrpTransaction {
            source.online.addPullComments(pullComments)
            source.online.addCodeComments(allCodeComments)
        }
    }
}

class BareRepoPair(
    val source: BareSource,
    val copy: BareCopy,
) {
    fun toFull(repoPairId: Int): RepoPair =
        RepoPair(
            source = source.toFull(repoPairId),
            copy = copy.toFull(repoPairId),
        )
}