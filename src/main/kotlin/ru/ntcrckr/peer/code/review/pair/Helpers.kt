package ru.ntcrckr.peer.code.review.pair

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

const val ANON_USER_NAME = "Some User"
const val ANON_USER_EMAIL = "some@user.com"

fun String.nameOfCopy(prefix: String = "copyOf"): String =
    "$prefix${this.replaceFirstChar { it.titlecase(Locale.getDefault()) }}"

fun sshUrl(userName: String, repositoryName: String): String = "git@github.com:$userName/$repositoryName.git"

fun <T> pcrpTransaction(statement: Transaction.() -> T): T =
    transaction(Database.connect("jdbc:h2:./data/pcrp;AUTO_SERVER=TRUE", driver = "org.h2.Driver"), statement)