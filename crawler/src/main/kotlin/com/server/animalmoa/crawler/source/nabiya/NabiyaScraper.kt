package com.server.animalmoa.crawler.source.nabiya

import com.server.animalmoa.common.adoption.enum.AdoptionStatus
import com.server.animalmoa.common.adoption.enum.Source
import com.server.animalmoa.common.adoption.enum.Species
import com.server.animalmoa.common.common.PostType
import com.server.animalmoa.common.dto.MakeAdoptionDto
import com.server.animalmoa.crawler.scraper.manager.AdoptionSaveManager
import com.server.animalmoa.crawler.scraper.service.AdoptionScraper
import com.server.animalmoa.crawler.scraper.service.FindPostErrorService
import com.server.animalmoa.crawler.scraper.util.JsoupUtil
import com.server.animalmoa.crawler.scraper.util.UrlUtil
import com.server.animalmoa.crawler.webdriver.WebDriverCommandService
import org.springframework.stereotype.Service

@Service
class NabiyaScraper(
    webDriverCommandService: WebDriverCommandService,
    adoptionSaveManager: AdoptionSaveManager,
    findPostErrorService: FindPostErrorService,
) : AdoptionScraper(webDriverCommandService, adoptionSaveManager, findPostErrorService) {
    override val source: Source = Source.NABIYA

    override fun findNewPost() {
        val freeAdoptionListUrls = listOf("https://nabiya.org/adoptable", "https://nabiya.org/shelter")
        freeAdoptionListUrls.forEach { freeAdoptionListUrl ->
            webDriverCommandService.navigateTo(freeAdoptionListUrl)
            findPostErrorService.catchScrawlPostListError {
                val lastPageNumber: Int =
                    webDriverCommandService
                        .findElementWithXpathWaiting("//ul[contains(@class, 'pagination')]//li[last()-1]/a")
                        ?.let {
                            it.getAttribute("href")?.let {
                                UrlUtil.extractQueryParam(it, "page")?.toIntOrNull()
                            }
                        } ?: 1

                for (p in 1..lastPageNumber) {
                    val eachPage = freeAdoptionListUrl + "?page=$p"
                    webDriverCommandService.navigateTo(eachPage)

                    val postElements =
                        webDriverCommandService.findElementsWithXpathWaitingAlwaysAsList(
                            "//div[contains(@class, '_shop_item')]//div[1]/a",
                        )
                    postElements.forEach { post ->

                        findPostErrorService.catchScrawlPostError {
                            val identifier =
                                post.getAttribute("href")?.let {
                                    UrlUtil.extractQueryParam(it, "idx")
                                }
                            val postUrl = identifier?.let { freeAdoptionListUrl + "?idx=$identifier" }
                            println(postUrl)

                            scrapNewPost(identifier, postUrl)
                        }
                    }
                }
            }
        }
    }

    override fun scrapAdoptionInformation(
        postUrl: String,
        identifier: String,
    ): MakeAdoptionDto {
        val thumbnailXpath = "//img[@id='main-image']"
        val html =
            webDriverCommandService.getHtmlWithWaitingElement(
                postUrl,
                thumbnailXpath,
            )
        val titleXpath = "//*[@id=\"prod_goods_form\"]/header/div[1]"
        val ageXpath = "//*[@id=\"prod_goods_form\"]/div[1]/div/table/tbody/tr[4]/td[2]/span"

        // contents 추출로직 수정
        val contentXpath = "//*[@id=\"prod_detail_body\"]"

        val genderXpath = "//*[@id=\"prod_goods_form\"]/div[1]/div/table/tbody/tr[3]/td[2]/span"
        return MakeAdoptionDto(
            speciesSynonym = Species.CAT.synonyms.first(),
            breedSynonym = null,
            regionSynonym = null,
            genderSynonym = JsoupUtil.findFirstElementTextWithXpath(html, genderXpath)?.get(0).toString(),
            title = JsoupUtil.findFirstElementTextWithXpath(html, titleXpath),
            content = JsoupUtil.findFirstElementTextWithXpath(html, contentXpath),
            age = JsoupUtil.findFirstElementTextWithXpath(html, ageXpath),
            thumbnailUrl = JsoupUtil.findImgSrcWithXpath(html, thumbnailXpath),
            // TODO 매번 createdAt이 update 된다. 이를 막아야함
            createdAt = null,
            originalUrl = postUrl,
            adoptionStatus = AdoptionStatus.ING,
            source = Source.NABIYA,
            postType = PostType.FREE_ADOPTION,
            identifier = identifier,
        )
    }
}
