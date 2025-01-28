package com.server.animalmoa.crawler.juseyo

import com.server.animalmoa.adoption.data.MakeAdoptionDto
import com.server.animalmoa.adoption.domain.AdoptionStatus
import com.server.animalmoa.adoption.domain.Source
import com.server.animalmoa.common.PostType
import com.server.animalmoa.crawler.FreeAdoptionCrawler
import com.server.animalmoa.crawler.LostCrawler
import com.server.animalmoa.crawler.WebDriverService
import com.server.animalmoa.exception.DataParseException
import mu.KotlinLogging
import org.openqa.selenium.By
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class JuseyoCrawler(
    private val webDriverService: WebDriverService,
    private val juseyoDataManageService: JuseyoDataManageService,
) : FreeAdoptionCrawler,
    LostCrawler {
    val logger = KotlinLogging.logger {}

    @Value("\${crawl-until.juseyo}")
    private val maxPage: Int = 10

    override fun crawlFreeAdoption() {
        val params =
            listOf(
                JuseyoXpath.cat(),
                JuseyoXpath.dog(),
            )
        for (param in params) {
            for (page in 1..maxPage) {
                val freeAdoptionUrl =
                    "https://www.zooseyo.com/sale/sale_list.php" +
                        "?animal=${param.animalParam}&page=$page&category=${param.categoryParam}&kind=&area=&categoryetc="
                webDriverService.navigateTo(freeAdoptionUrl)
                searchEachPage(param)
            }
        }
    }

    private fun searchEachPage(xpathes: JuseyoXpath) {
        // 주어진 CSS 선택자를 사용하여 요소들 선택
        // 요소가 존재할 때까지 대기 (tr 요소 중 onclick 속성이 있는 것)

        val elements = webDriverService.findElementsWithWaiting(xpathes.eachPostXpath)
        for (element in elements) {
            /*
            아래 부분은 고양이, 개가 동일
             */
            val title = element.findElement(By.xpath(xpathes.titleXpath)).text ?: ""

            // TODO Try, Catch를 분리하여 재사용 가능하도록 수정
            try {
                element.click()
                val originalWindow = webDriverService.webDriver.windowHandle
                val newWindow = webDriverService.getNewWindowThatIsNot(originalWindow)
                webDriverService.openNewWindowAndReturnToOriginalWindow(
                    newWindow = newWindow,
                    originalWindow = originalWindow,
                ) {
                    juseyoDataManageService
                        .parseDataAndSave(
                            MakeAdoptionDto(
                                originalUrl = webDriverService.webDriver.currentUrl,
                                title = title,
                                content =
                                    webDriverService
                                        .findElementWithWaiting(xpathes.contentXPath)
                                        ?.text,
                                thumbnailUrl =
                                    webDriverService
                                        .findElementWithWaiting(xpathes.thumbnailXpath)
                                        ?.getAttribute("src"),
                                createdAt =
                                    webDriverService
                                        .findElementWithWaiting(xpathes.createdAtXpath)
                                        ?.text,
                                region = getDataText(xpathes.regionXPath),
                                species = getDataText(xpathes.speciesXpath),
                                breed = getDataText(xpathes.breedXPath),
                                age = getDataText(xpathes.ageXPath),
                                gender = getDataText(xpathes.genderXPath),
                                postType = PostType.FREE_ADOPTION,
                                adoptionStatus = AdoptionStatus.ING,
                                source = Source.JUSEYO,
                                identifier = webDriverService.webDriver.currentUrl,
                            ),
                        )
                }
            } catch (e: DataParseException) {
                logger.error { e.printStackTrace() }
            } catch (e: Exception) {
                logger.error { e.printStackTrace() }
            }
        }
    }

    fun getDataText(xpath: String): String? = webDriverService.findElementWithWaiting(xpath)?.text

    override fun crawlLost() {
        TODO("Not yet implemented")
    }
}
