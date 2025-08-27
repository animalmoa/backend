package com.server.animalmoa.crawler.webdriver

import io.sentry.Sentry
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class WebDriverManager(
    private val webDriverFactory: WebDriverFactory,
) {
    private var driverLocal = ThreadLocal<WebDriver>()

    fun getWebDriver(): WebDriver =
        driverLocal.get()
            ?: throw IllegalStateException("WebDriver 가져오기 실패")

    private fun setNewWebDriver(headless: Boolean) {
        if (driverLocal.get() == null) {
            driverLocal.set(webDriverFactory.chromeDriver(headless))
        } else {
            Sentry.captureException(WebDriverException("driver not closed for ${Thread.currentThread().name}"))
        }
    }

    private fun removeWebDriver(): Boolean {
        val driver = driverLocal.get() ?: return true
        return try {
            driver.quit()
            true
        } catch (e: Exception) {
            Sentry.captureException(WebDriverException("webDriver quit error: ${e.message}", e))
            false
        } finally {
            // 실제 WebDriver를 종료하지 못하더라도 ThreadLocal에서는 확실히 제거한다.
            driverLocal.remove()
        }
    }

    fun resetWebDriver(headless: Boolean) {
        removeWebDriver()
        setNewWebDriver(headless)
    }

    fun wait(): WebDriverWait = WebDriverWait(driverLocal.get(), Duration.ofSeconds(10))
}

class WebDriverException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
