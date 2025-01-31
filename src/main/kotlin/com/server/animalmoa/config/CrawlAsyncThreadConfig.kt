package com.server.animalmoa.config

import com.server.animalmoa.webdriver.WebDriverManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@Configuration
@EnableAsync
class CrawlAsyncThreadConfig(
    private val webDriverManager: WebDriverManager,
) {
    @Bean("webdriver-per-thread")
    fun asyncThreadTaskExecutor(): ThreadPoolTaskExecutor =
        ThreadPoolTaskExecutor().apply {
            corePoolSize = 8
            maxPoolSize = 8
            setThreadNamePrefix("crawl-thread")
            setTaskDecorator { runnable ->
                Runnable {
                    /*
                    쓰레드마다 서로 다른 WebDriver를 배정하기 위해
                    쓰레드 실행시, WebDriver등록
                    쓰레드 실행 완료시 WebDriver제거
                     */
                    webDriverManager.setNewWebDriver()
                    try {
                        runnable.run()
                    } finally {
                        webDriverManager.removeWebDriver()
                    }
                }
            }
            initialize()
        }
}
