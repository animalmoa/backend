package com.server.animalmoa.crawler

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

@Configuration
@EnableAsync
class CrawlAsyncThreadConfig {
    @Bean
    fun asyncThreadTaskExecutor(): Executor {
        val threadPoolTaskExecutor = ThreadPoolTaskExecutor()
        threadPoolTaskExecutor.corePoolSize = 8
        threadPoolTaskExecutor.maxPoolSize = 8
        threadPoolTaskExecutor.setThreadNamePrefix("crawl-thread")
        return threadPoolTaskExecutor
    }
}
