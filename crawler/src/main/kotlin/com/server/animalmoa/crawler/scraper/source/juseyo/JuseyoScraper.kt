package com.server.animalmoa.crawler.scraper.source.juseyo

import com.server.animalmoa.common.adoption.domain.Source
import com.server.animalmoa.crawler.exception.AlreadySavedPostException
import com.server.animalmoa.crawler.scraper.service.AdoptionScraper
import com.server.animalmoa.crawler.scraper.service.ScraperErrorService
import com.server.animalmoa.crawler.scraper.starter.AdoptionSaveManager
import com.server.animalmoa.crawler.scraper.starter.AdoptionToSave
import com.server.animalmoa.crawler.webdriver.WebDriverCommandService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Suppress("ktlint:standard:no-consecutive-comments")
@Service
class JuseyoScraper(
    private val webDriverCommandService: WebDriverCommandService,
    private val adoptionSaveManager: AdoptionSaveManager,
    private val scraperErrorService: ScraperErrorService,
) : AdoptionScraper {
    val logger = KotlinLogging.logger {}

    @Value("\${scrap-until.page}")
    private val maxPage: Int = 10

    override fun scrapAdoptionPost() {
        val animalCategories =
            listOf(
                JuseyoHtmlParser.dog(),
            )

        for (animalCategory in animalCategories) {
            try {
                for (page in 1..maxPage) {
                    val freeAdoptionUrl =
                        "https://www.zooseyo.com/sale/sale_list.php" +
                            "?animal=${animalCategory.animalParam}&page=$page" +
                            "&category=${animalCategory.categoryParam}&kind=&area=&categoryetc="

                    webDriverCommandService.navigateTo(freeAdoptionUrl)
                    searchEachCategory(animalCategory)
                }
            } catch (e: AlreadySavedPostException) {
                // 이미 있는 글이면 다음 카테고리로
                logger.error { "stop scraping ${animalCategory.species} because ${e.message}" }
                continue
            }
        }
    }

    private fun searchEachCategory(juseyoHtmlParser: JuseyoHtmlParser) {
        val postElements = webDriverCommandService.findElementsWithWaitingAlwaysAsList(juseyoHtmlParser.eachPostXpath)

        postElements.forEach { element ->
            val eachPostUri = juseyoHtmlParser.postUrl(element.getAttribute("onclick"))
            if (eachPostUri == null) {
                logger.error { "extracting post url fail " }
            } else {
                val eachPostUrl = Source.JUSEYO.url + eachPostUri
                if (adoptionSaveManager.isNewPost(Source.JUSEYO, JuseyoHtmlParser.getIdentifier(eachPostUrl))) {
                    adoptionSaveManager.addAdoptionToQueue(
                        AdoptionToSave(
                            eachPostUrl,
                            {
                                juseyoHtmlParser.getMakeAdoptionDto(
                                    webDriverCommandService.getHtml(eachPostUrl),
                                    eachPostUrl,
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
