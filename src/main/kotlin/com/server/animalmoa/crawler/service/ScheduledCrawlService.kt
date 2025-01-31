package com.server.animalmoa.crawler.service

import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
@Profile("!test")
class ScheduledCrawlService(
    private val adoptionCrawlers: List<AdoptionCrawler>,
) {
    /**
     * 시작할 떄와
     * 매 15분마다 실행되는 메서드
     * TODO 비동기적으로 구현해야함.(모든 사이트의 모든 동물들이 한 번에 최신화되어야함)
     * 이전 스케쥴링 작업이 끝나기전에는 실행되지 않는 로직 구현(현재는 Scheduled의 단일 스레드에 의존)
     *
     * 스케쥴링 시간 기록, 비정상적 크롤링 감지
     */
    @Scheduled(fixedDelay = 1000 * 60 * 15) // 매 15분마다 실행
    fun startCrawling() {
        adoptionCrawlers.forEach { freeAdoptionCrawler ->
            try {
                freeAdoptionCrawler.crawlAdoption()
            } finally {
                /*
                에러가 발생해도 쓰레드가 멈추지 않기 위함
                 */
            }
        }
    }
}
