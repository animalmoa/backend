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

    fun getWebDriver(): WebDriver = webDriverManager.getWebDriver()

    fun getHtml(url: String): String {
        navigateTo(url)
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
