package com.server.animalmoa.crawler.common.service

import mu.KotlinLogging
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
    val logger = KotlinLogging.logger {}

    fun navigateTo(url: String) {
        webDriver.get(url)
        logger.info("Navigated to URL: ${webDriver.currentUrl}") // 현재 URL 출력
    }

    fun goBack() {
        webDriver.navigate().back()
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

    fun getNewWindowThatIsNot(originalWindow: String): String? {
        // 새로운 창이 열릴 때까지 대기
        wait.until { webDriver.windowHandles.size > 1 }
        // 열린 창들 중에 원래의 창이 아닌 것을 반환
        return webDriver.windowHandles.find { it != originalWindow }
    }

    /*
    주로 팝업 닫기에 사용
     */
    fun closeAllWindowsExcept(window: String) {
        // 현재 열린 모든 창 핸들 가져오기
        val handles = webDriver.windowHandles

        // 원본 창을 제외하고 모두 닫기
        handles.forEach { handle ->
            if (handle != window) {
                webDriver.switchTo().window(handle)
                webDriver.close()
            }
        }
        // 원본 창으로 다시 포커스 이동
        webDriver.switchTo().window(window)
    }

    // 프록시 형태의 함수 정의
    fun switchToNewWindowAndReturnToOriginalWindow(
        newWindow: String?,
        originalWindow: String,
        block: () -> Unit,
    ) {
        if (newWindow != null) {
            try {
                this.switchTo(newWindow)
                block()
            } finally {
                this.close()
                this.switchTo(originalWindow)
            }
        }
    }
}
