package nl.sourcelabs.dsl

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.web.bind.annotation.*

@SpringBootApplication
class DslDemoApplication

@RestController
class HelloWorldController {

    @GetMapping("/api/hello/{name}")
    fun sayHi(@PathVariable name: String): Greeting {
        return Greeting("Hi, $name!")
    }
}

data class Greeting(val message: String)

fun main(args: Array<String>) {
    SpringApplication.run(DslDemoApplication::class.java, *args)
}