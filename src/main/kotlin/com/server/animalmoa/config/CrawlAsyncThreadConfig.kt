package com.server.animalmoa.config

import com.server.animalmoa.webdriver.WebDriverManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.ThreadPoolExecutor

@Configuration
@EnableAsync
class CrawlAsyncThreadConfig(
    private val webDriverManager: WebDriverManager,
) {
    // 20250225 java robot을 쓸 필요가 없는 크롤링 사이트들을 위한 쓰레드이다.
    @Bean("headless-webdriver-per-thread")
    fun headLessWebDriverTaskExecutor(): ThreadPoolTaskExecutor =
        ThreadPoolTaskExecutor().apply {
            corePoolSize = 10 // 항상 유지되는 최소 스레드 개수
            maxPoolSize = 20 // 최대 20개까지 확장
            setThreadNamePrefix("headless-crawl-thread")
            setTaskDecorator { runnable ->
                Runnable {
                    /*
                    쓰레드마다 서로 다른 WebDriver를 배정하기 위해 ThreadLocal에 WebDriver를 새로 생성한다.
                     */
                    webDriverManager.setNewWebDriver(headless = true)
                    try {
                        runnable.run()
                    } finally {
                        webDriverManager.removeWebDriver()
                    }
                }
            }
            setRejectedExecutionHandler(ThreadPoolExecutor.AbortPolicy())
            initialize()
        }

    @Bean("gui-webdriver-per-thread")
    fun asyncThreadTaskExecutor(): ThreadPoolTaskExecutor =
        ThreadPoolTaskExecutor().apply {
            // 한 화면은 하나의 WebDriver만이 사용되어야하기떄문에 쓰레드는 한개만 유지되어야한다.
            corePoolSize = 1 // 항상 유지되는 최소 스레드 개수
            maxPoolSize = 1 // 1개까지 확장
            setThreadNamePrefix("un-headless-crawl-thread")
            setTaskDecorator { runnable ->
                Runnable {
                    /*
                    쓰레드마다 서로 다른 WebDriver를 배정하기 위해
                    쓰레드 실행시, WebDriver등록
                    쓰레드 실행 완료시 WebDriver제거
                     */
                    webDriverManager.setNewWebDriver(false)
                    try {
                        runnable.run()
                    } finally {
                        webDriverManager.removeWebDriver()
                    }
                }
            }
            setRejectedExecutionHandler(ThreadPoolExecutor.AbortPolicy())
            initialize()
        }
}
