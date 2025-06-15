package ru.ntcrckr.peer.code.review.pair

import org.slf4j.LoggerFactory
import ru.ntcrckr.peer.code.review.dao.Commits
import ru.ntcrckr.peer.code.review.pair.copy.BareCopy
import ru.ntcrckr.peer.code.review.pair.copy.Copy
import ru.ntcrckr.peer.code.review.pair.source.BareSource
import ru.ntcrckr.peer.code.review.pair.source.Source
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.TimeUnit

class RepoPair(
    pairId: Int,
    private val source: Source,
    private val copy: Copy,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val scheduler = Executors.newScheduledThreadPool(1)

    private val commitInserter = Commits.getInserter(pairId)

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
        updateLocalCopy()
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

    private fun updateLocalCopy() {
        val commitHashes = copy.local.updateFromLocalSource()
        if (commitHashes.isEmpty()) return
        pcrpTransaction {
            commitHashes.forEach { (sourceCommitHash, copyCommitHash) ->
                commitInserter.insert(sourceCommitHash, copyCommitHash)
            }
        }
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
        val pullComments = source.online.pullComments
        val allCodeComments = source.online.pullCodeComments
        pcrpTransaction {
            copy.online.addPullComments(pullComments)
            copy.online.addCodeComments(allCodeComments)
        }
    }
}

class BareRepoPair(
    val source: BareSource,
    val copy: BareCopy,
) {
    fun toFull(repoPairId: Int): RepoPair =
        RepoPair(
            pairId = repoPairId,
            source = source.toFull(repoPairId),
            copy = copy.toFull(repoPairId),
        )
}