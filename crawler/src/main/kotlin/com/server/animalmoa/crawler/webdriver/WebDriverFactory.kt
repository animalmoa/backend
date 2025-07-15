package com.server.animalmoa.crawler.webdriver

import org.openqa.selenium.Dimension
import org.openqa.selenium.UnexpectedAlertBehaviour
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class WebDriverFactory(
    @Value("\${webdriver.chrome.path}") private val driverPath: String,
) {
    private val chromeDriverPath = "$driverPath/chromedriver"
    private val geckoDriverPath = "$driverPath/geckodriver"

    private fun chromeOptions(headless: Boolean): ChromeOptions {
        val chromeOption =
            ChromeOptions().apply {
                addArguments("--no-sandbox") // 보안 샌드박스를 끔. Docker에서 sandbox환경이 제한적
                addArguments("--disable-dev-shm-usage") // Docker의 기본 공유 메모리가 작을경우 크롬이 죽는 문제 회피
                addArguments("--disable-gpu")
                    .addArguments("--remote-allow-origins=*") // CORS 우회
                addArguments("--disable-notifications")
//            addArguments("--incognito") // 방문자 모드
                setExperimentalOption("excludeSwitches", listOf("disable-popup-blocking")) // 팝업 차단
                setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.ACCEPT) // 알림(Alert)이 있다면 자동으로 수락
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
    WebDriver마다 브라우저를 공유하기에 ChromeDriver는 싱글톤이어서는 안 된다.
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
