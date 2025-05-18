package ru.ntcrckr.peer.code.review.pair.github

import com.jcabi.github.Github
import com.jcabi.github.Repo
import com.jcabi.github.Repos

fun Github.createRepo(name: String, private: Boolean = true): Repo =
    repos().create(Repos.RepoCreate(name, private))