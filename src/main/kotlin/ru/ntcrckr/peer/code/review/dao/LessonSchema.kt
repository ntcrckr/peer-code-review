package ru.ntcrckr.peer.code.review.dao

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import ru.ntcrckr.peer.code.review.ui.DropdownOption

data class LessonEntity(
    val id: Int = -1,
    val name: String,
) : DropdownOption {
    override val text: String = name
}

object Lessons : IntIdTable() {
    val classId = integer("class_id").references(Classes.id)
    val name = varchar("name", 50)

    fun getAllForClass(classId: Int): List<LessonEntity> =
        select { Lessons.classId eq classId }
            .map {
                LessonEntity(
                    id = it[id].value,
                    name = it[name],
                )
            }

    fun insert(classId: Int, entity: LessonEntity): LessonEntity = insert {
        it[this.classId] = classId
        it[name] = entity.name
    }.let { LessonEntity(id = it[id].value, name = it[name]) }
}