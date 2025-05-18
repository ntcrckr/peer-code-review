package ru.ntcrckr.peer.code.review.functions

import org.jetbrains.exposed.sql.SchemaUtils.create
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import ru.ntcrckr.peer.code.review.dao.*
import ru.ntcrckr.peer.code.review.pair.pcrpTransaction

interface TestSetup {
    @BeforeEach
    fun `before each`() {
        `remove test repositories`()
        `setup database`()
    }

    @AfterEach
    fun `after each`() {
        `remove test repositories`()
    }

    fun `remove test repositories`() {
        testRepositoriesPath.toFile().deleteRecursively()
    }

    fun `setup database`() {
        pcrpTransaction {
            create(RepoPairTable)
            create(PerformerTable)
            create(TeacherTable)
            create(ReviewerTable)
            create(PullCommentsTable)
            create(CodeComments)
            create(CodeReplies)
        }
    }
}