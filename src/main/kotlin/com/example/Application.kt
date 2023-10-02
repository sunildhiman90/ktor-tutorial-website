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
    DatabaseFactory.init(driverClassName, jdbcURL)
    configureTemplating()
    configureSerialization()
    configureRouting()
}
