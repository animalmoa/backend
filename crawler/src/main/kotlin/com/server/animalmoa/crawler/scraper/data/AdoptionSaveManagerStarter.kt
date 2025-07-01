package com.server.animalmoa.crawler.scraper.data

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class AdoptionSaveManagerStarter(
    private val adoptionSaveManager: AdoptionSaveManager,
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        adoptionSaveManager.consume()
    }
}
