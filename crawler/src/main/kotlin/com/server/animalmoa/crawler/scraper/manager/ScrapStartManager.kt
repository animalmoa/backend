package com.server.animalmoa.crawler.scraper.manager

import com.server.animalmoa.crawler.scraper.service.AdoptionScraper
import com.server.animalmoa.crawler.source.wuripet.WuriPetScraper
import mu.KLogging
import org.springframework.aop.support.AopUtils
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import kotlin.jvm.java

@Service
@Profile("!test")
class ScrapStartManager(
    private val adoptionScrapers: List<AdoptionScraper>,
    private val adoptionSaveManager: AdoptionSaveManager,
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        adoptionSaveManager.consumeJob()
    }

    private val enableScraperClasses =
        listOf(
            WuriPetScraper::class.java,
//            JuseyoScraper::class.java,
//            AnimalGoScraper::class.java,
        )

    val logger = KLogging().logger

     /*
     2025.05.24
     TODO 스케쥴링 시간 기록
      */
    @Scheduled(fixedDelay = 60000)
    fun scrawlNewPost() {
        try {
            logger.info { "job started!" }

            adoptionScrapers.forEach { adoptionScraper ->
                val targetClass = AopUtils.getTargetClass(adoptionScraper)
                if (enableScraperClasses.contains(targetClass)) {
                    try {
                        adoptionScraper.scrapAdoptionPost()
                    } catch (exceptionByClass: Exception) {
                        logger.error { exceptionByClass.message }
                        // 클래스 단위로 예외를 잡지 못 하였을 때
                    }
                }
            }
            logger.info { "job finished!" }
        } catch (e: Exception) {
            logger.error { e.printStackTrace() }
        }
    }
}
