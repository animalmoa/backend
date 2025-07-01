package com.server.animalmoa.crawler.scraper.source.juseyo

import com.server.animalmoa.common.adoption.domain.Source
import com.server.animalmoa.common.dto.MakeAdoptionDto
import com.server.animalmoa.crawler.scraper.service.AdoptionScraper
import com.server.animalmoa.crawler.scraper.service.LostAnimalScraper
import com.server.animalmoa.crawler.scraper.service.ScraperErrorService
import com.server.animalmoa.crawler.webdriver.WebDriverCommandService
import mu.KotlinLogging
import org.openqa.selenium.By
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class JuseyoScraper(
    private val webDriverCommandService: WebDriverCommandService,
    private val juseyoDataManageService: JuseyoDataManageService,
    private val scraperErrorService: ScraperErrorService,
) : AdoptionScraper,
    LostAnimalScraper {
    val logger = KotlinLogging.logger {}

    @Value("\${crawl-until.page}")
    private val maxPageToSearch: Int = 10

    override fun scrapAdoptionPost() {
        val scrapData =
            listOf(
                JuseyoData.cat(),
                JuseyoData.dog(),
            )
        for (page in 1..maxPageToSearch) {
            for (param in scrapData) {
                val adoptionPostUrl =
                    "https://www.zooseyo.com/sale/sale_list.php" +
                        "?animal=${param.animalParam}&page=$page&category=${param.categoryParam}&kind=&area=&categoryetc="
                webDriverCommandService.navigateTo(adoptionPostUrl)
                searchEachPage(param)
            }
        }
    }

    private fun searchEachPage(xpathes: JuseyoData) {
        val elements = webDriverCommandService.findElementsWithWaitingAlwaysAsList(xpathes.eachPostXpath)
        for (element in elements) {
            /*
            아래 부분은 고양이, 개가 동일
             */
            val title = element.findElement(By.xpath(xpathes.essential.titleXpath)).text ?: ""
            val postTypeImageSrc = element.findElement(By.xpath(xpathes.postTypeXpath)).getAttribute("src") ?: ""
            scraperErrorService.catchScrawlError({
                webDriverCommandService.clickElementWithAction(element)
                val originalWindow = webDriverCommandService.getWebDriver().windowHandle
                val newWindow = webDriverCommandService.getNewWindowThatIsNot(originalWindow)
                webDriverCommandService.switchToNewWindowAndReturnToOriginalWindow(
                    newWindow = newWindow,
                    originalWindow = originalWindow,
                ) {
                    juseyoDataManageService
                        .parseDataAndSave(
                            MakeAdoptionDto(
                                originalUrl = webDriverCommandService.getWebDriver().currentUrl,
                                title = title,
                                content =
                                    webDriverCommandService
                                        .findElementWithWaiting(xpathes.essential.contentXpath)
                                        ?.text,
                                thumbnailUrl =
                                    webDriverCommandService
                                        .findElementWithWaiting(xpathes.essential.thumbnailXpath)
                                        ?.getAttribute("src"),
                                createdAt =
                                    webDriverCommandService
                                        .findElementWithWaiting(xpathes.createdAtXpath)
                                        ?.text,
                                region = webDriverCommandService.getText(xpathes.essential.regionXpath),
                                species = webDriverCommandService.getText(xpathes.essential.speciesXpath),
                                breed = webDriverCommandService.getText(xpathes.essential.breedXpath),
                                age = webDriverCommandService.getText(xpathes.essential.ageXpath),
                                gender = webDriverCommandService.getText(xpathes.essential.genderXpath),
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
