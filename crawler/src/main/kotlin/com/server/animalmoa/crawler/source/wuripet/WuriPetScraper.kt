package com.server.animalmoa.crawler.source.wuripet

import com.server.animalmoa.common.adoption.enum.Source
import com.server.animalmoa.common.dto.MakeAdoptionDto
import com.server.animalmoa.crawler.scraper.manager.AdoptionSaveManager
import com.server.animalmoa.crawler.scraper.manager.AdoptionToSave
import com.server.animalmoa.crawler.scraper.manager.Priority
import com.server.animalmoa.crawler.scraper.service.AdoptionScraper
import com.server.animalmoa.crawler.scraper.service.FindPostErrorService
import com.server.animalmoa.crawler.webdriver.WebDriverCommandService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class WuriPetScraper(
    webDriverCommandService: WebDriverCommandService,
    adoptionSaveManager: AdoptionSaveManager,
    findPostErrorService: FindPostErrorService,
) : AdoptionScraper(webDriverCommandService, adoptionSaveManager, findPostErrorService) {
    override val source: Source = Source.WURIPET

    @Value("\${scrap-until.page}")
    private val maxPage: Int = 10

    override fun findNewPost() {
        for (page in 1..maxPage) {
            val pageUrl = WuriPetHtmlParser.getEachAdoptionPage(page)
            findPostErrorService.catchScrawlPostListError {
                webDriverCommandService.navigateTo(pageUrl)
                val postElements = webDriverCommandService.findElementsWithXpathWaitingAlwaysAsList(WuriPetHtmlParser.postXpathes)

                postElements.forEach { element ->
                    findPostErrorService.catchScrawlPostError {
                        val postUrl = WuriPetHtmlParser.postUrl(element)
                        val postIdentifier = postUrl?.let { WuriPetHtmlParser.postIdentifier(it) }
                        val ifNewPost = scrapNewPost(postIdentifier, postUrl)

                        // 2025.08.21
                        // 우리펫의 경우 사이트 자체적으로 예전글의 작성일을 최신화 하여 최신글로 올리는 것으로 보인다.
                        // 새로운 글은 아니지만 작성일이 최신화 된 글들에 대하여 update 해준다
                        if (!ifNewPost) {
                            adoptionSaveManager.addAdoptionToSaveQueue(
                                AdoptionToSave(
                                    postUrl!!,
                                    Priority(Priority.OLD_POST_PRIORITY),
                                    { scrapAdoptionInformation(postUrl, postIdentifier!!) },
                                ),
                            )
                        }
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
