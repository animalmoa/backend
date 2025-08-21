package com.server.animalmoa.crawler.source.kara

import com.server.animalmoa.common.adoption.enum.Source
import com.server.animalmoa.common.dto.MakeAdoptionDto
import com.server.animalmoa.crawler.exception.LastPageNotFoundException
import com.server.animalmoa.crawler.scraper.manager.AdoptionSaveManager
import com.server.animalmoa.crawler.scraper.service.AdoptionScraper
import com.server.animalmoa.crawler.scraper.service.FindPostErrorService
import com.server.animalmoa.crawler.webdriver.WebDriverCommandService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class KaraScraper(
    webDriverCommandService: WebDriverCommandService,
    adoptionSaveManager: AdoptionSaveManager,
    findPostErrorService: FindPostErrorService,
) : AdoptionScraper(webDriverCommandService, adoptionSaveManager, findPostErrorService) {
    override val source: Source = Source.JUSEYO
    override val logger = KotlinLogging.logger { source }

    override fun findNewPost() {
        val adoptionPageUrl = KaraAdoptionHtmlParser.freeAdoptionPageUrl
        webDriverCommandService.navigateTo(adoptionPageUrl)

        findPostErrorService.catchScrawlPostListError {
            // lastPage를 구하지 못한다면 1 페이지만
            val lastPageNumber: Int =
                webDriverCommandService
                    .findElementWithXpathWaiting(KaraAdoptionHtmlParser.lastPageXpath)
                    ?.let {
                        KaraAdoptionHtmlParser.lastPageNumber(it)
                    }
                    ?: run {
                        findPostErrorService.saveErrorLog(LastPageNotFoundException())
                        1
                    }
            logger.info(lastPageNumber.toString())
        }
    }

    override fun scrapAdoptionInformation(
        postUrl: String,
        identifier: String,
    ): MakeAdoptionDto {
        TODO("Not yet implemented")
    }

    @Value("\${scrap-until.page}")
    private val maxPage: Int = 10
}
