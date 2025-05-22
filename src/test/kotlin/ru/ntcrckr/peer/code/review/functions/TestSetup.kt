package ru.ntcrckr.peer.code.review.functions

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import ru.ntcrckr.peer.code.review.dao.setupDatabase

interface TestSetup {
    @BeforeEach
    fun `before each`() {
        `remove test repositories`()
        setupDatabase()
    }

    @AfterEach
    fun `after each`() {
        `remove test repositories`()
    }

    fun `remove test repositories`() {
        testRepositoriesPath.toFile().deleteRecursively()
    }
}