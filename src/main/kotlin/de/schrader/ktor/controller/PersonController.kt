package de.schrader.ktor.controller

import arrow.core.None
import arrow.core.Some
import arrow.core.getOrElse
import de.schrader.ktor.API_PREFIX
import de.schrader.ktor.model.Person
import de.schrader.ktor.service.PersonService
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import io.ktor.routing.route
import org.koin.ktor.ext.inject

private const val PERSONS = "$API_PREFIX/persons"

@KtorExperimentalLocationsAPI
fun Route.person() {

    val personService: PersonService by inject()

    route(PERSONS) {

        get {
            val persons = personService.findAll()
            call.respond(HttpStatusCode.OK, persons)
        }

        get("/{id}") {
            val id = call.parameters["id"]!!.toInt()
            when (val option = personService.find(id)) {
                is Some -> call.respond(HttpStatusCode.OK, option.getOrElse { "" })
                is None -> call.respond(HttpStatusCode.NotFound)
            }
        }

        post {
            val person = call.receive<Person>()
            when (val option = personService.create(person)) {
                is Some -> {
                    // val path = locations.href(option.getOrElse { "" })
                    call.respond(HttpStatusCode.Created, option.getOrElse { "" })
                }
                is None -> call.respond(HttpStatusCode.InternalServerError)
            }
        }

        put("/{id}") {
            val id = call.parameters["id"]!!.toInt()
            val person = call.receive<Person>()
            when (personService.update(id, person)) {
                0 -> call.respond(HttpStatusCode.NotFound)
                1 -> call.respond(HttpStatusCode.OK)
                else -> call.respond(HttpStatusCode.InternalServerError)
            }
        }

        delete("/{id}") {
            val id = call.parameters["id"]!!.toInt()
            when (personService.delete(id)) {
                0 -> call.respond(HttpStatusCode.NotFound)
                else -> call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}
