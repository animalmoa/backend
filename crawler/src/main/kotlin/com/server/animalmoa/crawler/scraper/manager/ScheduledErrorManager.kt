package com.server.animalmoa.crawler.scraper.manager

import com.server.animalmoa.common.log.ErrorLogRepositoryService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ScheduledErrorManager(
    private val errorLogRepositoryService: ErrorLogRepositoryService,
) {
    @Scheduled(fixedDelay = 1000 * 60 * 60 * 1)
    fun deleteOldErrorLog() {
        errorLogRepositoryService.deleteBefore(LocalDateTime.now().minusHours(1))
    }
}
