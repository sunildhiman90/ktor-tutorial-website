package com.example.dao

import com.example.dao.DatabaseFactory.dbQuery
import com.example.models.Article
import com.example.models.Articles
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class DAOFacadeImpl : DAOFacade {

    private fun resultRowToArticle(row: ResultRow) = Article(
        id = row[Articles.id],
        title = row[Articles.title],
        body = row[Articles.body],
    )

    // Table.selectAll returns an instance of Query, so to get the list of Article instances,
    // we need to manually extract data for each row and convert it to our data class.
    // We accomplish that using the helper function resultRowToArticle that builds an Article from the ResultRow.
    // The ResultRow provides a way to get the data stored in the specified Column by using a concise get operator,
    // allowing us to use the bracket syntax, similar to an array or a map
    override suspend fun allArticles(): List<Article> = dbQuery {
        Articles.selectAll().map(::resultRowToArticle)
    }

    override suspend fun article(id: Int): Article? = dbQuery {
        Articles
            .select { Articles.id eq id }
            .map { resultRowToArticle(it) }
            .singleOrNull()
    }

    override suspend fun addNewArticle(title: String, body: String): Article? = dbQuery {
        val insertStatement = Articles.insert {
            it[Articles.title] = title
            it[Articles.body] = body
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToArticle)
    }

    override suspend fun editArticle(id: Int, title: String, body: String): Boolean = dbQuery {
        Articles.update({ Articles.id eq id }) {
            it[Articles.title] = title
            it[Articles.body] = body
        } > 0
    }

    override suspend fun deleteArticle(id: Int): Boolean = dbQuery {
        Articles.deleteWhere { Articles.id eq id } > 0
    }
}

// lets add a sample article to be inserted into the database before the application is started
//val dao: DAOFacade = DAOFacadeImpl().apply {
//    runBlocking {
//        if(allArticles().isEmpty()) {
//            addNewArticle("The drive to develop!", "...it's what keeps me going.")
//        }
//    }
//}