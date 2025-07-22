package com.server.animalmoa.crawler.scraper.source.juseyo

import com.server.animalmoa.common.adoption.enum.Source
import com.server.animalmoa.crawler.exception.AlreadySavedPostException
import com.server.animalmoa.crawler.scraper.service.AdoptionScraper
import com.server.animalmoa.crawler.scraper.threadmanager.AdoptionSaveManager
import com.server.animalmoa.crawler.scraper.threadmanager.AdoptionToSave
import com.server.animalmoa.crawler.webdriver.WebDriverCommandService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Suppress("ktlint:standard:no-consecutive-comments")
@Service
class JuseyoScraper(
    private val webDriverCommandService: WebDriverCommandService,
    private val adoptionSaveManager: AdoptionSaveManager,
) : AdoptionScraper {
    val logger = KotlinLogging.logger {}

    @Value("\${scrap-until.page}")
    private val maxPage: Int = 10

    override fun scrapAdoptionPost() {
        val animalCategories =
            listOf(
                JuseyoAdoptionHtmlParser.cat(),
                JuseyoAdoptionHtmlParser.dog(),
            )

        animalCategories.forEach category@{ animalCategory ->
            (1..maxPage).forEach page@{ page ->
                val eachPageOfCategory =
                    "https://www.zooseyo.com/sale/sale_list.php" +
                        "?animal=${animalCategory.animalParam}&page=$page" +
                        "&category=${animalCategory.categoryParam}&kind=&area=&categoryetc="
                try {
                    webDriverCommandService.navigateTo(eachPageOfCategory)
                    try {
                        searchEachCategory(animalCategory)
                    } catch (e: AlreadySavedPostException) {
                        logger.error { "stop scraping ${animalCategory.species} because ${e.message}" }
                        // 이미 있는 글이란 에러를 받았을 경우 다음 카테고리로 넘어간다.
                        return@category
                    }
                } catch (e: Exception) {
                    // 카테고리 각 페이지 글에 진입 못 했을 경우
                    // 다음 페이지 진입 가능성도 낮기에, 카테고리 변경
                    return@category
                }
            }
        }
    }

    private fun searchEachCategory(juseyoAdoptionHtmlParser: JuseyoAdoptionHtmlParser) {
        val postElements = webDriverCommandService.findElementsWithWaitingAlwaysAsList(juseyoAdoptionHtmlParser.postXpathes)

        postElements.forEach { element ->
            val eachPostUri = juseyoAdoptionHtmlParser.postUrl(element.getAttribute("onclick"))
            if (eachPostUri == null) {
                logger.error { "extracting post url fail " }
            } else {
                val eachPostUrl = Source.JUSEYO.url + eachPostUri
                if (adoptionSaveManager.isNewPost(Source.JUSEYO, JuseyoAdoptionHtmlParser.getIdentifier(eachPostUrl))) {
                    adoptionSaveManager.addAdoptionToQueue(
                        AdoptionToSave(
                            eachPostUrl,
                            {
                                juseyoAdoptionHtmlParser.getMakeAdoptionDto(
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
