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
        /*
    TODO 페이지를 넘겨가며 크롤링하도록 수정
         */
    override fun crawlFreeAdoption() {
        for (page in 1..10) {
            val freeCatAdoptionUrl =
                "https://www.zooseyo.com/sale/sale_list.php" +
                    "?animal=cat&page=$page&category=%B0%ED%BE%E7%C0%CC&kind=&area=&categoryetc="
            webDriverService.navigateTo(freeCatAdoptionUrl)

            val lastPostSequence = searchEachPage()
            println(lastPostSequence)
            lastPostSequence?.let {
                juseyoAdoptionService.updateSequence(it)
            }
        }
    }

    private fun searchEachPage(): PostSeq? {
        var lastPostSequenceOfPage: PostSeq? = null
        // 주어진 CSS 선택자를 사용하여 요소들 선택
        // 요소가 존재할 때까지 대기 (tr 요소 중 onclick 속성이 있는 것)
        val eachPostXpath = "//tr[@onclick]"
        val elements = webDriverService.findElementsWithWaiting(eachPostXpath)
        elements.forEachIndexed { _, element ->
            try {
                val titleXpath = ".//td[4]"
                val contentXpath = "/html/body/table[2]/tbody/tr/td/table[18]/tbody/tr/td[2]/table/tbody/tr/td[2]"
                val createdAtXpath = "/html/body/table[1]/tbody/tr/td[2]/table/tbody/tr/td"
                val thumbnailXpath = "//*[@id='imgg1']/img"
                val title = element.findElement(By.xpath(titleXpath)).text ?: ""
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
                            // 중복된 데이터일시 null 반환
                        ) ?: return null
                    webDriverService.close()
                    webDriverService.switchTo(originalWindow)

                    lastPostSequenceOfPage = lastPostSequenceOfPage?.let {
                        // null이 아니라면 대소 비교 후, 더 큰 시퀀스 값을 가진 새로운 객체를 반환
                        if (it.sequence.toInt() >= newPostSequence.sequence.toInt()) {
                            it
                        } else {
                            // 이론상 언제나 if문의 조건은 참이 됨
                            // 하지만 거짓이 된다면 새롭게 탐색한 Sequence로 대체
                            null
                        }
                    } ?: newPostSequence
                    println(lastPostSequenceOfPage)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return lastPostSequenceOfPage
    }

    fun getDataText(pair: Pair<Int, Int>): String? {
        val xpath = "/html/body/table[2]/tbody/tr/td/table[${pair.first}]/tbody/tr/td[${pair.second}]/p_style_subma"
        return webDriverService.findElementWithWaiting(xpath)?.text
    }

    override fun crawlLost() {
        TODO("Not yet implemented")
    }
}
