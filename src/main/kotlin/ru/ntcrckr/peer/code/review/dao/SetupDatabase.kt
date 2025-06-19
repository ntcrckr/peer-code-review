package ru.ntcrckr.peer.code.review.dao

import org.jetbrains.exposed.sql.SchemaUtils.addMissingColumnsStatements
import org.jetbrains.exposed.sql.SchemaUtils.create
import ru.ntcrckr.peer.code.review.pair.pcrpTransaction

fun setupDatabase() {
    pcrpTransaction {
        listOf(
            AppSetup,
            Classes,
            Lessons,
            RepoPairs,
            Users,
            Repos,
            Commits,
            PullIssueComments,
            PullCodeComments,
            PullCodeReplies,
            Configs,
        ).forEach { table ->
            create(table)
            addMissingColumnsStatements(table).forEach { statement -> exec(statement) }
        }
    }
}