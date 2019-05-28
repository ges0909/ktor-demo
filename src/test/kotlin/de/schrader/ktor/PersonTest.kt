/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package de.schrader.ktor

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.application.Application
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class PersonTest {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class TestPerson(val id: Int? = null, val name: String, val age: Int)

    private val mapper = jacksonObjectMapper()

//    @BeforeTest fun setUp() {
//    }
//    }

//    @AfterTest fun tearDown() {
//    }

    @KtorExperimentalLocationsAPI
    @Test fun `when a person is created then it is returned`() = withTestApplication(Application::main) {

        // create
        val id = with(handleRequest(HttpMethod.Post, "/persons") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            // setBody(mapOf("name" to "Vinz", "age" to 20).toString())
            // setBody(Gson().toJson(TestPerson(name = "Vinzenz", age = 20)))
            setBody(mapper.writeValueAsString(TestPerson(name = "Vinzenz", age = 20)))
        }) {
            assertEquals(HttpStatusCode.Created, response.status())
            // Gson().fromJson(response.content.toString(), TestPerson::class.java).id
            mapper.readValue<TestPerson>(response.content.toString()).id
        }

        // read
        var person = with(handleRequest(HttpMethod.Get, "/persons/$id") {
        }) {
            assertEquals(HttpStatusCode.OK, response.status())
            // Gson().fromJson(response.content.toString(), TestPerson::class.java)
            mapper.readValue<TestPerson>(response.content.toString())
        }
        assertEquals("Vinzenz", person.name)
        assertEquals(20, person.age)

        // update
        with(handleRequest(HttpMethod.Put, "/persons/$id") {
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            // setBody(Gson().toJson(TestPerson(name = "Freddy", age = 30)))
            setBody(mapper.writeValueAsString(TestPerson(name = "Freddy", age = 30)))

        }) {
            assertEquals(HttpStatusCode.OK, response.status())
        }

        // read
        person = with(handleRequest(HttpMethod.Get, "/persons/$id") {
        }) {
            assertEquals(HttpStatusCode.OK, response.status())
            // Gson().fromJson(response.content.toString(), TestPerson::class.java)
            mapper.readValue(response.content.toString())
        }
        assertEquals("Freddy", person.name)
        assertEquals(30, person.age)

        // delete
        with(handleRequest(HttpMethod.Delete, "/persons/$id") {
        }) {
            assertEquals(HttpStatusCode.NoContent, response.status())
        }
    }
}
