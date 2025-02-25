package com.server.animalmoa.webdriver

import com.server.animalmoa.oracle.OciObjectStorageService
import org.openqa.selenium.OutputType
import org.openqa.selenium.WebElement
import org.springframework.stereotype.Service

@Service
class ScreenShotCaptureService(
    private val ociObjectStorageService: OciObjectStorageService,
) {
    fun getScreenShot(screenShotElement: WebElement?): String? {
        if (screenShotElement == null) return null
        // 네이버 카페의 알림창 들을 사라지게 하기 위한 최소한의 시간
        Thread.sleep(2000)
        return screenShotElement.getScreenshotAs(OutputType.BYTES).let { bytes ->
            val fileName = "screenshot-${System.currentTimeMillis()}.png"
            val ociUrl = ociObjectStorageService.uploadImageAsByteArray(fileName, bytes)
            ociUrl
        }
    }
}
