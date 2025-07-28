package com.server.animalmoa.crawler.source.juseyo

import com.server.animalmoa.common.adoption.enum.Source
import com.server.animalmoa.crawler.scraper.manager.AdoptionSaveManager
import com.server.animalmoa.crawler.scraper.service.AdoptionScraper
import com.server.animalmoa.crawler.scraper.service.ScraperErrorService
import com.server.animalmoa.crawler.webdriver.WebDriverCommandService
import mu.KotlinLogging
import org.openqa.selenium.WebElement
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Suppress("ktlint:standard:no-consecutive-comments")
@Service
class JuseyoScraper(
    webDriverCommandService: WebDriverCommandService,
    adoptionSaveManager: AdoptionSaveManager,
    scraperErrorService: ScraperErrorService,
) : AdoptionScraper(webDriverCommandService, adoptionSaveManager, scraperErrorService) {
    override val source: Source = Source.JUSEYO
    override val logger = KotlinLogging.logger { source }

    @Value("\${scrap-until.page}")
    private val maxPage: Int = 10

    override fun scrapAdoptionPost() {
        val htmlParserByCategory =
            listOf(
                JuseyoAdoptionHtmlParser.cat(),
                JuseyoAdoptionHtmlParser.dog(),
            )

        htmlParserByCategory.forEach category@{ htmlParser ->
            (1..maxPage).forEach page@{ page ->
                val eachPageOfCategory =
                    "https://www.zooseyo.com/sale/sale_list.php" +
                        "?animal=${htmlParser.animalParam}&page=$page"
//                + "&category=${htmlParser.categoryParam}&kind=&area=&categoryetc="
                runCatching {
                    scraperErrorService.catchScrawlPostListError {
                        webDriverCommandService.navigateTo(eachPageOfCategory)
                        val postElements = webDriverCommandService.findElementsWithWaitingAlwaysAsList(htmlParser.postXpathes)

                        postElements.forEach { element ->
                            scrapEachPost(htmlParser, element)
                        }
                    }
                }
                    // 게시글 리스트 페이지 실패시 다음 카테고리 시도.
                    .onFailure { return@category }
            }
        }
    }

    private fun scrapEachPost(
        htmlParser: JuseyoAdoptionHtmlParser,
        element: WebElement,
    ) {
        scraperErrorService.catchScrawlPostError {
            val postUri = htmlParser.postUrl(element)
            val postUrl = postUri?.let { Source.JUSEYO.url + it }
            val identifier = postUrl?.let { JuseyoAdoptionHtmlParser.getIdentifier(it) }

            scrapNewPost(identifier, postUrl) {
                htmlParser.getMakeAdoptionDto(
                    webDriverCommandService.getHtml(postUrl!!),
                    postUrl,
                    identifier!!,
                )
            }
        }
    }
}
