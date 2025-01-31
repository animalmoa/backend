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
import org.springframework.scheduling.annotation.Async
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

    @Value("\${crawl-until.juseyo}")
    private val maxPage: Int = 10

    @Async("webdriver-per-thread")
    override fun crawlAdoption() {
        val params =
            listOf(
                JuseyoPath.cat(),
                JuseyoPath.dog(),
            )
        for (param in params) {
            for (page in 1..maxPage) {
                val freeAdoptionUrl =
                    "https://www.zooseyo.com/sale/sale_list.php" +
                        "?animal=${param.animalParam}&page=$page&category=${param.categoryParam}&kind=&area=&categoryetc="
                webDriverCommandService.navigateTo(freeAdoptionUrl)
                searchEachPage(param)
            }
        }
    }

    private fun searchEachPage(xpathes: JuseyoPath) {
        // 주어진 CSS 선택자를 사용하여 요소들 선택
        // 요소가 존재할 때까지 대기 (tr 요소 중 onclick 속성이 있는 것)

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
                                region = getDataText(xpathes.essential.regionXpath),
                                species = getDataText(xpathes.essential.speciesXpath),
                                breed = getDataText(xpathes.essential.breedXpath),
                                age = getDataText(xpathes.essential.ageXpath),
                                gender = getDataText(xpathes.essential.genderXpath),
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

    fun getDataText(xpath: String): String? = webDriverCommandService.findElementWithWaiting(xpath)?.text

    override fun crawlLost() {
        TODO("Not yet implemented")
    }
}
