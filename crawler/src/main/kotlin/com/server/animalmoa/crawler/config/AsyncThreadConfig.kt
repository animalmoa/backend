package com.server.animalmoa.crawler.config

import com.server.animalmoa.crawler.webdriver.WebDriverManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.ThreadPoolExecutor

@Configuration
@EnableAsync
class AsyncThreadConfig(
    private val webDriverManager: WebDriverManager,
) {
    // 20250225 java robot을 쓸 필요가 없는 크롤링 사이트들을 위한 쓰레드이다.
    @Bean("get-html")
    fun headLessWebDriverTaskExecutor(): ThreadPoolTaskExecutor =
        ThreadPoolTaskExecutor().apply {
            corePoolSize = 10 // 항상 유지되는 최소 스레드 개수
            maxPoolSize = 20 // 최대 20개까지 확장
            setThreadNamePrefix("get-html")
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

    @Bean("find-post")
    fun asyncThreadTaskExecutor(): ThreadPoolTaskExecutor =
        ThreadPoolTaskExecutor().apply {
            // 2025.07.27 현재 새 게시글을 찾는 WebDriver는 한개만 있기에 쓰레드는 한개만 유지되어야한다.
            corePoolSize = 1 // 항상 유지되는 최소 스레드 개수
            maxPoolSize = 1 //
            setThreadNamePrefix("find-post")
            setTaskDecorator { runnable ->
                Runnable {
                    /*
                    쓰레드마다 서로 다른 WebDriver를 배정하기 위해
                    쓰레드 실행시, WebDriver등록
                     */
                    webDriverManager.setNewWebDriver(true)
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
