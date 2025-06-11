package ru.ntcrckr.peer.code.review.pair

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

const val ANON_USER_NAME = "Some User"
const val ANON_USER_EMAIL = "some@user.com"

fun String.nameOfCopy(prefix: String = "copyOf"): String =
    "$prefix${this.takeIf { prefix != "" }?.replaceFirstChar { it.titlecase(Locale.getDefault()) } ?: this}"

fun sshUrl(userName: String, repositoryName: String): String = "git@github.com:$userName/$repositoryName.git"

private val db = Database.connect("jdbc:h2:./data/pcrp_temp;AUTO_SERVER=TRUE;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")

fun <T> pcrpTransaction(statement: Transaction.() -> T): T =
    transaction(db, statement = statement)

suspend fun <T> suspendPcrpTransaction(statement: suspend Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO, db, statement = statement)

fun getGitHubToken(): String = System.getenv("GITHUB_TOKEN")

fun String.parseGitHubPrUrl(): Triple<String, String, Int> {
    val regex = Regex("""^https://github\.com/([^/]+)/([^/]+)/pull/(\d+)$""")
    val matchResult = regex.matchEntire(this) ?: TODO()
    val (username, repo, prNumberStr) = matchResult.destructured
    return Triple(username, repo, prNumberStr.toInt())
}