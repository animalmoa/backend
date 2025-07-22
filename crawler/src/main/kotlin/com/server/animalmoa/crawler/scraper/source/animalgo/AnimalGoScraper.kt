package com.server.animalmoa.crawler.scraper.source.animalgo

import com.server.animalmoa.common.adoption.enum.Source
import com.server.animalmoa.crawler.exception.AlreadySavedPostException
import com.server.animalmoa.crawler.scraper.service.AdoptionScraper
import com.server.animalmoa.crawler.scraper.threadmanager.AdoptionSaveManager
import com.server.animalmoa.crawler.scraper.threadmanager.AdoptionToSave
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
    private val webDriverCommandService: WebDriverCommandService,
    private val adoptionSaveManager: AdoptionSaveManager,
) : AdoptionScraper {
    private val logger = KotlinLogging.logger {}

    @Value("\${scrap-until.page}")
    private val maxPage: Int = 10

    override fun scrapAdoptionPost() {
        val htmlParser = AnimalGoAdoptionHtmlParser.adoption()
        for (page in 1..maxPage) {
            val freeAdoptionUrl =
                "https://www.animal.go.kr/front/awtis/protection/protectionList.do?" +
                    "menuNo=${htmlParser.menuNoParam}" +
                    "&page=$page"
            webDriverCommandService.navigateTo(freeAdoptionUrl)
            searchEachPage(htmlParser)
        }
    }

    private fun searchEachPage(animalGoAdoptionHtmlParser: AnimalGoAdoptionHtmlParser) {
        val eachPostElements = webDriverCommandService.findElementsWithWaitingAlwaysAsList(animalGoAdoptionHtmlParser.postXpathes)
        eachPostElements.forEach { element ->
            val eachPostIdentifier = animalGoAdoptionHtmlParser.postIdentifier(element.getAttribute("onclick"))
            if (eachPostIdentifier == null) {
                logger.error { "extracting identifier fail" }
            } else {
                val eachPostUrl = animalGoAdoptionHtmlParser.postUrl(eachPostIdentifier)
                if (adoptionSaveManager.isNewPost(Source.ANIMAL_GO, eachPostIdentifier)) {
                    adoptionSaveManager.addAdoptionToQueue(
                        AdoptionToSave(
                            eachPostUrl,
                            {
                                animalGoAdoptionHtmlParser.getMakeAdoptionDto(
                                    webDriverCommandService.getHtml(eachPostUrl),
                                    eachPostIdentifier,
                                )
                            },
                            AdoptionToSave.NEW_POST_PRIORITY,
                        ),
                    )
                } else {
                    throw AlreadySavedPostException(eachPostUrl)
                }
            }
        }
    }
}
