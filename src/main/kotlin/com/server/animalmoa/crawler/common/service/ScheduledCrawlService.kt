package com.server.animalmoa.crawler.common.service

import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
@Profile("!test")
class ScheduledCrawlService(
    private val freeAdoptCrawlers: List<FreeAdoptionCrawler>,
) {
    /**
     * 시작할 떄와
     * 매 15분마다 실행되는 메서드
     * TODO 이전 스케쥴링 작업이 끝나기전에는 실행되지 않는 로직 구현(현재는 Scheduled의 단일 스레드에 의존)
     * 스케쥴링 시간 기록, 비정상적 크롤링 감지
     */
    // @PostConstruct
    @Scheduled(cron = "0 0/15 * * * *") // 매 15분마다 실행
    fun crawlFreeAdoptionCrawler() {
        freeAdoptCrawlers.forEach { freeAdoptionCrawler ->
            freeAdoptionCrawler.startCrawling()
        }
    }
}
