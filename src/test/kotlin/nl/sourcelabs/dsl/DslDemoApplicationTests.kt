package nl.sourcelabs.dsl

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

    @Test
    fun `Test 'Hello' endpoint using a valid name`() {
        val headers = HttpHeaders()
        val entity = HttpEntity<Any>(headers)

        val response = testRestTemplate.exchange("/api/hello/Jim", HttpMethod.GET, entity, Greeting::class.java)

        assertEquals("Hi, Jim!", response.body.message)
        assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun `Test 'Hello' with a simple DSL structure`() {
        request {
            path = "/api/hello/Jim"
            headers {
                accept("application/xml")
                contentType("text/css")
            }
            expect {
                status = HttpStatus.OK
                body = """{ "message": "Hi, Jim!" }"""
            }
        }.execute(testRestTemplate)
    }
}
