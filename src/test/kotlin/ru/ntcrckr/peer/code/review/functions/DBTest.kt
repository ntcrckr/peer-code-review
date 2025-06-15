package ru.ntcrckr.peer.code.review.functions

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Test

class DBTest {
    @Test
    fun main() {
        Database.connect("jdbc:h2:./data/testdb;AUTO_SERVER=TRUE", driver = "org.h2.Driver")

        transaction {
            create(Users)

            Users.insert {
                it[name] = "Alice"
            }

            Users.selectAll().forEach {
                println("${it[Users.id]}: ${it[Users.name]}")
            }
        }
    }
}

object Users : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50)
    override val primaryKey = PrimaryKey(id)
}