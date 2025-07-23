package com.server.animalmoa.crawler.scraper.source.animalgo

import com.server.animalmoa.common.adoption.enum.Source
import com.server.animalmoa.crawler.scraper.service.AdoptionScraper
import com.server.animalmoa.crawler.scraper.service.ScraperErrorService
import com.server.animalmoa.crawler.scraper.threadmanager.AdoptionSaveManager
import com.server.animalmoa.crawler.webdriver.WebDriverCommandService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

 /*
 입양대상 동물 메뉴: 완료
 TODO 실종동물 페이지
  */
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
        val htmlParser = AnimalGoAdoptionHtmlParser.adoption()
        for (page in 1..maxPage) {
            val postUrl =
                "https://www.animal.go.kr/front/awtis/protection/protectionList.do?" +
                    "menuNo=${htmlParser.menuNoParam}" +
                    "&page=$page"

            scraperErrorService.catchScrawlPostListError(
                logger,
            ) {
                webDriverCommandService.navigateTo(postUrl)
                val postElements = webDriverCommandService.findElementsWithWaitingAlwaysAsList(htmlParser.postXpathes)

                postElements.forEach { element ->
                    scraperErrorService.catchScrawlPostError(
                        logger,
                    ) {
                        val identifier = htmlParser.postIdentifier(element.getAttribute("onclick"))
                        val postUrl = identifier?.let { htmlParser.postUrl(it) }
                        scrapNewPost(identifier, postUrl) {
                            htmlParser.getMakeAdoptionDto(
                                webDriverCommandService.getHtml(postUrl!!),
                                identifier,
                            )
                        }
                    }
                }
            }
        }
    }
}
