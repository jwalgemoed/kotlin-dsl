package nl.sourcelabs.dsl

import net.javacrumbs.jsonunit.*
import org.junit.*
import org.springframework.boot.test.web.client.*
import org.springframework.http.*

class Request private constructor(
        val path: String,
        val method: HttpMethod,
        val body: String?,
        val headers: HttpHeaders,
        val expect: Expectations
) {
    class Builder {
        var path = ""
        var method = HttpMethod.GET
        var body: String? = null
        var expect = Expectations.Builder()
        var headers = HttpHeaders()

        fun build(): Request {
            return Request(path, method, body?.trimIndent(), headers, expect.build())
        }
    }

    fun execute(testRestTemplate: TestRestTemplate) {
        val entity = if (body != null) {
            HttpEntity(body, headers)
        } else {
            HttpEntity(headers)
        }

        val response = testRestTemplate.exchange(path, method, entity, String::class.java)

        Assert.assertEquals(expect.status, response.statusCode)
        JsonAssert.assertJsonEquals(expect.body, response.body)
    }
}

class Expectations private constructor(
        val status: HttpStatus,
        val body: String?
) {
    class Builder {
        var status = HttpStatus.OK
        var body: String? = null

        fun build(): Expectations {
            return Expectations(status, body?.trimIndent())
        }
    }
}

fun request(builder: Request.Builder.() -> Unit) = Request.Builder().apply(builder).build()

fun Request.Builder.headers(headers: HttpHeaders.() -> Unit) {
    this.headers = HttpHeaders().apply(headers)
}

fun HttpHeaders.accept(value: String) {
    this.put(HttpHeaders.ACCEPT, listOf(value))
}

fun HttpHeaders.contentType(value: String) {
    this.put(HttpHeaders.CONTENT_TYPE, listOf(value))
}

fun Request.Builder.expect(builder: Expectations.Builder.() -> Unit) {
    this.expect = Expectations.Builder().apply(builder)
}