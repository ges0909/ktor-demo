package de.schrader.ktor.webapp

import de.schrader.ktor.api.model.Person
import de.schrader.ktor.api.service.PersonService
import io.ktor.application.call
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import org.koin.ktor.ext.inject

const val PERSONS = "/persons"

fun Route.person() {
    val personService: PersonService by inject()

    route(PERSONS) {
        get {
            val persons = personService.findAll()
            // val user = call.authentication.principal as User
            call.respond(
                FreeMarkerContent(
                    "person.ftl", mapOf(
                        "persons" to persons
                        // "displayName" to user.displayName
                    )
                )
            )
        }

        post {
            val params = call.receiveParameters()
            when (params["action"] ?: throw IllegalArgumentException("Missing parameter: action")) {
                "add" -> {
                    val name = params["name"] ?: throw IllegalArgumentException("Missing parameter: name")
                    val age = params["age"] ?: throw IllegalArgumentException("Missing parameter: age")
                    personService.create(
                        Person(
                            userId = "",
                            name = name,
                            age = age.toInt()
                        )
                    )
                }
                "delete" -> {
                    val id = params["id"] ?: throw IllegalArgumentException("Missing parameter: id")
                    personService.delete(id.toInt())
                }
            }
            call.respondRedirect(PERSONS)
        }
    }
}