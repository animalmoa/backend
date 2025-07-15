package com.server.animalmoa.crawler.scraper.source.umadong

import com.server.animalmoa.common.adoption.enum.AdoptionStatus
import com.server.animalmoa.common.adoption.enum.Source
import com.server.animalmoa.common.common.PostType
import com.server.animalmoa.common.dto.MakeAdoptionDto
import com.server.animalmoa.crawler.exception.LoginFailException
import com.server.animalmoa.crawler.scraper.service.AdoptionScraper
import com.server.animalmoa.crawler.scraper.service.ScraperErrorService
import com.server.animalmoa.crawler.webdriver.JavaRobotService
import com.server.animalmoa.crawler.webdriver.ScreenShotCaptureService
import com.server.animalmoa.crawler.webdriver.WebDriverCommandService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.awt.event.KeyEvent

@Service
class UmadongScraper(
    private val webDriverCommandService: WebDriverCommandService,
    private val javaRobotService: JavaRobotService,
    private val umadongDataManageService: UmadongDataManageService,
    private val screenShotCaptureService: ScreenShotCaptureService,
    private val scraperErrorService: ScraperErrorService,
) : AdoptionScraper {
    private val logger = KotlinLogging.logger {}

    @Value("\${scrap-until.page}")
    private val maxPage: Int = 10

    @Value("\${naver.id}")
    private val naverId: String = ""

    @Value("\${naver.password}")
    private var naverPassword: String = ""

    /*
    게시글마다 에러 핸들링 필요
     */
    override fun scrapAdoptionPost() {
        webDriverCommandService.navigateTo(
            "https://nid.naver.com/nidlogin.login",
        )

        // 로그인 시도 후 실패하면 바로 종료
        runCatching { tryLogin() }
            .onFailure { ex ->
                logger.error(ex) { "Naver login failed" }
                return
            }
        val params =
            listOf(
                UmadongData.cat(),
                UmadongData.dog(),
            )
        for (param in params) {
            Thread.sleep(2000)
            webDriverCommandService.navigateTo(
                param.url,
            )
            Thread.sleep(2000)
            val posts = webDriverCommandService.findElementsWithWaitingAlwaysAsList(param.postsXpath)
            val postUrls = posts.map { it.getAttribute("href") }
            for (postUrl in postUrls) {
                scraperErrorService.catchScrawlEachPostError(
                    {
                        webDriverCommandService.navigateTo(postUrl)
                        Thread.sleep(2000)
                        if (webDriverCommandService.getWebDriver().currentUrl.contains("nid.naver.com")) {
                            // 로그인창으로 리다이렉션 됐다면 작업을 취소한다
                            throw LoginFailException("Naver login failed")
                        }

                        val screenshotElement = webDriverCommandService.findElementWithWaiting(param.thumbnailXpath)
                        val thumbnailUrl = screenShotCaptureService.getScreenShot(screenshotElement)

                        umadongDataManageService.processDataAndSave(
                            MakeAdoptionDto(
                                originalUrl = postUrl,
                                species = param.species,
                                breed = null,
                                region = null,
                                gender = null,
                                title = webDriverCommandService.findElementWithWaiting(param.titleXpath)?.text,
                                content = webDriverCommandService.findElementWithWaiting(param.contentXpath)?.text,
                                age = null,
                                thumbnailUrl = thumbnailUrl,
                                postType = PostType.FREE_ADOPTION,
                                adoptionStatus = AdoptionStatus.ING,
                                source = Source.UMADONG,
                                identifier = postUrl,
                                createdAt = webDriverCommandService.findElementWithWaiting(param.createdAtXpath)?.text,
                            ),
                        )
                    },
                    logger,
                )
            }
        }
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
