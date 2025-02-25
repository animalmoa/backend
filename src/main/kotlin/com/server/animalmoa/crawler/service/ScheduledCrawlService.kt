package com.server.animalmoa.crawler.service

import com.server.animalmoa.crawler.source.umadong.UmadongCrawler
import org.springframework.aop.support.AopUtils
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
@Profile("!test")
class ScheduledCrawlService(
    private val adoptionCrawlers: List<AdoptionCrawler>,
) {
    /**
     * TODO 코루틴으로 변환( Scheduled 쓰레드 반환이 이후에 모든 작업이 끝난 후에 되어야함)
     */
//    /**
//     * 시작할 떄와
//     * 매 15분마다 실행되는 메서드
//     * 이전 스케쥴링 작업이 끝나기전에는 실행되지 않는 로직 구현(현재는 Scheduled의 단일 스레드에 의존)
//     *
//     * 스케쥴링 시간 기록, 비정상적 크롤링 감지
//     */
//    @Scheduled(fixedDelay = 1000 * 60 * 15) // 매 15분마다 실행
//    fun crawling() {
//        adoptionCrawlers.forEach { adoptionCrawler ->
//            try {
//                adoptionCrawler.crawlAdoptionAsync()
//            } finally {
//                /*
//                에러가 발생해도 쓰레드가 멈추지 않기 위함
//                 */
//            }
//        }
//    }

    // @Scheduled(fixedDelay = 1000 * 60 * 10)
    @EventListener(ApplicationReadyEvent::class)
    fun specificCrawling() {
        adoptionCrawlers.forEach { adoptionCrawler ->
            val targetClass = AopUtils.getTargetClass(adoptionCrawler)
            try {
                if (targetClass == UmadongCrawler::class.java) {
                    adoptionCrawler.crawlAdoptionAsync()
                }
            } finally {
                // 에러 발생 시 쓰레드가 멈추지 않도록
            }
        }
    }
}
