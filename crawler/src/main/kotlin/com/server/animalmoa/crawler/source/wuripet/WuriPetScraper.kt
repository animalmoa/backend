package com.server.animalmoa.crawler.source.wuripet

import com.server.animalmoa.common.adoption.enum.Source
import com.server.animalmoa.common.dto.MakeAdoptionDto
import com.server.animalmoa.crawler.scraper.manager.AdoptionSaveManager
import com.server.animalmoa.crawler.scraper.service.AdoptionScraper
import com.server.animalmoa.crawler.scraper.service.FindPostErrorService
import com.server.animalmoa.crawler.webdriver.WebDriverCommandService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class WuriPetScraper(
    webDriverCommandService: WebDriverCommandService,
    adoptionSaveManager: AdoptionSaveManager,
    findPostErrorService: FindPostErrorService,
) : AdoptionScraper(webDriverCommandService, adoptionSaveManager, findPostErrorService) {
    override val source: Source = Source.WURIPET
    override val logger = KotlinLogging.logger { source }

    @Value("\${scrap-until.page}")
    private val maxPage: Int = 10

    override fun findNewPost() {
        for (page in 1..maxPage) {
            val pageUrl = WuriPetHtmlParser.getEachAdoptionPage(page)
            findPostErrorService.catchScrawlPostListError {
                webDriverCommandService.navigateTo(pageUrl)
                val postElements = webDriverCommandService.findElementsWithWaitingAlwaysAsList(WuriPetHtmlParser.postXpathes)

                postElements.forEach { element ->
                    findPostErrorService.catchScrawlPostError {
                        val postUrl = WuriPetHtmlParser.postUrl(element)
                        val postIdentifier = postUrl?.let { WuriPetHtmlParser.postIdentifier(it) }
                        scrapNewPost(postIdentifier, postUrl)
                    }
                }
            }
        }
    }

    override fun scrapAdoptionInformation(
        postUrl: String,
        identifier: String,
    ): MakeAdoptionDto =
        WuriPetHtmlParser.getMakeAdoptionDto(
            html = webDriverCommandService.getHtml(postUrl),
            url = postUrl,
            identifier = identifier,
        )
}
