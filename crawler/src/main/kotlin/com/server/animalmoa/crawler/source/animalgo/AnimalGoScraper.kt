package com.server.animalmoa.crawler.source.animalgo

import com.server.animalmoa.common.adoption.enum.Source
import com.server.animalmoa.common.dto.MakeAdoptionDto
import com.server.animalmoa.crawler.scraper.manager.AdoptionSaveManager
import com.server.animalmoa.crawler.scraper.service.AdoptionScraper
import com.server.animalmoa.crawler.scraper.service.FindPostErrorService
import com.server.animalmoa.crawler.webdriver.WebDriverCommandService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class AnimalGoScraper(
    webDriverCommandService: WebDriverCommandService,
    adoptionSaveManager: AdoptionSaveManager,
    findPostErrorService: FindPostErrorService,
) : AdoptionScraper(webDriverCommandService, adoptionSaveManager, findPostErrorService) {
    override val source = Source.ANIMAL_GO

    @Value("\${scrap-until.page}")
    private val maxPage: Int = 10

    override fun findNewPost() {
        for (page in 1..maxPage) {
            // TODO 입양 페이지의 게시글만 가져오고 있지만, 보호 페이지의 게시글들도 입양 가능한 동물들의 페이지로 보인다.
            val pageUrl = AnimalGoAdoptionHtmlParser.postListUrl(page)
            findPostErrorService.catchScrawlPostListError {
                webDriverCommandService.navigateTo(pageUrl)
                val postElements = webDriverCommandService.findElementsWithXpathWaitingAlwaysAsList(AnimalGoAdoptionHtmlParser.postXpathes)

                postElements.forEach { element ->
                    findPostErrorService.catchScrawlPostError {
                        val identifier = AnimalGoAdoptionHtmlParser.postIdentifier(element)
                        val postUrl = identifier?.let { AnimalGoAdoptionHtmlParser.postUrl(it) }
                        scrapNewPost(identifier, postUrl)
                    }
                }
            }
        }
    }

    override fun scrapAdoptionInformation(
        postUrl: String,
        identifier: String,
    ): MakeAdoptionDto =
        AnimalGoAdoptionHtmlParser.getMakeAdoptionDto(
            html = webDriverCommandService.getHtml(postUrl),
            identifier = identifier,
            postUrl = postUrl,
        )
}
