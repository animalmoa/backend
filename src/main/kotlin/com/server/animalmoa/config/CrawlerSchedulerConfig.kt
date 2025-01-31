package com.server.animalmoa.config

import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.scheduling.config.ScheduledTaskRegistrar

@Configuration
@EnableScheduling
class CrawlerSchedulerConfig : SchedulingConfigurer {
    override fun configureTasks(taskRegistrar: ScheduledTaskRegistrar) {
        val threadPoolTaskScheduler = ThreadPoolTaskScheduler()
        threadPoolTaskScheduler.poolSize = 30
        threadPoolTaskScheduler.setThreadNamePrefix("scheduler-")
        threadPoolTaskScheduler.initialize()
        taskRegistrar.setTaskScheduler(threadPoolTaskScheduler)
    }
}
