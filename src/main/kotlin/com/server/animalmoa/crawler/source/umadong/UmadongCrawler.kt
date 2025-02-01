package com.server.animalmoa.crawler.source.umadong

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

        webDriverCommandService.navigateTo("https://cafe.naver.com/6655happyclub")
        webDriverCommandService.navigateTo(
            "https://cafe.naver.com/6655happyclub?iframe_url=/ArticleList.nhn%3Fsearch.clubid=24387804%26search.menuid=7%26search.boardtype=L",
        )
        val posts = webDriverCommandService.findElementsWithWaitingAlwaysAsList("//*[@id=\"main-area\"]/div[4]/table/tbody/tr")
        for (post in posts) {
            webDriverCommandService.clickElementWithAction(post)
            webDriverCommandService.goBack()
        }

        Thread.sleep(33000)
    }

    private fun tryLogin() {
        val idInput = webDriverCommandService.findElementWithWaiting("//*[@id=\"id\"]")
        val passwordInput = webDriverCommandService.findElementWithWaiting("//*[@id=\"pw\"]")
        val loginButton = webDriverCommandService.findElementWithWaiting("//*[@id=\"log.login\"]")
        if (idInput == null || passwordInput == null || loginButton == null) {
            throw LoginFailException("Naver login failed")
        }
        /*
        충분한 Thread.sleep을 해야 봇으로 감지되지 않음
         */
        Thread.sleep(2000)
        idInput.click()
        Thread.sleep(2000)
        javaRobotService.pasteTextIntoField(naverId)
        Thread.sleep(2000)

        // Password란으로 이동
        javaRobotService.robot.keyPress(KeyEvent.VK_TAB)
        Thread.sleep(2000)
        javaRobotService.pasteTextIntoField(naverPassword)
        Thread.sleep(2000)
        javaRobotService.robot.keyPress(KeyEvent.VK_ENTER)
        Thread.sleep(2000)
    }
}
