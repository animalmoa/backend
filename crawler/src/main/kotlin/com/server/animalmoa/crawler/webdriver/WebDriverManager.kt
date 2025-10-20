package com.server.animalmoa.crawler.webdriver

import com.server.animalmoa.common.adoption.enum.Source
import mu.KotlinLogging
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chromium.ChromiumDriver
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class WebDriverManager(
    private val webDriverFactory: WebDriverFactory,
) {
    private val logger = KotlinLogging.logger { }
    private var driverLocal = ThreadLocal<WebDriver>()

    fun getWebDriver(): WebDriver =
        driverLocal.get()
            ?: throw IllegalStateException("WebDriver 가져오기 실패")

    fun setNewWebDriver(headless: Boolean): WebDriver {
        if (driverLocal.get() == null) {
            val newDriver = webDriverFactory.chromeDriver(headless)
            driverLocal.set(newDriver)
            return newDriver
        } else {
            throw WebDriverException("driver not closed for ${Thread.currentThread().name}")
        }
    }

    fun resetWebDriver(headless: Boolean) {
        logger.info("reset webDriver")
        val driver = driverLocal.get() ?: setNewWebDriver(headless)
        resetBrowserState(
            driver,
            Source.entries.map {
                it.url
            },
        )
    }

    private fun resetBrowserState(
        driver: WebDriver,
        origins: Collection<String>,
    ) {
        val cd = driver as ChromiumDriver

        // 쿠키/캐시 전역 삭제

        cd.executeCdpCommand("Network.enable", emptyMap())
        cd.executeCdpCommand("Network.clearBrowserCache", emptyMap())
        cd.executeCdpCommand("Network.clearBrowserCookies", emptyMap())

        // origin 저장소(LS/SS/IndexedDB/CacheStorage/ServiceWorker 등) 삭제
        for (o in origins) {
            cd.executeCdpCommand(
                "Storage.clearDataForOrigin",
                mapOf("origin" to o, "storageTypes" to "all"),
            )
        }
    }

    fun wait(): WebDriverWait = WebDriverWait(driverLocal.get(), Duration.ofSeconds(10))
}

class WebDriverException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
