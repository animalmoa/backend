package com.server.animalmoa.crawler.source.juseyo

import com.server.animalmoa.common.adoption.enum.Source
import com.server.animalmoa.common.dto.MakeAdoptionDto
import com.server.animalmoa.crawler.scraper.manager.AdoptionSaveManager
import com.server.animalmoa.crawler.scraper.service.AdoptionScraper
import com.server.animalmoa.crawler.scraper.service.FindPostErrorService
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
    findPostErrorService: FindPostErrorService,
) : AdoptionScraper(webDriverCommandService, adoptionSaveManager, findPostErrorService) {
    override val source: Source = Source.JUSEYO
    override val logger = KotlinLogging.logger { source }

    @Value("\${scrap-until.page}")
    private val maxPage: Int = 10

    override fun findNewPost() {
        val juseyoCategories = JuseyoCategory.entries.toTypedArray()

        juseyoCategories.forEach category@{ catogory ->
            (1..maxPage).forEach page@{ page ->
                val eachPageOfCategory = JuseyoAdoptionHtmlParser.postListUrl(catogory.urlParam, page)
                runCatching {
                    findPostErrorService.catchScrawlPostListError {
                        webDriverCommandService.navigateTo(eachPageOfCategory)
                        val postElements = webDriverCommandService.findElementsWithWaitingAlwaysAsList(JuseyoAdoptionHtmlParser.postXpathes)

                        postElements.forEach { element ->
                            scrapEachPost(element)
                        }
                    }
                }
                    // 게시글 리스트 페이지 실패시 다음 카테고리 시도.
                    .onFailure { return@category }
            }
        }
    }

    override fun scrapAdoptionInformation(
        postUrl: String,
        identifier: String,
    ): MakeAdoptionDto =
        JuseyoAdoptionHtmlParser.getMakeAdoptionDto(
            html = webDriverCommandService.getHtml(postUrl),
            url = postUrl,
            identifier = identifier,
        )

    private fun scrapEachPost(element: WebElement) {
        findPostErrorService.catchScrawlPostError {
            val postUri = JuseyoAdoptionHtmlParser.postUrl(element)
            val postUrl = postUri?.let { Source.JUSEYO.url + it }
            val identifier = postUrl?.let { JuseyoAdoptionHtmlParser.getIdentifier(it) }
            scrapNewPost(identifier, postUrl)
        }
    }
}
