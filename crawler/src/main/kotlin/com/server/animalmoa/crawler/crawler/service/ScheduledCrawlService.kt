package com.server.animalmoa.crawler.crawler.service

import com.server.animalmoa.crawler.crawler.source.umadong.UmadongCrawler
import org.springframework.aop.support.AopUtils
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import kotlin.jvm.java

@Service
@Profile("!test")
class ScheduledCrawlService(
    private val adoptionCrawlers: List<AdoptionCrawler>,
) {
     /*
     2025.05.24
     각 AdoptionCrawlers의 메소드들은 비동기적인 쓰레드에서 실행된다.

     시작할 떄와
     매 15분마다 실행되는 메서드
     이전 스케쥴링 작업이 끝나기전에는 실행되지 않는 로직 구현(현재는 Scheduled의 단일 스레드에 의존)

     TODO 스케쥴링 시간 기록, 비정상적 크롤링 감지, 코루틴으로 변환
      */
    @Scheduled(fixedDelay = 1000 * 60 * 15) // 매 15분마다 실행
    fun crawling() {
        adoptionCrawlers.forEach { adoptionCrawler ->
            val targetClass = AopUtils.getTargetClass(adoptionCrawler)
            try {
                if (targetClass == UmadongCrawler::class.java) {
                    //                    adoptionCrawler.crawlAdoptionWithGuiWebDriver()
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
