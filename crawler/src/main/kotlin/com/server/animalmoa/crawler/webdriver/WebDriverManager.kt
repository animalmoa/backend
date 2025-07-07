package com.server.animalmoa.crawler.webdriver

import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class WebDriverManager(
    private val webDriverFactory: WebDriverFactory,
) {
    private var threadLocalDriver = ThreadLocal<WebDriver>()

    fun getWebDriver(): WebDriver =
        threadLocalDriver.get()
            ?: throw IllegalStateException("WebDriver 가져오기 실패")

    fun setNewWebDriver(headless: Boolean) {
        threadLocalDriver.set(webDriverFactory.chromeDriver(headless))
    }

    fun removeWebDriver() {
        threadLocalDriver.get()?.quit()
        threadLocalDriver.remove()
    }

    fun wait(): WebDriverWait = WebDriverWait(threadLocalDriver.get(), Duration.ofSeconds(10))
}
