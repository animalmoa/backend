package com.server.animalmoa.crawler.webdriver

import com.server.animalmoa.api.oracle.OciObjectStorageService
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.OutputType
import org.openqa.selenium.WebElement
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ScreenShotCaptureService(
    private val ociObjectStorageService: OciObjectStorageService,
    private val webDriverManager: WebDriverManager,
) {
    fun getScreenShot(screenShotElement: WebElement?): String? {
        if (screenShotElement == null) return null
        // 요소를 화면 상단에 위치시키도록 스크롤
        (webDriverManager.getWebDriver() as JavascriptExecutor).executeScript("arguments[0].scrollIntoView(true);", screenShotElement)
        // 스크롤 완료 후 안정화를 위해 잠시 대기
        Thread.sleep(500)
        return screenShotElement.getScreenshotAs(OutputType.BYTES).let { bytes ->
            val fileName = "screenshot-${LocalDateTime.now()}.png"
            val ociUrl = ociObjectStorageService.uploadImageAsByteArray(fileName, bytes)
            ociUrl
        }
    }
}
