package com.server.animalmoa.crawler.webdriver

import mu.KotlinLogging
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class WebDriverCommandService(
    private val webDriverManager: WebDriverManager,
) {
    val logger = KotlinLogging.logger {}

    private fun getWebDriver(): WebDriver = webDriverManager.getWebDriver()

    fun getHtml(url: String): String {
        navigateTo(url)
        return getWebDriver().pageSource
    }

    // 2025.09.02 Kara같은 경우 이미지 로딩이 매우 긴 경우가 존재. 그럴 경우 페이지 진입 후에 잠시 대기한다.
    fun getHtmlWithSleep(
        url: String,
        second: Int,
    ): String {
        navigateTo(url)
        Thread.sleep(1000 * second.toLong())
        return getWebDriver().pageSource
    }

    fun navigateTo(url: String) {
        val webDriver = getWebDriver()
        webDriver.get(url)
        WebDriverWait(webDriver, Duration.ofSeconds(3))
//       .until {
//            (webDriver as JavascriptExecutor).executeScript("return document.readyState") == "complete"
//        }
        logger.info("Navigated to URL: ${getWebDriver().currentUrl}") // 현재 URL 출력
//        logger.info("current pageSource: ${webDriver.pageSource}")
    }

    fun findElementsWithXpathWaitingAlwaysAsList(path: String): List<WebElement> =
        webDriverManager.wait().until(
            ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.xpath(path),
            ),
        )

    fun findElementWithXpathWaiting(path: String): WebElement? =
        webDriverManager.wait().until(
            ExpectedConditions.presenceOfElementLocated(
                By.xpath(path),
            ),
        )
}
