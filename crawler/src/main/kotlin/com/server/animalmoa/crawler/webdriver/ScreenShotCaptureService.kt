package com.server.animalmoa.crawler.webdriver

import com.server.animalmoa.crawler.oracle.OciObjectStorageService
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
        val fileName = "screenshot-${LocalDateTime.now()}.png"
        val screenShot =
            screenShotElement.getScreenshotAs(OutputType.BYTES)
        return ociObjectStorageService.uploadImageAsByteArray(fileName, screenShot)
    }

    // FireFox사용시 FullScreenShotCapture방법
//                val webDriver = webDriverManager.getWebDriver()
//        val screenShot =
//            (webDriver as FirefoxDriver).getFullPageScreenshotAs(OutputType.BYTES)
//
//    ashot 사용시 Bytes로 바꾸는 법
//    fun changeToBytes(screenShot: Screenshot): ByteArray {
//        val baos = ByteArrayOutputStream()
//        ImageIO.write(screenShot.image, "png", baos)
//        return baos.toByteArray()
//    }
}
