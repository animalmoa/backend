package com.server.animalmoa.crawler.juseyo

import com.server.animalmoa.adoption.domain.Source
import com.server.animalmoa.crawler.FreeAdoptionCrawler
import com.server.animalmoa.crawler.LostCrawler
import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.stereotype.Service

@Service
class JuseyoCrawler(
    private val webDriver: ChromeDriver,
    private val wait: WebDriverWait,
    private val juseyoDataParser: JuseyoDataParser,
) : FreeAdoptionCrawler,
    LostCrawler {
    private val source: Source = Source.JUSEYO
    private val freeCatAdoptionUrl = "https://www.zooseyo.com/sale/sale_list.php?animal=cat"

    override fun crawlFreeAdoption() {
        webDriver.get(freeCatAdoptionUrl)
        // 주어진 CSS 선택자를 사용하여 요소들 선택
        // 요소가 존재할 때까지 대기 (tr 요소 중 onclick 속성이 있는 것)
        wait.until(
            ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.xpath(
                    "//tr[@onclick]",
                ),
            ),
        )
        val elements =
            webDriver.findElements(
                By.xpath(
                    "//tr[@onclick]",
                ),
            )

        println("찾은 요소 개수: ${elements.size}")

        elements.forEachIndexed { _, element ->
            try {
                element.click()
                val originalWindow = webDriver.windowHandle
                // 새로운 창이 열릴 때까지 대기
                wait.until { webDriver.windowHandles.size > 1 }

                // 모든 창 핸들 가져오기
                val windowHandles = webDriver.windowHandles
                // 새로운 창 핸들 찾기
                val newWindow = windowHandles.find { it != originalWindow }

                if (newWindow != null) {
                    webDriver.switchTo().window(newWindow)

                    juseyoDataParser.parseData(
                        currentUrl = webDriver.currentUrl,
                        thumbnailUrlText =
                            webDriver
                                .findElement(By.xpath("//*[@id='imgg1']/img"))
                                // *[@id="imgg1"]/img
                                .getAttribute("src"),
                        animalTypeText = getText(Pair(5, 2)),
                        regionText = getText(Pair(7, 2)),
                        ageText = getText(Pair(11, 2)),
                        genderText = getText(Pair(11, 4)),
                    )

                    webDriver.close()
                    webDriver.switchTo().window(originalWindow)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getText(pair: Pair<Int, Int>): String {
        val xpath = "/html/body/table[2]/tbody/tr/td/table[${pair.first}]/tbody/tr/td[${pair.second}]/p_style_subma"
        return webDriver.findElement(By.xpath(xpath)).text
    }

    override fun crawlLost() {
        TODO("Not yet implemented")
    }
}
