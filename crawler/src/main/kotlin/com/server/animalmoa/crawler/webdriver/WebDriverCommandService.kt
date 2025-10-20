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
        waitSecond: Int = 2,
    ): String {
        navigateTo(url, waitSecond)

        var html = getWebDriver().pageSource

        // TODO Html 비어있는지 확인은 별개의 메소드에서 수행하여야한다. 현재 GetHtmlWithWaitingElement와 어울리지 않다
        val document = Jsoup.parse(html)
        val bodyHtmlText = document.body().text()
        if (bodyHtmlText.isEmpty()) {
            throw EmptyHtmlException(url)
        }

        WebDriverWait(getWebDriver(), Duration.ofSeconds(10))
            .until(ExpectedConditions.presenceOfElementLocated(By.xpath(xPath)))

        html = getWebDriver().pageSource

        return html
    }

    fun navigateTo(
        url: String,
        waitSecond: Int = 0,
    ) {
        val webDriver = getWebDriver()
        webDriver.get(url)

        Thread.sleep((waitSecond * 1000).toLong())

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
