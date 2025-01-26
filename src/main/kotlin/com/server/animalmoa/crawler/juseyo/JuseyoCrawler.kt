package com.server.animalmoa.crawler.juseyo

import com.server.animalmoa.adoption.domain.PostType
import com.server.animalmoa.crawler.FreeAdoptionCrawler
import com.server.animalmoa.crawler.LostCrawler
import com.server.animalmoa.crawler.WebDriverService
import org.openqa.selenium.By
import org.springframework.stereotype.Service

@Service
class JuseyoCrawler(
    private val webDriverService: WebDriverService,
    private val juseyoDataService: JuseyoDataService,
) : FreeAdoptionCrawler,
    LostCrawler {
    private val freeCatAdoptionUrl = "https://www.zooseyo.com/sale/sale_list.php?animal=cat"

    override fun crawlFreeAdoption() {
        webDriverService.navigateTo(freeCatAdoptionUrl)
        // 주어진 CSS 선택자를 사용하여 요소들 선택
        // 요소가 존재할 때까지 대기 (tr 요소 중 onclick 속성이 있는 것)
        val eachPostXpath = "//tr[@onclick]"
        val elements = webDriverService.findElementsWithWaiting(eachPostXpath)
        println("찾은 요소 개수: ${elements.size}")
        elements.forEachIndexed { _, element ->
            try {
                val titleXpath = ".//td[4]"
                var contentXpath = "/html/body/table[2]/tbody/tr/td/table[18]/tbody/tr/td[2]/table/tbody/tr/td[2]"
                var thumbnailXpath = "//*[@id='imgg1']/img"
                val title = element.findElement(By.xpath(titleXpath)).text
                element.click()

                val originalWindow = webDriverService.webDriver.windowHandle
                // 새로운 창 핸들 찾기
                val newWindow = webDriverService.getNewWindow(originalWindow)
                if (newWindow != null) {
                    webDriverService.switchTo(newWindow)
                    juseyoDataService.parseData(
                        currentUrl = webDriverService.webDriver.currentUrl,
                        titleText = title,
                        contentText =
                            webDriverService
                                .findElementWithWaiting(contentXpath)
                                ?.text,
                        thumbnailUrlText =
                            webDriverService
                                .findElementWithWaiting(thumbnailXpath)
                                ?.getAttribute("src"),
                        animalTypeText = getDataText(Pair(5, 2)),
                        regionText = getDataText(Pair(7, 2)),
                        ageText = getDataText(Pair(11, 2)),
                        genderText = getDataText(Pair(11, 4)),
                        PostType.FREE_ADOPTION,
                    )
                    webDriverService.close()
                    webDriverService.switchTo(originalWindow)
                }
            } catch (e: Exception) {
                e.printStackTrace()
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
