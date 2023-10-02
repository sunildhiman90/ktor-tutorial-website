package com.example

import com.example.dao.DatabaseFactory
import com.example.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val driverClassName = environment.config.propertyOrNull("database.driverClassName")?.getString()
    val jdbcURL = environment.config.propertyOrNull("database.jdbcURL")?.getString()
    val username = environment.config.propertyOrNull("database.username")?.getString()
    val password = environment.config.propertyOrNull("database.password")?.getString()
    DatabaseFactory.init(driverClassName, jdbcURL, username, password)
    configureTemplating()
    configureSerialization()
    configureRouting()
}
