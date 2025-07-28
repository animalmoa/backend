package com.server.animalmoa.crawler.source.wuripet

import com.server.animalmoa.common.adoption.enum.Source
import com.server.animalmoa.crawler.scraper.manager.AdoptionSaveManager
import com.server.animalmoa.crawler.scraper.service.AdoptionScraper
import com.server.animalmoa.crawler.scraper.service.ScraperErrorService
import com.server.animalmoa.crawler.webdriver.WebDriverCommandService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class WuriPetScraper(
    webDriverCommandService: WebDriverCommandService,
    adoptionSaveManager: AdoptionSaveManager,
    scraperErrorService: ScraperErrorService,
) : AdoptionScraper(webDriverCommandService, adoptionSaveManager, scraperErrorService) {
    override val source: Source = Source.WURIPET
    override val logger = KotlinLogging.logger { source }

    @Value("\${scrap-until.page}")
    private val maxPage: Int = 10

    override fun scrapAdoptionPost() {
        for (page in 1..maxPage) {
            val pageUrl = WuriPetHtmlParser.getEachAdoptionPage(page)
            scraperErrorService.catchScrawlPostListError {
                webDriverCommandService.navigateTo(pageUrl)
                val postElements = webDriverCommandService.findElementsWithWaitingAlwaysAsList(WuriPetHtmlParser.postXpathes)

                postElements.forEach { element ->
                    scraperErrorService.catchScrawlPostError {
                    }
                    println(element.getAttribute("href"))
                }
            }
        }
    }
}
