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
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import io.ktor.util.InternalAPI
import io.ktor.util.encodeBase64
import org.assertj.core.api.Assertions.assertThat
import org.junit.FixMethodOrder
import org.junit.runners.MethodSorters
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

private const val PERSONS = "/api/v1/persons"

@InternalAPI
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ApplicationTest {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class TestPerson(val id: Int? = null, val userId: String = "", val name: String, val age: Int)

    private val credentials = "ger:ger123".encodeBase64()
    private val mapper = jacksonObjectMapper()

    private companion object {
        var ID: Int? = null
    }

//    @BeforeTest fun setUp() {
//    }
//    }

//    @AfterTest fun tearDown() {
//    }

    @Test fun `1 create person`() {
        ID = withTestApplication(Application::main) {
            with(handleRequest(HttpMethod.Post, PERSONS) {
                addHeader(HttpHeaders.Authorization, "Basic $credentials")
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                // setBody(mapOf("name" to "Vinz", "age" to 20).toString())
                // setBody(Gson().toJson(TestPerson(name = "Vinzenz", age = 20)))
                setBody(mapper.writeValueAsString(TestPerson(name = "Vinzenz", age = 20)))
            }) {
                assertThat(response.status()).isEqualTo(HttpStatusCode.Created)
                // Gson().fromJson(response.content.toString(), TestPerson::class.java).ID
                mapper.readValue<TestPerson>(response.content.toString()).id
            }
        }
    }

    @Test fun `2 read created person`() = withTestApplication(Application::main) {
        val person = with(handleRequest(HttpMethod.Get, "$PERSONS/$ID") {
            addHeader(HttpHeaders.Authorization, "Basic $credentials")
        }) {
            assertThat(response.status()).isEqualTo(HttpStatusCode.OK)
            // Gson().fromJson(response.content.toString(), TestPerson::class.java)
            mapper.readValue<TestPerson>(response.content.toString())
        }
        assertThat(person.name).isEqualTo("Vinzenz")
        assertThat(person.age).isEqualTo(20)
        Unit
    }

    @Test fun `3 update person`() = withTestApplication(Application::main) {
        with(handleRequest(HttpMethod.Put, "$PERSONS/$ID") {
            addHeader(HttpHeaders.Authorization, "Basic $credentials")
            addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            // setBody(Gson().toJson(TestPerson(name = "Freddy", age = 30)))
            setBody(mapper.writeValueAsString(TestPerson(name = "Freddy", age = 30)))
        }) {
            assertThat(response.status()).isEqualTo(HttpStatusCode.OK)
        }
        Unit
    }

    @Test fun `4 read updated person`() = withTestApplication(Application::main) {
        val person = with(handleRequest(HttpMethod.Get, "$PERSONS/$ID") {
            addHeader(HttpHeaders.Authorization, "Basic $credentials")
        }) {
            assertThat(response.status()).isEqualTo(HttpStatusCode.OK)
            // Gson().fromJson(response.content.toString(), TestPerson::class.java)
            mapper.readValue<TestPerson>(response.content.toString())
        }
        assertThat(person.name).isEqualTo("Freddy")
        assertThat(person.age).isEqualTo(30)
        Unit
    }

    @Test fun `5 delete person`() = withTestApplication(Application::main) {
        with(handleRequest(HttpMethod.Delete, "$PERSONS/$ID") {
            addHeader(HttpHeaders.Authorization, "Basic $credentials")
        }) {
            assertThat(response.status()).isEqualTo(HttpStatusCode.NoContent)
        }
        Unit
    }

    @Test fun `find non-existing person returns not found`() = withTestApplication(Application::main) {
        val id = Random.nextInt(0, 100)
        val person = with(handleRequest(HttpMethod.Get, "$PERSONS/$id") {
            addHeader(HttpHeaders.Authorization, "Basic $credentials")
        }) {
            assertThat(response.status()).isEqualTo(HttpStatusCode.NotFound)
        }
    }

    @Test fun `delete non-existing person returns not found`() = withTestApplication(Application::main) {
        val id = Random.nextInt(0, 100)
        with(handleRequest(HttpMethod.Delete, "$PERSONS/$id") {
            addHeader(HttpHeaders.Authorization, "Basic $credentials")
        }) {
            assertThat(response.status()).isEqualTo(HttpStatusCode.NotFound)
        }
        Unit
    }

    @Test fun `find all persons returns empty list`() = withTestApplication(Application::main) {
        val list = with(handleRequest(HttpMethod.Get, "$PERSONS") {
            addHeader(HttpHeaders.Authorization, "Basic $credentials")
        }) {
            assertEquals(HttpStatusCode.OK, response.status())
            mapper.readValue<List<TestPerson>>(response.content.toString())
        }
        assertThat(list).isEmpty()
    }
}