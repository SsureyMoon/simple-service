package com.simple.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.simple"])
class SimpleServiceApplication

fun main(args: Array<String>) {
    runApplication<SimpleServiceApplication>(*args)
}
