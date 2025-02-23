package ru.ntcrckr.peer.code.review.functions

import org.junit.jupiter.api.AfterEach

interface RemoveRepositoriesAfterTest {
    @AfterEach
    fun `remove test repositories`() {
        testRepositoriesPath.toFile().deleteRecursively()
    }
}