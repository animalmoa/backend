package com.server.animalmoa.crawler.webdriver

import org.openqa.selenium.Dimension
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions

object WebDriverFactory {
    // driver에 chmod +x driver처리 필요
    private val chromeDriverPath = "crawler/src/main/resources/chromedriver"
    private val geckoDriverPath = "crawler/src/main/resources/geckodriver"

    private fun chromeOptions(headless: Boolean): ChromeOptions {
        val chromeOption =
            ChromeOptions().apply {
                addArguments("--disable-gpu")
                addArguments("--remote-allow-origins=*") // CORS 우회
                addArguments("--disable-notifications")
//            addArguments("--incognito") // 방문자 모드
                setExperimentalOption("excludeSwitches", listOf("disable-popup-blocking")) // 팝업 차단
//            addArguments("--disable-popup-blpetocking") // 팝업 차단 해제
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

    fun firefoxDriver(headless: Boolean): FirefoxDriver {
        // GeckoDriver 경로 설정
        System.setProperty("webdriver.gecko.driver", geckoDriverPath)
        val driver = FirefoxDriver(firefoxOptions(headless))
        // MoveTargetOutOfBoundsException을 방지하기 위함
        driver.manage().window().size = Dimension(1920, 1080)
        return driver
    }

    /*
    FireFoxDriver
     */
    private fun firefoxOptions(headless: Boolean): FirefoxOptions {
        val firefoxOptions = FirefoxOptions()
        if (headless) {
            // headless 모드 설정
            firefoxOptions.addArguments("-headless")
        }
        return firefoxOptions
    }
}
