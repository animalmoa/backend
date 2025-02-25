package com.server.animalmoa.webdriver

import mu.KotlinLogging
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.springframework.stereotype.Service

@Service
class WebDriverCommandService(
    private val webDriverManager: WebDriverManager,
) {
    val logger = KotlinLogging.logger {}

    fun getWebDriver(): WebDriver = webDriverManager.getWebDriver()

    fun navigateTo(url: String) {
        getWebDriver().get(url)
        logger.info("Navigated to URL: ${getWebDriver().currentUrl}") // 현재 URL 출력
    }

    fun goBack() {
        getWebDriver().navigate().back()
    }

    // ElementClickInterruptedException을 방지
    fun clickElementWithAction(webElement: WebElement) {
        val actions = Actions(getWebDriver())
        actions.moveToElement(webElement).click().perform()
    }

    fun findElementWithWaiting(path: String): WebElement? =
        try {
            webDriverManager.wait().until(
                ExpectedConditions.presenceOfElementLocated(
                    By.xpath(path),
                ),
            )
        } catch (e: Exception) {
            null
        }

    fun getText(path: String): String? = findElementWithWaiting(path)?.text

    fun findElementsWithWaitingAlwaysAsList(path: String): List<WebElement> =
        try {
            webDriverManager.wait().until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.xpath(path),
                ),
            )
        } catch (e: Exception) {
            emptyList()
        }

    fun close() {
        getWebDriver().close()
    }

    fun switchTo(originalWindow: String) {
        getWebDriver().switchTo().window(originalWindow)
    }

    fun getNewWindowThatIsNot(originalWindow: String): String? {
        // 새로운 창이 열릴 때까지 대기
        webDriverManager.wait().until { getWebDriver().windowHandles.size > 1 }
        // 열린 창들 중에 원래의 창이 아닌 것을 반환
        return getWebDriver().windowHandles.find { it != originalWindow }
    }

    /*
    주로 팝업 닫기에 사용
     */
    fun closeAllWindowsExcept(window: String) {
        // 현재 열린 모든 창 핸들 가져오기
        val handles = getWebDriver().windowHandles

        // 원본 창을 제외하고 모두 닫기
        handles.forEach { handle ->
            if (handle != window) {
                getWebDriver().switchTo().window(handle)
                getWebDriver().close()
            }
        }
        // 원본 창으로 다시 포커스 이동
        getWebDriver().switchTo().window(window)
    }

    // 새로운 창을 이동후. block()함수를 실행후 닫은 후에 원래 창으로 돌아온다
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
