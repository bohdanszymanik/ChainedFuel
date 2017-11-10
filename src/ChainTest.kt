import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.result.Result
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import com.fasterxml.jackson.module.kotlin.*
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.*

fun main(args: Array<String>) {

    // getting to grips with Result... https://github.com/kittinunf/Result
    val r1: Result<Int, Exception> = Result.of(1)
    val r2: Result<Int, Exception> = Result.of { throw Exception("Not a number") }
    val r3: Result<Int, Exception> = Result.of(3)
    val r4: Result<Int, Exception> = Result.of { throw Exception("Division by zero") }

    val validation = Validation(r1, r2, r3, r4)
    validation.hasFailure //true
    validation.failures.map { it.message } //[Not a number, Division by zero]

    when (r3) {
        is Result.Success -> {
            println("Success")
        }
        is Result.Failure -> {
            println("Failure")
        }
    }

    val r3m = r3.map { it * 3 } // using the implicit 'it'
    val r3m1 = r3.map { i -> i * 4 } // being explicit

    val r4m = r4.map { it * 3 } // using the implicit 'it'

    fun test(r: Result<Int, Exception>) = { Validation(r).hasFailure }


    val jsonMapper = ObjectMapper().registerModule(KotlinModule())


    fun h1() =
            "http://api.geonet.org.nz/quake?MMI=3"
                    .httpGet()
                    .responseString { request: Request, response: Response, result -> result
                        println(response.responseMessage)
                    }

    fun h2() =
            "http://api.geonet.org.nz/quake?MMI=4"
                    .httpGet()
                    .responseString { request: Request, response: Response, result -> result
                        println(response.responseMessage)
                    }

    fun h3() =
            "http://api.geonet.org.nz/quake?MMI=5"
                    .httpGet()
                    .responseString { request: Request, response: Response, result -> result
                        println(response.responseMessage)
                    }


    h1().response().component3().map {  h2()}.map { h3() }

    // yay!
}