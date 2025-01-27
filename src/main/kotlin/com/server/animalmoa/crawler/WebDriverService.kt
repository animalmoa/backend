package com.server.animalmoa.crawler

import okhttp3.internal.wait
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.stereotype.Service

@Service
class WebDriverService(
    val webDriver: ChromeDriver,
    private val wait: WebDriverWait,
) {
    fun navigateTo(url: String) {
        webDriver.get(url)
        println("Navigated to URL: ${webDriver.currentUrl}") // 현재 URL 출력
    }

    fun findElementWithWaiting(path: String): WebElement? =
        try {
            wait.until(
                ExpectedConditions.presenceOfElementLocated(
                    By.xpath(path),
                ),
            )
        } catch (e: Exception) {
            null
        }

    fun findElementsWithWaiting(path: String): List<WebElement> =
        try {
            wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.xpath(path),
                ),
            )
        } catch (e: Exception) {
            emptyList()
        }

    fun close() {
        webDriver.close()
    }

    fun switchTo(originalWindow: String) {
        webDriver.switchTo().window(originalWindow)
    }

    fun getNewWindow(originalWindow: String): String? {
        // 새로운 창이 열릴 때까지 대기
        wait.until { webDriver.windowHandles.size > 1 }
        // 열린 창들 중에 원래의 창이 아닌 것을 반환
        return webDriver.windowHandles.find { it != originalWindow }
    }
}
