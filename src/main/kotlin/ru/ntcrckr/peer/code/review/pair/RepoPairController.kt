package ru.ntcrckr.peer.code.review.pair

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import ru.ntcrckr.peer.code.review.dao.AppSetupEntity
import ru.ntcrckr.peer.code.review.dao.RepoPairs

class RepoPairController {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private var factory: RepoPairFactory? = null
    private val repoPairs: MutableList<RepoPair> = mutableListOf()

    fun addPair(lessonId: Int, performerPullLink: String, reviewerUsername: String) {
        logger.info("Adding ($performerPullLink, $reviewerUsername) to $lessonId lesson")
        val startCallback = { repoPair: RepoPair ->
            repoPair.startUpdateCycle()
        }
        CoroutineScope(Dispatchers.IO).launch {
            val repoPair = factory!!.createRepoPair(RepoPairInitRequest(lessonId, performerPullLink, reviewerUsername))
            repoPairs.add(repoPair)
            startCallback(repoPair)
        }
        logger.info("Added ($performerPullLink, $reviewerUsername) to $lessonId lesson")
    }

    fun updateAppSetup(appSetupEntity: AppSetupEntity) {
        logger.info("Updating setup to $appSetupEntity")
        CoroutineScope(Dispatchers.IO).launch {
            stopAll()
            factory = RepoPairFactory(appSetupEntity)
            startAllPairs()
        }
        logger.info("Updated setup to $appSetupEntity")
    }

    private suspend fun startAllPairs() {
        logger.info("Fetching entities")
        val entities = pcrpTransaction { RepoPairs.getAll() }
        logger.info("Fetched ${entities.size} entities")
        val existingRepoPairs = entities.mapNotNull {
            suspendPcrpTransaction { factory!!.existingRepoPair(it.id) }
                ?.also(RepoPair::startUpdateCycle)
        }
        logger.info("Created and started ${existingRepoPairs.size} repo pairs")
        repoPairs.addAll(existingRepoPairs)
        logger.info("Added repo pairs to list")
    }

    private fun stopAll() {
        logger.info("Stopping all repo pairs")
        repoPairs.forEach { it.stopUpdateCycle() }
        logger.info("Stopped all repo pairs")
        repoPairs.clear()
        logger.info("Removed all repo pairs")
    }
}