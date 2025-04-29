package ru.ntcrckr.peer.code.review.functions

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

interface RemoveRepositoriesBeforeAndAfterTest {
    @BeforeEach
    @AfterEach
    fun `remove test repositories`() {
        testRepositoriesPath.toFile().deleteRecursively()
    }
}