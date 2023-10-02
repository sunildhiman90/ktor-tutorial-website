package com.example.dao

import com.example.models.Articles
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    private fun createHikariDataSource(
        url: String,
        driver: String,
        username: String,
        password: String
    ) = HikariDataSource(HikariConfig().apply {
        driverClassName = driver
        jdbcUrl = url
        maximumPoolSize = 10
        this.username = username
        this.password = password
        isAutoCommit = false
        transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        validate()
    }.apply {

    })

    fun init(driverClassName: String?, jdbcURL: String?) {
        require(driverClassName?.isNotEmpty() == true) {
            "driverClassName cannot be empty"
        }
        require(jdbcURL?.isNotEmpty() == true) {
            "jdbcUrl cannot be empty"
        }

        if (driverClassName != null && jdbcURL != null) {
            val hikariDataSource = createHikariDataSource(
                jdbcURL,
                driverClassName,
                username = "blogdb_user",
                password = "skdktorblogapp@mysq281290"
            )
            val database = Database.connect(datasource = hikariDataSource)

            //val database = Database.connect(datasource = hikariDataSource)
            //val database = Database.connect(jdbcURL, driverClassName)
            /**
             * Note that the Database.connect function doesn't establish a real database connection
             * until you call the transaction, it only creates a descriptor for future connections.
             */
            //blocking transaction
            transaction(database) {
                SchemaUtils.create(Articles)
            }
        }

    }


    // non blocking or async transaction
    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}