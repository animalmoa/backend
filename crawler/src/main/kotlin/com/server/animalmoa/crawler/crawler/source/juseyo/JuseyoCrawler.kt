package com.server.animalmoa.crawler.crawler.source.juseyo

import com.server.animalmoa.common.adoption.domain.Source
import com.server.animalmoa.common.dto.MakeAdoptionDto
import com.server.animalmoa.crawler.crawler.service.AdoptionCrawler
import com.server.animalmoa.crawler.crawler.service.CrawlerErrorService
import com.server.animalmoa.crawler.crawler.service.LostCrawler
import com.server.animalmoa.crawler.webdriver.WebDriverCommandService
import mu.KotlinLogging
import org.openqa.selenium.By
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class JuseyoCrawler(
    private val webDriverCommandService: WebDriverCommandService,
    private val juseyoDataManageService: JuseyoDataManageService,
    private val crawlerErrorService: CrawlerErrorService,
) : AdoptionCrawler,
    LostCrawler {
    val logger = KotlinLogging.logger {}

    @Value("\${crawl-until.page}")
    private val maxPage: Int = 10

    override fun crawlAdoption() {
        val params =
            listOf(
                JuseyoData.cat(),
                JuseyoData.dog(),
            )
        for (page in 1..maxPage) {
            for (param in params) {
                val freeAdoptionUrl =
                    "https://www.zooseyo.com/sale/sale_list.php" +
                        "?animal=${param.animalParam}&page=$page&category=${param.categoryParam}&kind=&area=&categoryetc="
                webDriverCommandService.navigateTo(freeAdoptionUrl)
                searchEachPage(param)
            }
        }
    }

    private fun searchEachPage(dataExtractor: JuseyoData) {
        val postElements = webDriverCommandService.findElementsWithWaitingAlwaysAsList(dataExtractor.eachPostXpath)
        for (postElement in postElements) {
            /*
            아래 부분은 고양이, 개가 동일
             */
            crawlerErrorService.catchCrawlError({
                val title = postElement.findElement(By.xpath(dataExtractor.titleXpath)).text!!
                val postTypeImageSrc = postElement.findElement(By.xpath(dataExtractor.postTypeXpath)).getAttribute("src") ?: ""

                webDriverCommandService.clickElementWithAction(postElement)

                val originalWindow = webDriverCommandService.getWebDriver().windowHandle
                val newWindow = webDriverCommandService.getNewWindowThatIsNot(originalWindow)
                webDriverCommandService.switchToNewWindowAndReturnToOriginalWindow(
                    newWindow = newWindow,
                    originalWindow = originalWindow,
                ) {
                    val html = webDriverCommandService.getBody().text
                    println(html)
                    juseyoDataManageService
                        .processDataAndSave(
                            MakeAdoptionDto(
                                originalUrl = webDriverCommandService.getWebDriver().currentUrl,
                                title = title,
                                content = dataExtractor.content(html),
                                thumbnailUrl =
                                    webDriverCommandService
                                        .findElementWithWaiting(dataExtractor.thumbnailXpath)
                                        ?.getAttribute("src"),
                                createdAt =
                                    webDriverCommandService
                                        .findElementWithWaiting(dataExtractor.createdAtXpath)
                                        ?.text,
                                region = dataExtractor.region(html),
                                species = dataExtractor.species.toString(),
                                breed = dataExtractor.breed(html),
                                age = dataExtractor.age(html),
                                gender = dataExtractor.gender(html),
                                postType = postTypeImageSrc,
                                adoptionStatus = postTypeImageSrc,
                                source = Source.JUSEYO,
                                identifier = webDriverCommandService.getWebDriver().currentUrl,
                            ),
                        )
                }
            }, logger)
        }
    }

    override fun crawlLost() {
        TODO("Not yet implemented")
    }
}
