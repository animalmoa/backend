package com.server.animalmoa.crawler.config

import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.scheduling.config.ScheduledTaskRegistrar
import java.util.concurrent.ThreadPoolExecutor

@Configuration
@EnableScheduling
class CrawlerSchedulingThreadConfig : SchedulingConfigurer {
    override fun configureTasks(taskRegistrar: ScheduledTaskRegistrar) {
        val threadPoolTaskScheduler = ThreadPoolTaskScheduler()
        threadPoolTaskScheduler.poolSize = 10
        threadPoolTaskScheduler.setThreadNamePrefix("scheduler-")
        // 스레드가 부족하면 즉시 중단 ( 크롤링 작업이 밀릴 수 있음)
        threadPoolTaskScheduler.setRejectedExecutionHandler(ThreadPoolExecutor.AbortPolicy())
        threadPoolTaskScheduler.initialize()
        taskRegistrar.setTaskScheduler(threadPoolTaskScheduler)
    }
}
