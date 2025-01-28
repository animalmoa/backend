package com.server.animalmoa.crawler.juseyo

import com.server.animalmoa.adoption.data.MakeAdoptionDto
import com.server.animalmoa.adoption.domain.Adoption
import com.server.animalmoa.adoption.domain.PostType
import com.server.animalmoa.adoption.domain.Source
import com.server.animalmoa.adoption.domain.Species
import com.server.animalmoa.adoption.service.AdoptionRepositoryService
import com.server.animalmoa.crawler.FreeAdoptionCrawler
import com.server.animalmoa.crawler.LostCrawler
import com.server.animalmoa.crawler.WebDriverService
import com.server.animalmoa.exception.ConflictDataCrawledException
import com.server.animalmoa.exception.DataTextParseException
import mu.KotlinLogging
import org.openqa.selenium.By
import org.springframework.stereotype.Service

@Service
class JuseyoCrawler(
    private val webDriverService: WebDriverService,
    private val juseyoDataManageService: JuseyoDataManageService,
    private val adoptionRepositoryService: AdoptionRepositoryService,
) : FreeAdoptionCrawler,
    LostCrawler {
    val logger = KotlinLogging.logger {}

    // TODO 강아지 페이지 크롤링 추가
    override fun crawlFreeAdoption() {
        val latestCatAdoption =
            adoptionRepositoryService.findLatestAdoption(
                Source.JUSEYO.name,
                Species.CAT.name,
            )
        val catCategory = "%B0%ED%BE%E7%C0%CC"
        val dogCategory = "%B0%AD%BE%C6%C1%F6"
        for (page in 1..10) {
            val freeCatAdoptionUrl =
                "https://www.zooseyo.com/sale/sale_list.php" +
                    "?animal=cat&page=$page&category=$catCategory&kind=&area=&categoryetc="
            webDriverService.navigateTo(freeCatAdoptionUrl)
            try {
                searchEachPage(latestCatAdoption)
            } catch (e: ConflictDataCrawledException) {
                break
            }
        }
    }

    private fun searchEachPage(latestAdoption: Adoption?) {
        // 주어진 CSS 선택자를 사용하여 요소들 선택
        // 요소가 존재할 때까지 대기 (tr 요소 중 onclick 속성이 있는 것)
        val eachPostXpath = "//tr[@onclick]"
        val elements = webDriverService.findElementsWithWaiting(eachPostXpath)
        for (element in elements) {
            val titleXpath = ".//td[4]"
            val contentXpath = "/html/body/table[2]/tbody/tr/td/table[18]/tbody/tr/td[2]/table/tbody/tr/td[2]"
            val createdAtXpath = "/html/body/table[1]/tbody/tr/td[2]/table/tbody/tr/td"
            val thumbnailXpath = "//*[@id='imgg1']/img"
            val title = element.findElement(By.xpath(titleXpath)).text ?: ""

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
                        .checkDataIsNewAndParse(
                            MakeAdoptionDto(
                                region = getDataText(Pair(7, 2)),
                                originalUrl = webDriverService.webDriver.currentUrl,
                                title = title,
                                content =
                                    webDriverService
                                        .findElementWithWaiting(contentXpath)
                                        ?.text,
                                thumbnailUrl =
                                    webDriverService
                                        .findElementWithWaiting(thumbnailXpath)
                                        ?.getAttribute("src"),
                                createdAt =
                                    webDriverService
                                        .findElementWithWaiting(createdAtXpath)
                                        ?.text,
                                species = getDataText(Pair(5, 2)),
                                breed = getDataText(Pair(5, 2)),
                                age = getDataText(Pair(11, 2)),
                                gender = getDataText(Pair(11, 4)),
                                postType = PostType.FREE_ADOPTION,
                                source = Source.JUSEYO,
                            ),
                            latestAdoption,
                        )?.let {
                            adoptionRepositoryService.save(it)
                        } ?: throw ConflictDataCrawledException()
                }
            } catch (e: ConflictDataCrawledException) {
                // 이미 수집한 데이터를 또 수집했을 땐, 그만두고
                logger.error { e.printStackTrace() }
                throw e
            } catch (e: DataTextParseException) {
                logger.error { e.printStackTrace() }
            } catch (e: Exception) {
                logger.error { e.printStackTrace() }
            }
        }
    }

    fun getDataText(pair: Pair<Int, Int>): String? {
        val xpath = "/html/body/table[2]/tbody/tr/td/table[${pair.first}]/tbody/tr/td[${pair.second}]/p_style_subma"
        return webDriverService.findElementWithWaiting(xpath)?.text
    }

    override fun crawlLost() {
        TODO("Not yet implemented")
    }
}
