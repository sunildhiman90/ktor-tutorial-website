package com.example.plugins

import com.example.dao.DAOFacade
import com.example.dao.DAOFacadeCacheImpl
import com.example.dao.DAOFacadeImpl
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.coroutines.runBlocking
import java.io.File

fun Application.configureRouting() {

    val dao: DAOFacade = DAOFacadeCacheImpl(
        DAOFacadeImpl(),
        File(environment.config.property("database.ehcacheFilePath").getString())
    ).apply {
        runBlocking {
            if(allArticles().isEmpty()) {
                addNewArticle("The drive to develop!", "...it's what keeps me going.")
            }
        }
    }

    routing {

        get("/") {
            call.respondRedirect("articles")
        }
        route("articles") {
            get {
                // Show a list of articles
                call.respond(FreeMarkerContent("index.ftl", model = mapOf("articles" to dao.allArticles())))
            }
            get("new") {
                // Show a page with fields for creating a new article
                call.respond(FreeMarkerContent("new.ftl", model = null))
            }
            post {
                // Save an article
                val formParameters = call.receiveParameters()
                val title = formParameters.getOrFail("title")
                val body = formParameters.getOrFail("body")
                val newEntry = dao.addNewArticle(title, body)
                call.respondRedirect("/articles/${newEntry?.id}")
            }
            get("{id}") {
                // Show an article with a specific id
                val id = call.parameters.getOrFail<Int>("id").toInt()
                call.respond(FreeMarkerContent("show.ftl", mapOf("article" to dao.article(id))))
            }
            get("{id}/edit") {
                // Show a page with fields for editing an article
                val id = call.parameters.getOrFail<Int>("id").toInt()
                call.respond(FreeMarkerContent("edit.ftl", mapOf("article" to dao.article(id))))
            }
            post("{id}") {
                // Update or delete an article

                val id = call.parameters.getOrFail<Int>("id").toInt()
                val formParameters = call.receiveParameters()
                when (formParameters.getOrFail("_action")) {
                    "update" -> {
                        val title = formParameters.getOrFail("title")
                        val body = formParameters.getOrFail("body")
                        dao.editArticle(id, title, body)
                        call.respondRedirect("/articles/$id")
                    }
                    "delete" -> {
                        dao.deleteArticle(id)
                        call.respondRedirect("/articles")
                    }
                }
            }
        }

        //for resources from project resources directory. If we type localhost:8080/static/anyfilename, it will map them to files directory inside resources ie. files/anyfilename
        staticResources("/static", "static") {
            default("index.html") //default if no path is specified
        }

        //for resources from project resources directory
        staticResources("/files", "files") {
            //default("index.html") //default if no path is specified
        }

        //for resources from some project root directory: In this example it is files directory in project root
        staticFiles("/files", File("files")) {
            default("index.html") //default if no path is specified
        }

        // deprecated
        /*static("/static") {
            resources("files")
        }*/
    }
}
