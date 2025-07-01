package com.server.animalmoa.crawler.crawler.source.ijoa

import com.server.animalmoa.common.adoption.domain.Source
import com.server.animalmoa.common.dto.MakeAdoptionDto
import com.server.animalmoa.crawler.crawler.service.AdoptionCrawler
import com.server.animalmoa.crawler.crawler.service.CrawlerErrorService
import com.server.animalmoa.crawler.webdriver.WebDriverCommandService
import mu.KotlinLogging
import org.openqa.selenium.By
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class IjoaCrawler(
    private val webDriverCommandService: WebDriverCommandService,
    private val ijoaDataManageService: IjoaDataManageService,
    private val crawlerErrorService: CrawlerErrorService,
) : AdoptionCrawler {
    val logger = KotlinLogging.logger {}

    @Value("\${crawl-until.page}")
    private val maxPage: Int = 10

    override fun crawlAdoption() {
        val params =
            listOf(
                IjoaData.cat(),
                IjoaData.dog(),
            )

        for (param in params) {
            for (page in 1..maxPage) {
                val url = "${param.url}?page=$page"
                webDriverCommandService.navigateTo(url)
                searchEachPage(param)
            }
        }
    }

    private fun searchEachPage(dataExtractor: IjoaData) {
        val postElements = webDriverCommandService.findElementsWithWaitingAlwaysAsList(dataExtractor.eachPostXpath)

        for (postElement in postElements) {
            crawlerErrorService.catchCrawlError({
                val title = postElement.findElement(By.xpath(dataExtractor.titleXpath)).text
                val thumbnailUrl = postElement.findElement(By.xpath(dataExtractor.thumbnailXpath)).getAttribute("src") ?: ""
                val linkElement = postElement.findElement(By.xpath(dataExtractor.linkXpath))

                webDriverCommandService.clickElementWithAction(linkElement)

                // 상세 페이지에서 데이터 추출
                val originalUrl = webDriverCommandService.getWebDriver().currentUrl
                val content = webDriverCommandService.findElementWithWaiting(dataExtractor.contentXpath)?.text ?: ""
                val createdAt = webDriverCommandService.findElementWithWaiting(dataExtractor.createdAtXpath)?.text

                // 정보 테이블에서 데이터 추출
                val infoTable = webDriverCommandService.findElementWithWaiting(dataExtractor.infoTableXpath)?.text ?: ""

                ijoaDataManageService.processDataAndSave(
                    MakeAdoptionDto(
                        originalUrl = originalUrl,
                        title = title,
                        content = content,
                        thumbnailUrl = thumbnailUrl,
                        createdAt = createdAt,
                        region = dataExtractor.region(infoTable),
                        species = dataExtractor.species.toString(),
                        breed = dataExtractor.breed(infoTable),
                        age = dataExtractor.age(infoTable),
                        gender = dataExtractor.gender(infoTable),
                        source = Source.IJOA,
                        identifier = originalUrl,
                        postType = "FREE_ADOPTION",
                        adoptionStatus = "ING",
                    ),
                )

                // 상세 페이지에서 목록으로 돌아가기
                webDriverCommandService.goBack()
            }, logger)
        }
    }
}
