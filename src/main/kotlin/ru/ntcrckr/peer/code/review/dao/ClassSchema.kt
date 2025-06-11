package ru.ntcrckr.peer.code.review.dao

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import ru.ntcrckr.peer.code.review.ui.DropdownOption

data class ClassEntity(
    val id: Int = -1,
    val name: String,
) : DropdownOption {
    override val text: String = name
}

object Classes : IntIdTable() {
    val name = varchar("name", 50)

    fun getAll(): List<ClassEntity> = selectAll()
        .map { ClassEntity(id = it[id].value, name = it[name]) }

    fun insert(entity: ClassEntity): ClassEntity = insert {
        it[name] = entity.name
    }.let { ClassEntity(id = it[id].value, name = it[name]) }
}