package ru.ntcrckr.peer.code.review.dao

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.select
import ru.ntcrckr.peer.code.review.ui.DropdownOption

data class LessonEntity(
    val id: Int,
    val name: String,
) : DropdownOption {
    override val text: String = name
}

object Lessons : IntIdTable() {
    val classId = integer("class_id").references(ClassService.Classes.id)
    val name = varchar("name", 50)

    fun getAllForClass(classId: Int): List<LessonEntity> =
        select { Lessons.classId eq classId }
            .map {
                LessonEntity(
                    id = it[id].value,
                    name = it[name],
                )
            }
}