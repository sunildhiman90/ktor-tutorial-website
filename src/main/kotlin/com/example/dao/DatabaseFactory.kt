package com.example.dao

import com.example.models.Articles
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init(driverClassName: String?, jdbcURL: String?) {
        requireNotNull(driverClassName?.isNotEmpty() == true) {
            "driverClassName cannot be empty"
        }
        require(jdbcURL?.isNotEmpty() == true) {
            "jdbcUrl cannot be empty"
        }

        if(driverClassName != null && jdbcURL != null) {
            val database = Database.connect(jdbcURL, driverClassName)
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