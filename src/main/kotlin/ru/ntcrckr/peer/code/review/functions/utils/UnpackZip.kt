package ru.ntcrckr.peer.code.review.functions.utils

import java.util.zip.ZipFile

fun unpackZip(fileName: String, targetDirectory: String) {
//    Files.createDirectories(Path(targetDirectory))
    ZipFile(fileName).use { zip ->
        zip.entries().asSequence().forEach { entry ->
            println(entry.name)
        }
    }
}