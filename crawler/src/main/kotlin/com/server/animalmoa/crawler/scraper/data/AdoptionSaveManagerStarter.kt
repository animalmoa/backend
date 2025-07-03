package com.server.animalmoa.crawler.scraper.data

import com.server.animalmoa.crawler.scraper.service.ScheduledScrapService
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class AdoptionSaveManagerStarter(
    private val adoptionSaveManager: AdoptionSaveManager,
    private val scheduledScrapService: ScheduledScrapService,
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        scheduledScrapService.scrawl()
        adoptionSaveManager.consume()
    }
}
