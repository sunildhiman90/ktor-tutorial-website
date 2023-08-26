package com.example.models

import org.jetbrains.exposed.sql.Table
import java.io.Serializable
import java.util.concurrent.atomic.AtomicInteger

// make it serializable for making it cacheable
data class Article(val id: Int, var title: String, var body: String): Serializable {
//    companion object {
//        private val idCounter = AtomicInteger() //thread safe data structure
//
//        fun newEntry(title: String, body: String) = Article(idCounter.getAndIncrement(), title, body)
//    }
}

object Articles : Table() {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 128)
    val body = varchar("body", 1024)

    override val primaryKey = PrimaryKey(id)
}

//val articles = mutableListOf(Article.newEntry(
//    "The drive to develop!",
//    "...it's what keeps me going."
//))