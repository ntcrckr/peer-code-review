package ru.ntcrckr.peer.code.review.ui

import ru.ntcrckr.peer.code.review.dao.RepoPairEntity
import ru.ntcrckr.peer.code.review.dao.UserEntity
import java.net.URI

data class StudentPair(
    val performer: Student,
    val reviewer: Student,
    val sourceUrl: URI,
    val copyUrl: URI,
) {
    data class Student(
        val username: String,
    ) {
        companion object {
            fun from(userEntity: UserEntity): Student =
                Student(
                    username = userEntity.username,
                )
        }
    }

    companion object {
        fun RepoPairEntity.toStudentPair(teacherUsername: String): StudentPair =
            StudentPair(
                performer = Student.from(performer),
                reviewer = Student.from(reviewer),
                sourceUrl = githubPullUrl(performer.username, sourceRepo.name, sourceRepo.pullId),
                copyUrl = githubPullUrl(teacherUsername, copyRepo.name, copyRepo.pullId),
            )

        private fun githubPullUrl(username: String, repoName: String, pullId: Int): URI =
            URI("https://github.com/$username/$repoName/pull/$pullId")
    }
}
