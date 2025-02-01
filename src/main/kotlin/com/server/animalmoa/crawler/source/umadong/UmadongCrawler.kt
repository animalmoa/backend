package com.server.animalmoa.crawler.source.umadong

import com.server.animalmoa.adoption.data.MakeAdoptionDto
import com.server.animalmoa.adoption.domain.AdoptionStatus
import com.server.animalmoa.adoption.domain.Source
import com.server.animalmoa.common.PostType
import com.server.animalmoa.crawler.service.AdoptionCrawler
import com.server.animalmoa.crawler.service.JavaRobotService
import com.server.animalmoa.exception.LoginFailException
import com.server.animalmoa.webdriver.WebDriverCommandService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.awt.event.KeyEvent

@Service
class UmadongCrawler(
    private val webDriverCommandService: WebDriverCommandService,
    private val javaRobotService: JavaRobotService,
) : AdoptionCrawler {
    private val logger = KotlinLogging.logger {}

    @Value("\${crawl-until.page}")
    private val maxPage: Int = 10

    @Value("\${naver.id}")
    private val naverId: String = ""

    @Value("\${naver.password}")
    private var naverPassword: String = ""

    override fun crawlAdoption() {
        webDriverCommandService.navigateTo(
            "https://nid.naver.com/nidlogin.login",
        )

        // 로그인 시도 후 실패하면 바로 종료
        runCatching { tryLogin() }
            .onFailure { ex ->
                logger.error(ex) { "Naver login failed" }
                return
            }

        Thread.sleep(2000)
        webDriverCommandService.navigateTo(
            "https://m.cafe.naver.com/ca-fe/web/cafes/24387804/menus/7",
        )
        Thread.sleep(2000)
        val posts = webDriverCommandService.findElementsWithWaitingAlwaysAsList("//*[@id=\"ct\"]/div/div[1]/ul/li/div/a[@class='mainLink']")
        val postUrls = posts.map { it.getAttribute("href") }
        for (url in postUrls) {
            webDriverCommandService.navigateTo(url)
            if (webDriverCommandService.getWebDriver().currentUrl.contains("nid.naver.com")) {
                // 로그인창으로 리다이렉션 됐다면 작업을 취소한다
                throw LoginFailException("Naver login failed")
            }
            val umadongData = UmadongData.cat()
            val makeAdoptionDto =
                MakeAdoptionDto(
                    originalUrl = webDriverCommandService.getWebDriver().currentUrl,
                    species = null,
                    breed = null,
                    region = null,
                    gender = null,
                    title = webDriverCommandService.findElementWithWaiting(umadongData.titleXpath)?.text,
                    content = webDriverCommandService.findElementWithWaiting(umadongData.contentXpath)?.text,
                    age = null,
                    thumbnailUrl =
                        webDriverCommandService
                            .findElementWithWaiting(umadongData.thumbnailXpath)
                            ?.getAttribute("src"),
                    postType = PostType.FREE_ADOPTION.name,
                    adoptionStatus = AdoptionStatus.ING.name,
                    source = Source.UMADONG,
                    identifier = webDriverCommandService.getWebDriver().currentUrl,
                    createdAt = webDriverCommandService.findElementWithWaiting(umadongData.createdAtXpath)?.text,
                )
            println(makeAdoptionDto)
        }
        Thread.sleep(1000000)
    }

    private fun tryLogin(delayedMillis: Long = 1000) {
        val idInput = webDriverCommandService.findElementWithWaiting("//*[@id=\"id\"]")
        val passwordInput = webDriverCommandService.findElementWithWaiting("//*[@id=\"pw\"]")
        val loginButton = webDriverCommandService.findElementWithWaiting("//*[@id=\"log.login\"]")
        if (idInput == null || passwordInput == null || loginButton == null) {
            throw LoginFailException("Naver login failed")
        }
        /*
        충분한 Thread.sleep을 해야 봇으로 감지되지 않음
         */
        Thread.sleep(delayedMillis)
        idInput.click()
        Thread.sleep(delayedMillis)
        javaRobotService.pasteTextIntoField(naverId)
        Thread.sleep(delayedMillis)
        // Password란으로 이동
        javaRobotService.robot.keyPress(KeyEvent.VK_TAB)
        Thread.sleep(delayedMillis)
        javaRobotService.pasteTextIntoField(naverPassword)
        Thread.sleep(delayedMillis)
        javaRobotService.robot.keyPress(KeyEvent.VK_ENTER)
        Thread.sleep(delayedMillis)
    }
}
