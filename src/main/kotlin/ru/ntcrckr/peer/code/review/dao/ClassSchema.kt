package ru.ntcrckr.peer.code.review.dao

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.selectAll
import ru.ntcrckr.peer.code.review.ui.DropdownOption

data class ClassEntity(
    val id: Int,
    val name: String,
) : DropdownOption {
    override val text: String = name
}

class ClassService(database: Database) {
    object Classes : IntIdTable() {
        val name = varchar("name", 50)

        fun getAll(): List<ClassEntity> = selectAll()
            .map {
                ClassEntity(
                    id = it[id].value,
                    name = it[name],
                )
            }
    }

    init {
        SchemaUtils.create(Classes)
    }
}