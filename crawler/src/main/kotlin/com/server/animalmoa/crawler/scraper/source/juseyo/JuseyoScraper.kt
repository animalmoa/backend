package com.server.animalmoa.crawler.crawler.source.juseyo

import com.server.animalmoa.common.adoption.domain.Source
import com.server.animalmoa.crawler.exception.AlreadySavedPostException
import com.server.animalmoa.crawler.scraper.data.AdoptionSaveManager
import com.server.animalmoa.crawler.scraper.data.AdoptionToSave
import com.server.animalmoa.crawler.scraper.service.AdoptionScraper
import com.server.animalmoa.crawler.scraper.source.juseyo.JuseyoData
import com.server.animalmoa.crawler.scraper.source.juseyo.JuseyoDataParseService
import com.server.animalmoa.crawler.webdriver.WebDriverCommandService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class JuseyoScraper(
    private val webDriverCommandService: WebDriverCommandService,
    private val juseyoDataParseService: JuseyoDataParseService,
    private val adoptionSaveManager: AdoptionSaveManager,
) : AdoptionScraper {
    val logger = KotlinLogging.logger {}

    @Value("\${crawl-until.page}")
    private val maxPage: Int = 10

    override fun scrapAdoptionPost() {
        {
            val animalCategories =
                listOf(
                    JuseyoData.cat(),
                    JuseyoData.dog(),
                )
            for (page in 1..maxPage) {
                for (animalCategory in animalCategories) {
                    val freeAdoptionUrl =
                        "https://www.zooseyo.com/sale/sale_list.php" +
                            "?animal=${animalCategory.animalParam}&page=$page&category=${animalCategory.categoryParam}&kind=&area=&categoryetc="
                    webDriverCommandService.navigateTo(freeAdoptionUrl)
                    searchEachPage(animalCategory)
                }
            }
        }
    }

    private fun searchEachPage(dataExtractor: JuseyoData) {
        val postElements = webDriverCommandService.findElementsWithWaitingAlwaysAsList(dataExtractor.eachPostXpath)
        for (eachPost in postElements) {
            webDriverCommandService.clickElementWithAction(eachPost)

            val originalWindow = webDriverCommandService.getWebDriver().windowHandle
            val newWindow = webDriverCommandService.getNewWindowThatIsNot(originalWindow)
            webDriverCommandService.switchToNewWindowAndReturnToOriginalWindow(
                newWindow = newWindow,
                originalWindow = originalWindow,
            ) {
                val eachPostUrl = webDriverCommandService.getWebDriver().currentUrl
                if (adoptionSaveManager.isNewPost(Source.JUSEYO, juseyoDataParseService.getIdentifier(eachPostUrl))) {
                    adoptionSaveManager.addAdoptionToQueue(
                        AdoptionToSave(
                            eachPostUrl,
                            { juseyoDataParseService.getMakeAdoptionDto(eachPostUrl) },
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

//                val title = eachPost.findElement(By.xpath(dataExtractor.titleXpath)).text!!
//                val postTypeImageSrc = eachPost.findElement(By.xpath(dataExtractor.postTypeXpath)).getAttribute("src") ?: ""
//                juseyoDataParseService
//                    .processDataAndSave(
//                        MakeAdoptionDto(
//                            originalUrl = webDriverCommandService.getWebDriver().currentUrl,
//                            title = title,
//                            content = dataExtractor.content(html),
//                            thumbnailUrl =
//                                webDriverCommandService
//                                    .findElementWithWaiting(dataExtractor.thumbnailXpath)
//                                    ?.getAttribute("src"),
//                            createdAt =
//                                webDriverCommandService
//                                    .findElementWithWaiting(dataExtractor.createdAtXpath)
//                                    ?.text,
//                            region = dataExtractor.region(html),
//                            species = dataExtractor.species.toString(),
//                            breed = dataExtractor.breed(html),
//                            age = dataExtractor.age(html),
//                            gender = dataExtractor.gender(html),
//                            postType = postTypeImageSrc,
//                            adoptionStatus = postTypeImageSrc,
//                            source = Source.JUSEYO,
//                            identifier = webDriverCommandService.getWebDriver().currentUrl,
//                        ),
//                    )
