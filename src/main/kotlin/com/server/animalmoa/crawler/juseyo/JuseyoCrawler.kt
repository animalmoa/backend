package com.server.animalmoa.crawler.juseyo

import com.server.animalmoa.adoption.data.MakeAdoptionDto
import com.server.animalmoa.adoption.domain.PostType
import com.server.animalmoa.adoption.domain.Source
import com.server.animalmoa.crawler.FreeAdoptionCrawler
import com.server.animalmoa.crawler.LostCrawler
import com.server.animalmoa.crawler.WebDriverService
import com.server.animalmoa.seq.PostSeq
import org.openqa.selenium.By
import org.springframework.stereotype.Service

@Service
class JuseyoCrawler(
    private val webDriverService: WebDriverService,
    private val juseyoAdoptionService: JuseyoAdoptionService,
) : FreeAdoptionCrawler,
    LostCrawler {
    private val freeCatAdoptionUrl = "https://www.zooseyo.com/sale/sale_list.php?animal=cat"

    /*
    TODO 페이지를 넘겨가며 크롤링하도록 수정
     */
    override fun crawlFreeAdoption() {
        webDriverService.navigateTo(freeCatAdoptionUrl)
        // 주어진 CSS 선택자를 사용하여 요소들 선택
        // 요소가 존재할 때까지 대기 (tr 요소 중 onclick 속성이 있는 것)
        val eachPostXpath = "//tr[@onclick]"
        val elements = webDriverService.findElementsWithWaiting(eachPostXpath)
        println("찾은 요소 개수: ${elements.size}")
        var lastPostSequence: PostSeq? = null
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
                    val newPostSequence =
                        juseyoAdoptionService.saveAdoptionIfNotCrawled(
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
                                species = getDataText(Pair(5, 2)),
                                breed = getDataText(Pair(5, 2)),
                                age = getDataText(Pair(11, 2)),
                                gender = getDataText(Pair(11, 4)),
                                postType = PostType.FREE_ADOPTION,
                                source = Source.JUSEYO,
                            ),
                        ) ?: return
                    webDriverService.close()
                    webDriverService.switchTo(originalWindow)

                    lastPostSequence = lastPostSequence?.let {
                        // null이 아니라면 대소 비교 후, 더 큰 값을 가진 새로운 객체를 반환
                        if (it.sequence.toInt() >= newPostSequence.sequence.toInt()) {
                            it
                        } else {
                            null
                        }
                    } ?: newPostSequence
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 모든 게시글을 돈 후에 업데이트
        lastPostSequence?.let {
            juseyoAdoptionService.updateSequence(it)
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
