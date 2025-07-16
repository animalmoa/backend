package com.server.animalmoa.crawler.scraper.threadmanager

import com.server.animalmoa.crawler.scraper.service.AdoptionScraper
import com.server.animalmoa.crawler.scraper.source.animalgo.AnimalGoScraper
import com.server.animalmoa.crawler.scraper.source.juseyo.JuseyoScraper
import org.springframework.aop.support.AopUtils
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import kotlin.jvm.java

@Service
@Profile("!test")
class ScheduledScrapService(
    private val adoptionScrapers: List<AdoptionScraper>,
) {
    private val enableScraperClasses =
        listOf(JuseyoScraper::class.java, AnimalGoScraper::class.java)

     /*
     2025.05.24
     각 AdoptionCrawlers의 메소드들은 비동기적인 쓰레드에서 실행된다.

     시작할 떄와
     매 1분마다 실행되는 메서드
     이전 스케쥴링 작업이 끝나기전에는 실행되지 않는 로직 구현(현재는 Scheduled의 단일 스레드에 의존)

     TODO 스케쥴링 시간 기록, 비정상적 크롤링 감지, 코루틴으로 변환
      */
    @Async("un-headless")
    fun scrawlJob() {
        while (true) {
            adoptionScrapers.forEach { adoptionScraper ->
                val targetClass = AopUtils.getTargetClass(adoptionScraper)
                try {
                    if (enableScraperClasses.contains(targetClass)) {
                        adoptionScraper.scrapAdoptionPost()
                    }
                } finally {
                    // 에러 발생 시 쓰레드가 멈추지 않도록
                }
            }
            // 1분마다 실행
            Thread.sleep(60000)
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
