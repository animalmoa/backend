package com.server.animalmoa.api.webdriver

import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions

object WebDriverFactory {
    private val chromeDriverPath = "src/main/resources/chromedriver"

    private fun chromeOptions(headless: Boolean): ChromeOptions {
        val chromeOption =
            ChromeOptions().apply {
                addArguments("--disable-gpu")
                addArguments("--remote-allow-origins=*") // CORS 우회
                addArguments("--disable-notifications")
//            addArguments("--incognito") // 방문자 모드
                setExperimentalOption("excludeSwitches", listOf("disable-popup-blocking")) // 팝업 차단
//            addArguments("--disable-popup-blocking") // 팝업 차단 해제
            }
        if (headless) {
            chromeOption.apply {
                addArguments("--headless")
            }
        }
        return chromeOption
    }

    /*
    20250225
    WebDriver마다 브라우저를 공유하기에 ChoromDriver는 싱글톤이어서는 안 된다.
     */
    fun chromeDriver(headless: Boolean): ChromeDriver {
        System.setProperty("webdriver.chrome.driver", chromeDriverPath)
        return ChromeDriver(chromeOptions(headless))
    }
}
