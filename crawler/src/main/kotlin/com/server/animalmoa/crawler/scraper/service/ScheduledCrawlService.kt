package com.server.animalmoa.crawler.scraper.service

import com.server.animalmoa.crawler.scraper.source.umadong.UmadongScraper
import org.springframework.aop.support.AopUtils
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
@Profile("!test")
class ScheduledCrawlService(
    private val adoptionScrapers: List<AdoptionScraper>,
) {
     /*TODO 코루틴으로 변환(Scheduled 쓰레드 반환이 이후에 모든 작업이 끝난 후에 되어야한다.
     현재 비동기적 쓰레드를 호출하고 바로 쓰레드를 반환한다.
     그렇기에 [CrawlerSchedulerConfig]에서 Scheduing 쓰레드를 제한한 효과가 크게 없다.
      *
      * 시작할 떄와
      * 매 15분마다 실행되는 메서드
      * 이전 스케쥴링 작업이 끝나기전에는 실행되지 않는 로직 구현(현재는 Scheduled의 단일 스레드에 의존)
      *
      * 스케쥴링 시간 기록, 비정상적 크롤링 감지
      */
    @Scheduled(fixedDelay = 1000 * 60 * 15) // 매 15분마다 실행
    fun crawling() {
        adoptionScrapers.forEach { adoptionCrawler ->
            val targetClass = AopUtils.getTargetClass(adoptionCrawler)
            try {
                if (targetClass == UmadongScraper::class.java) {
                    adoptionCrawler.crawlAdoptionWithGuiWebDriver()
                } else {
                    adoptionCrawler.crawlAdoptionWithHeadlessWebDriver()
                }
            } finally {
                // 에러 발생 시 쓰레드가 멈추지 않도록
            }
        }
    }

//    // UmadonCrawler를 테스트 해보기 위한 메소드
//    @EventListener(ApplicationReadyEvent::class)
//    fun specificCrawling() {
//        adoptionCrawlers.forEach { adoptionCrawler ->
//            val targetClass = AopUtils.getTargetClass(adoptionCrawler)
//            try {
//                if (targetClass == UmadongCrawler::class.java) {
//                    adoptionCrawler.crawlAdoptionWithHeadlessWebDriverAsync()
//                }
//            }
//        }
//    }
}
