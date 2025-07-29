package com.server.animalmoa.crawler.source.animalgo

import com.server.animalmoa.common.adoption.enum.Source
import com.server.animalmoa.crawler.scraper.manager.AdoptionSaveManager
import com.server.animalmoa.crawler.scraper.service.AdoptionScraper
import com.server.animalmoa.crawler.scraper.service.ScraperErrorService
import com.server.animalmoa.crawler.webdriver.WebDriverCommandService
import mu.KotlinLogging
import org.jsoup.parser.Parser.htmlParser
import org.openqa.selenium.WebElement
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class AnimalGoScraper(
    webDriverCommandService: WebDriverCommandService,
    adoptionSaveManager: AdoptionSaveManager,
    scraperErrorService: ScraperErrorService,
) : AdoptionScraper(webDriverCommandService, adoptionSaveManager, scraperErrorService) {
    override val source = Source.ANIMAL_GO
    override val logger = KotlinLogging.logger { source }

    @Value("\${scrap-until.page}")
    private val maxPage: Int = 10

    override fun scrapAdoptionPost() {
        for (page in 1..maxPage) {
            val pageUrl = AnimalGoAdoptionHtmlParser.postListUrl(page)
            scraperErrorService.catchScrawlPostListError {
                webDriverCommandService.navigateTo(pageUrl)
                val postElements = webDriverCommandService.findElementsWithWaitingAlwaysAsList(htmlParser.postXpathes)

                postElements.forEach { element ->
                    scrapEachPost(element)
                }
            }
        }
    }

    private fun scrapEachPost(element: WebElement) {
        scraperErrorService.catchScrawlPostError {
            val identifier = AnimalGoAdoptionHtmlParser.postIdentifier(element.getAttribute("onclick"))
            val postUrl = identifier?.let { AnimalGoAdoptionHtmlParser.postUrl(it) }
            scrapNewPost(identifier, postUrl) {
                AnimalGoAdoptionHtmlParser.getMakeAdoptionDto(
                    webDriverCommandService.getHtml(postUrl!!),
                    identifier,
                    postUrl,
                )
            }
        }
    }
}
