package com.server.animalmoa.crawler

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class ScheduledCrawlService(
    private val freeAdoptCrawlers: List<FreeAdoptionCrawler>,
) {
    /**
     * 시작할 떄와
     * 매 15분마다 실행되는 메서드
     */
    @PostConstruct
    @Scheduled(cron = "0 0/15 * * * *") // 매 15분마다 실행
    fun crawlFreeAdoptionCrawler() {
        freeAdoptCrawlers.forEach { freeAdoptionCrawler ->
            freeAdoptionCrawler.crawlFreeAdoption()
        }
    }
}
