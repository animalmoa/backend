package com.server.animalmoa.crawler.scraper.manager

import com.server.animalmoa.common.adoption.repository.AdoptionRepositoryService
import com.server.animalmoa.crawler.scraper.manager.Priority.Companion.OLD_POST_PRIORITY
import com.server.animalmoa.crawler.scraper.service.AdoptionScraper
import com.server.animalmoa.crawler.scraper.service.FindPostErrorService
import com.server.animalmoa.crawler.source.animalgo.AnimalGoScraper
import com.server.animalmoa.crawler.source.juseyo.JuseyoScraper
import com.server.animalmoa.crawler.source.wuripet.WuriPetScraper
import com.server.animalmoa.crawler.webdriver.WebDriverManager
import io.sentry.Sentry
import mu.KLogging
import org.springframework.aop.support.AopUtils
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import kotlin.jvm.java

@Suppress("ktlint:standard:no-consecutive-comments")
@Service
@Profile("!test")
class ScheduledScrapManager(
    private val adoptionScrapers: List<AdoptionScraper>,
    private val adoptionSaveManager: AdoptionSaveManager,
    private val adoptionRepositoryService: AdoptionRepositoryService,
    private val webDriverManager: WebDriverManager,
    private val findPostErrorService: FindPostErrorService,
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        adoptionSaveManager.consumeJob()
    }

    private val enableScraperClasses =
        listOf(
            WuriPetScraper::class.java,
            JuseyoScraper::class.java,
            AnimalGoScraper::class.java,
        )

    val logger = KLogging().logger

     /*
     2025.05.24
     TODO 스케쥴링 시간 기록
     updatePost() scrapNewPost()보다 훨씬 빠른 속도로 끝나기 때문에,
     initialDelay 차이를 두어 어플리케이션 실행 초기에 updatePost 먼저 수행한다.
      */
    @Scheduled(fixedDelay = 1000 * 60 * 10, initialDelay = 1000)
    fun scrapNewPost() {
        webDriverManager.resetWebDriver(false)

        findPostErrorService.catchScrawlError {
            logger.info { "scrap new post job started!" }
            adoptionScrapers.forEach { adoptionScraper ->
                val targetClass = AopUtils.getTargetClass(adoptionScraper)
                if (enableScraperClasses.contains(targetClass)) {
                    try {
                        adoptionScraper.findNewPost()
                    } catch (exceptionByClass: Exception) {
                        logger.error { exceptionByClass.message }
                        // 클래스 단위로 예외를 잡지 못 하였을 때
                    }
                }
            }
            logger.info { "scrap new post job finished!" }
        }
    }

    @Scheduled(fixedDelay = 60000 * 60 * 24)
    fun updatePost() {
        logger.info { "update job started!" }
        Sentry.captureException(Exception("update job started!"))

        // 2025.07.30 2주전 게시글 까지만 크롤링한다.
        adoptionRepositoryService.findAfter(LocalDateTime.now().minusWeeks(1)).forEach { adoption ->
            adoptionScrapers.forEach { adoptionScraper ->
                if (adoptionScraper.isSource(adoption.source)) {
                    adoptionSaveManager.addAdoptionToSaveQueue(
                        AdoptionToSave(
                            adoption.originalUrl,
                            Priority(OLD_POST_PRIORITY),
                            {
                                adoptionScraper.scrapAdoptionInformation(
                                    adoption.originalUrl,
                                    adoption.identifier,
                                )
                            },
                        ),
                    )
                }
            }
        }
        logger.info { "update job finished!" }
    }
}
