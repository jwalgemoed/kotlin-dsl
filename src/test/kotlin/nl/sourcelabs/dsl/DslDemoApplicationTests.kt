package nl.sourcelabs.dsl

import net.javacrumbs.jsonunit.*
import net.javacrumbs.jsonunit.JsonAssert.*
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.*
import org.springframework.beans.factory.annotation.*
import org.springframework.boot.test.context.*
import org.springframework.boot.test.web.client.*
import org.springframework.http.*
import org.springframework.test.context.junit4.*

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DslDemoApplicationTests {

    @Autowired
    private lateinit var testRestTemplate: TestRestTemplate

    /**
     * 'Classic test' -> We are replacing this with our DSL
     */
    @Test
    fun `Test 'Hello' endpoint using a valid name`() {
        val headers = HttpHeaders()
        headers.put("Accept", listOf("application/json"))

        val entity = HttpEntity<Any>(headers)

        val response = testRestTemplate.exchange("/api/hello/Jarno", HttpMethod.GET, entity, String::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertJsonEquals("{ \"message\": \"Hi, Jarno!\" }", response.body)
    }

    /**
     * Tests using the DSL for verifications
     */
    @Test
    fun `Test 'Hello' with a simple DSL structure`() {
        contract {
            request {
                path = "/api/hello/Jarno"
                method = HttpMethod.GET
                headers {
                    accept("application/json")
                }
            }
            response {
                status = HttpStatus.OK
                body = """{ "message": "Hi, Jarno!" }"""
            }
        }.verify(testRestTemplate)
    }

    @Test
    fun `Test 'Hello' with unsupported accept header`() {
        contract {
            request {
                path = "/api/hello/Jarno"
                method = HttpMethod.GET
                headers {
                    accept("application/xml")
                }
            }
            response {
                status = HttpStatus.NOT_ACCEPTABLE // not expecting a body here
            }
        }.verify(testRestTemplate)
    }

    @Test
    fun `Test 'Hello' with a simple DSL structure - String interpolation & multiline`() {
        val name = "Jarno" // Re-use the variable to make sure we compare the right values.
        contract {
            request {
                path = "/api/hello/$name" // Interpolation here too
                method = HttpMethod.GET
                headers {
                    accept("application/json")
                }
            }
            response {
                status = HttpStatus.OK
                body = """
                    {
                        "message": "Hi, $name!"
                    }
                """ // Multiline string, easy to read + interpolation
            }
        }.verify(testRestTemplate)
    }
}
