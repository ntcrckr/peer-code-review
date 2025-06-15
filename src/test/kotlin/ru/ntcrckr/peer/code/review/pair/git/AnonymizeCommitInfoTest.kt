package ru.ntcrckr.peer.code.review.pair.git

import com.github.syari.kgit.KGit
import org.junit.jupiter.api.Test
import ru.ntcrckr.peer.code.review.functions.TestSetup
import java.io.File

class AnonymizeCommitInfoTest : TestSetup {
    @Test
    fun `anonymize local repo`() {
        val repo = KGit.open(File("/Users/ntcrckr/IdeaProjects/temp_copy/"))
        repo.anonymizeCommitInfo()
        Unit
    }
}