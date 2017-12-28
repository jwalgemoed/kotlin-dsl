package nl.sourcelabs.dsl

import net.javacrumbs.jsonunit.*
import org.junit.*
import org.springframework.boot.test.web.client.*
import org.springframework.http.*

data class Contract private constructor(val request: Request, val response: Response) {

    class Builder {
        var request = Request.Builder()
        var response = Response.Builder()

        fun build(): Contract {
            return Contract(request.build(), response.build())
        }
    }

    fun verify(testRestTemplate: TestRestTemplate) {
        val entity = if (request.body != null) {
            HttpEntity(request.body, request.headers)
        } else {
            HttpEntity(request.headers)
        }

        val actualResponse = testRestTemplate.exchange(request.path, request.method, entity, String::class.java)

        Assert.assertEquals("Actual HTTP response code doesn't match expected value.", response.status, actualResponse.statusCode)
        JsonAssert.assertJsonEquals(response.body, actualResponse.body)
    }
}

class Request private constructor(
        val path: String,
        val method: HttpMethod,
        val body: String?,
        val headers: HttpHeaders
) {
    class Builder {
        var path = ""
        var method = HttpMethod.GET
        var body: String? = null
        var expect = Response.Builder()
        var headers = HttpHeaders()

        fun build(): Request {
            return Request(path, method, body?.trimIndent(), headers)
        }
    }
}

class Response private constructor(
        val status: HttpStatus,
        val body: String?
) {
    class Builder {
        var status = HttpStatus.OK
        var body: String? = null

        fun build(): Response {
            return Response(status, body?.trimIndent())
        }
    }
}

fun contract(builder: Contract.Builder.() -> Unit): Contract = Contract.Builder().apply(builder).build()

fun Contract.Builder.request(builder: Request.Builder.() -> Unit) {
    this.request = Request.Builder().apply(builder)
}

fun Contract.Builder.response(builder: Response.Builder.() -> Unit) {
    this.response = Response.Builder().apply(builder)
}

fun Request.Builder.headers(headers: HttpHeaders.() -> Unit) {
    this.headers = HttpHeaders().apply(headers)
}

fun HttpHeaders.accept(value: String) {
    this.put(HttpHeaders.ACCEPT, listOf(value))
}

fun HttpHeaders.contentType(value: String) {
    this.put(HttpHeaders.CONTENT_TYPE, listOf(value))
}
