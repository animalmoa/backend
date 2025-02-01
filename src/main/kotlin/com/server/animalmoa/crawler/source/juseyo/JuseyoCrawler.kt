package com.server.animalmoa.crawler.source.juseyo

import com.server.animalmoa.adoption.data.MakeAdoptionDto
import com.server.animalmoa.adoption.domain.Source
import com.server.animalmoa.crawler.service.AdoptionCrawler
import com.server.animalmoa.crawler.service.LostCrawler
import com.server.animalmoa.exception.DataParseException
import com.server.animalmoa.webdriver.WebDriverCommandService
import mu.KotlinLogging
import org.openqa.selenium.By
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

/**
 * TODO 유료 동물 분양 페이지, 분실
 */
@Service
class JuseyoCrawler(
    private val webDriverCommandService: WebDriverCommandService,
    private val juseyoDataManageService: JuseyoDataManageService,
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

    private fun searchEachPage(xpathes: JuseyoData) {
        val elements = webDriverCommandService.findElementsWithWaitingAlwaysAsList(xpathes.eachPostXpath)
        for (element in elements) {
            /*
            아래 부분은 고양이, 개가 동일
             */
            val title = element.findElement(By.xpath(xpathes.essential.titleXpath)).text ?: ""
            val postTypeImageSrc = element.findElement(By.xpath(xpathes.postTypeXpath)).getAttribute("src") ?: ""
            // TODO Try, Catch를 분리하여 재사용 가능하도록 수정
            try {
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
            } catch (e: DataParseException) {
                logger.error { e.printStackTrace() }
            } catch (e: Exception) {
                // IdentifierNotFoundException을 포함함
                logger.error { e.printStackTrace() }
            }
        }
    }

    override fun crawlLost() {
        TODO("Not yet implemented")
    }
}
