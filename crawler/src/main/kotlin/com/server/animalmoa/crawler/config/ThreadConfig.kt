package com.server.animalmoa.crawler.config

import com.server.animalmoa.crawler.webdriver.WebDriverManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.scheduling.config.ScheduledTaskRegistrar
import java.util.concurrent.ThreadPoolExecutor

@Configuration
@EnableScheduling
@EnableAsync
class ThreadConfig(
    private val webDriverManager: WebDriverManager,
) : SchedulingConfigurer {
    // 20250225 java robot을 쓸 필요가 없는 크롤링 사이트들을 위한 쓰레드이다.
    @Bean("get-html")
    fun getHtmlThread(): ThreadPoolTaskExecutor =
        ThreadPoolTaskExecutor().apply {
            corePoolSize = 10 // 항상 유지되는 최소 스레드 개수
            maxPoolSize = 20 // 최대 20개까지 확장
            setThreadNamePrefix("get-html")
            setTaskDecorator { runnable ->
                Runnable {
                    try {
                        webDriverManager.resetWebDriver(headless = true)
                        runnable.run()
                    } catch (e: Exception) {
                        throw RunnableThreadException("get-html thread error : ${e.message}", e)
                    }
                }
            }
            setRejectedExecutionHandler(ThreadPoolExecutor.AbortPolicy())
            initialize()
        }

    @Bean
    fun findPostThread(): ThreadPoolTaskScheduler =
        ThreadPoolTaskScheduler().apply {
            poolSize = 1
            setThreadNamePrefix("find-post-")
            initialize()
        }

    override fun configureTasks(registrar: ScheduledTaskRegistrar) {
        registrar.setTaskScheduler(findPostThread())
    }

    class RunnableThreadException(
        message: String,
        cause: Throwable,
    ) : RuntimeException(message, cause)
}
