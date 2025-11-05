package com.server.animalmoa.crawler.source.kara

import com.server.animalmoa.common.adoption.enum.Source
import com.server.animalmoa.common.dto.MakeAdoptionDto
import com.server.animalmoa.crawler.scraper.manager.AdoptionSaveManager
import com.server.animalmoa.crawler.scraper.service.AdoptionScraper
import com.server.animalmoa.crawler.scraper.service.FindPostErrorService
import com.server.animalmoa.crawler.webdriver.WebDriverCommandService
import org.springframework.stereotype.Service

@Service
class KaraScraper(
    webDriverCommandService: WebDriverCommandService,
    adoptionSaveManager: AdoptionSaveManager,
    findPostErrorService: FindPostErrorService,
) : AdoptionScraper(webDriverCommandService, adoptionSaveManager, findPostErrorService) {
    override val source: Source = Source.KARA

    val waitSecond = 5

    override fun findNewPost() {
        val freeAdoptionPagesUrl = KaraAdoptionHtmlParser.freeAdoptionPageUrl
        webDriverCommandService.navigateTo(freeAdoptionPagesUrl, waitSecond)
        findPostErrorService.catchScrawlPostListError {
            // lastPage를 구하지 못한다면 1 페이지만
            val lastPageNumber: Int =
                webDriverCommandService
                    .findElementWithXpathWaiting(KaraAdoptionHtmlParser.lastPageXpath)
                    ?.let(KaraAdoptionHtmlParser::lastPageNumber) ?: 1

            for (i in 1..lastPageNumber) {
                val eachPage = "${KaraAdoptionHtmlParser.freeAdoptionPageUrl}&page=$i"
                webDriverCommandService.navigateTo(eachPage, waitSecond)

                val postElements =
                    webDriverCommandService.findElementsWithXpathWaitingAlwaysAsList(KaraAdoptionHtmlParser.postsXpath)

                postElements.forEach { element ->
                    findPostErrorService.catchScrawlPostError {
                        val identifier = KaraAdoptionHtmlParser.postIdentifier(element)
                        val postUrl = identifier?.let { KaraAdoptionHtmlParser.postUrl(it) }
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
        KaraAdoptionHtmlParser.getMakeAdoptionDto(
            html = webDriverCommandService.getHtmlWithWaitingElement(postUrl, KaraAdoptionHtmlParser.thumbnailXpath, waitSecond),
            url = postUrl,
            identifier = identifier,
        )
}
