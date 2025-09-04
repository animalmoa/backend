package com.server.animalmoa.crawler.webdriver

import com.server.animalmoa.crawler.exception.EmptyHtmlException
import mu.KotlinLogging
import org.jsoup.Jsoup
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
        val html = getWebDriver().pageSource

        val document = Jsoup.parse(html)
        val bodyHtmlText = document.body().text()
        if (bodyHtmlText.isEmpty()) {
            throw EmptyHtmlException(url)
        }
        return html
    }

    fun getHtmlWithWaitingElement(
        url: String,
        xPath: String,
    ): String {
        navigateTo(url)
        WebDriverWait(getWebDriver(), Duration.ofSeconds(10))
            .until(ExpectedConditions.presenceOfElementLocated(By.xpath(xPath)))
        val html = getWebDriver().pageSource

        val document = Jsoup.parse(html)
        val bodyHtmlText = document.body().text()
        if (bodyHtmlText.isEmpty()) {
            throw EmptyHtmlException(url)
        }
        return html
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
