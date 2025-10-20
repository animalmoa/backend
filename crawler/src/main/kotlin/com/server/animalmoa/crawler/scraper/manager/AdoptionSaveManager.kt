package com.server.animalmoa.crawler.scraper.manager

import com.server.animalmoa.common.adoption.domain.Adoption
import com.server.animalmoa.common.adoption.enum.Source
import com.server.animalmoa.common.adoption.repository.AdoptionRepositoryService
import com.server.animalmoa.common.dto.MakeAdoptionDto
import com.server.animalmoa.crawler.ScrapType
import com.server.animalmoa.crawler.exception.EmptyHtmlException
import com.server.animalmoa.crawler.scrapresult.ScrapResultRepositoryService
import com.server.animalmoa.crawler.webdriver.WebDriverManager
import io.sentry.Sentry
import mu.KotlinLogging
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.PriorityBlockingQueue

/**
 * URL과 해당 URL 사이트의 HTML을 파싱하는 방법을 담고 있는 클래스
 */

data class ScrapInfo(
    val scrapType: ScrapType,
    val source: Source,
    // 큐에 추가된 순서부터 업데이트한다
    val createdAt: LocalDateTime = LocalDateTime.now(),
) : Comparable<ScrapInfo> {
    override fun compareTo(other: ScrapInfo): Int =
        compareValuesBy(
            this,
            other,
            { it.scrapType.priority },
            { it.createdAt },
        )
}

data class AdoptionToSave(
    val url: String,
    val scrapInfo: ScrapInfo,
    val makeAdoptionDtoFunction: () -> MakeAdoptionDto,
)

@Service
class AdoptionSaveManager(
    private val adoptionRepositoryService: AdoptionRepositoryService,
    private val webDriverManager: WebDriverManager,
    private val scrapResultRepositoryService: ScrapResultRepositoryService,
) {
    val logger = KotlinLogging.logger {}

    // 게시글 스크래핑 요청이 많아도 최대 N개까지만 저장 대기 가능하다.
    // N개는 새로운 게시글, 업데이트할 기존 게시글들의 합이며. 기존 게시글일 경우 최신글들이 우선 순위를 갖는다.
    private val adoptionToSavePriorityQueue =
        PriorityBlockingQueue<AdoptionToSave>(
            1000,
            Comparator { o1, o2 ->
                o1.scrapInfo.compareTo(o2.scrapInfo)
            },
        )

    // 현재 Save 큐에 들어가 있거나, 작업 중인 URL을 담음
    // Save 큐에 없지만, 현재 스크래핑중인 URL이 큐에 들어오지 않게 하기 위함
    private val processingUrls = ConcurrentHashMap.newKeySet<String>()

    @Async("get-html")
    fun consumeJob() {
        while (!Thread.currentThread().isInterrupted) {
            val adoptionToSave = adoptionToSavePriorityQueue.take() // 큐가 비면 자동 대기(Block)
            try {
                logger.info(
                    "trying to ${adoptionToSave.scrapInfo.scrapType.name} " +
                        "information from ${adoptionToSave.url} ...",
                )
                val makeAdoptionDto = adoptionToSave.makeAdoptionDtoFunction()
                adoptionRepositoryService.ifNewSaveElseUpdate(Adoption.from(makeAdoptionDto))
                scrapResultRepositoryService.saveScrapResult(
                    adoptionToSave,
                    true,
                )
            } catch (e: EmptyHtmlException) {
                webDriverManager.resetWebDriver(true)
                logger.error(e) { "Adoption save failed :$adoptionToSave" }
                Sentry.captureException(e)

                scrapResultRepositoryService.saveScrapResult(
                    adoptionToSave,
                    false,
                )
            } catch (e: Exception) {
                webDriverManager.resetWebDriver(true)
                logger.error(e) { "Adoption save failed :$adoptionToSave" }
                Sentry.captureException(e)

                scrapResultRepositoryService.saveScrapResult(
                    adoptionToSave,
                    false,
                )
            } finally {
                processingUrls.remove(adoptionToSave.url)
            }
        }
    }

    fun addAdoptionToSaveQueue(adoptionToSave: AdoptionToSave) {
        if (processingUrls.add(adoptionToSave.url)) {
            // add의 경우 꽉 차면 에러.
            // offer의 경우 true/false 반환
            // put의 경우 무한 대기
            runCatching { adoptionToSavePriorityQueue.add(adoptionToSave) }
                .onFailure { processingUrls.remove(adoptionToSave.url) }
        }
    }

    fun isNewPost(
        source: Source,
        identifier: String,
    ): Boolean = adoptionRepositoryService.findBy(source, identifier) == null
}
