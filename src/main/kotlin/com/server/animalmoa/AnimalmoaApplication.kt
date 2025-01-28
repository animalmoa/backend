package com.server.animalmoa

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class AnimalmoaApplication

fun main(args: Array<String>) {
    runApplication<AnimalmoaApplication>(*args)
}
