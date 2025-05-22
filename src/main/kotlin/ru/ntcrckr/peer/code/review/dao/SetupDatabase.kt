package ru.ntcrckr.peer.code.review.dao

import org.jetbrains.exposed.sql.SchemaUtils.addMissingColumnsStatements
import org.jetbrains.exposed.sql.SchemaUtils.create
import ru.ntcrckr.peer.code.review.pair.pcrpTransaction

fun setupDatabase() {
    pcrpTransaction {
        listOf(
            ClassService.Classes,
            Lessons,
            RepoPairs,
            Users,
            Repos,
        ).forEach { table ->
            create(table)
            addMissingColumnsStatements(table).forEach { statement -> exec(statement) }
        }
//        create(ClassTable)
//        create(CodeComments)
//        create(CodeReplies)
//        create(PerformerTable)
//        create(PullCommentsTable)
//        create(RepoPairTable)
//        create(ReviewerTable)
//        create(TeacherTable)
//        addMissingColumnsStatements(ClassTable).forEach { exec(it) }
//        addMissingColumnsStatements(CodeComments).forEach { exec(it) }
//        addMissingColumnsStatements(CodeReplies).forEach { exec(it) }
//        addMissingColumnsStatements(PerformerTable).forEach { exec(it) }
//        addMissingColumnsStatements(PullCommentsTable).forEach { exec(it) }
//        addMissingColumnsStatements(RepoPairTable).forEach { exec(it) }
//        addMissingColumnsStatements(ReviewerTable).forEach { exec(it) }
//        addMissingColumnsStatements(TeacherTable).forEach { exec(it) }
    }
}