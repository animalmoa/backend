package com.server.animalmoa.crawler

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableJpaRepositories(basePackages = ["com.server.animalmoa.common"])
@ComponentScan(basePackages = ["com.server.animalmoa.common", "com.server.animalmoa.crawler"])
@EntityScan(basePackages = ["com.server.animalmoa.common"])
class ScraperApplication {
    fun main(args: Array<String>) {
        runApplication<ScraperApplication>(*args)
    }
}
