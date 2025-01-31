package com.server.animalmoa.webdriver

import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

/*
TODO WebDriver는 싱글톤으로 등록시키면 안 될 가능성 존재
 */
@Configuration
class WebDriverConfig {
    private val chromeDriverPath = "src/main/resources/chromedriver"

    @Bean
    fun chromeOptions(): ChromeOptions =
        ChromeOptions().apply {
//            addArguments("--headless") // GUI 백그라운드 여부
            addArguments("--disable-gpu")
            addArguments("--remote-allow-origins=*") // CORS 우회
            addArguments("--disable-notifications")
//            addArguments("--incognito") // 방문자 모드
            setExperimentalOption("excludeSwitches", listOf("disable-popup-blocking")) // 팝업 차단
//            addArguments("--disable-popup-blocking") // 팝업 차단 해제
        }

    @Bean
    fun chromeDriver(chromeOptions: ChromeOptions): ChromeDriver {
        System.setProperty("webdriver.chrome.driver", chromeDriverPath)
        return ChromeDriver(chromeOptions)
    }

    @Bean
    fun webDriverWait(): WebDriverWait = WebDriverWait(chromeDriver(chromeOptions()), Duration.ofSeconds(10))
}
