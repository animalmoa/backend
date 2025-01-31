package com.server.animalmoa.webdriver

import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

object WebDriverFactory {
    private val chromeDriverPath = "src/main/resources/chromedriver"

    private fun chromeOptions(): ChromeOptions =
        ChromeOptions().apply {
//            addArguments("--headless") // GUI 백그라운드 여부
            addArguments("--disable-gpu")
            addArguments("--remote-allow-origins=*") // CORS 우회
            addArguments("--disable-notifications")
//            addArguments("--incognito") // 방문자 모드
            setExperimentalOption("excludeSwitches", listOf("disable-popup-blocking")) // 팝업 차단
//            addArguments("--disable-popup-blocking") // 팝업 차단 해제
        }

    fun chromeDriver(): ChromeDriver {
        System.setProperty("webdriver.chrome.driver", chromeDriverPath)
        return ChromeDriver(chromeOptions())
    }

    fun webDriverWait(): WebDriverWait = WebDriverWait(chromeDriver(), Duration.ofSeconds(10))
}
