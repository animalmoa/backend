package com.server.animalmoa.crawler.scraper.manager

import com.server.animalmoa.common.adoption.domain.Adoption
import com.server.animalmoa.common.adoption.enum.Source
import com.server.animalmoa.common.adoption.repository.AdoptionRepositoryService
import com.server.animalmoa.common.dto.MakeAdoptionDto
import com.server.animalmoa.common.log.ErrorLogRepositoryService
import com.server.animalmoa.crawler.exception.EmptyHtmlException
import com.server.animalmoa.crawler.webdriver.WebDriverManager
import mu.KotlinLogging
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.PriorityBlockingQueue

/**
 * URL과 해당 URL 사이트의 HTML을 파싱하는 방법을 담고 있는 클래스
 */

data class Priority(
    val level: Int,
    // 큐에 추가된 순서부터 업데이트한다
    val createdAt: LocalDateTime = LocalDateTime.now(),
) : Comparable<Priority> {
    // 최신글부터, CreatedAt이 최신순부터
    override fun compareTo(other: Priority): Int =
        compareValuesBy(
            this,
            other,
            { it.level },
            { it.createdAt },
        )

    companion object {
        const val NEW_POST_PRIORITY = 0
        const val OLD_POST_PRIORITY = 1
    }
}

data class AdoptionToSave(
    val url: String,
    val priority: Priority,
    val makeAdoptionDtoFunction: () -> MakeAdoptionDto,
)

@Service
class AdoptionSaveManager(
    private val adoptionRepositoryService: AdoptionRepositoryService,
    private val webDriverManager: WebDriverManager,
    private val errorLogRepositoryService: ErrorLogRepositoryService,
) {
    val logger = KotlinLogging.logger {}

    // 게시글 스크래핑 요청이 많아도 최대 N개까지만 저장 대기 가능하다.
    // N개는 새로운 게시글, 업데이트할 기존 게시글들의 합이며. 기존 게시글일 경우 최신글들이 우선 순위를 갖는다.
    private val adoptionToSavePriorityQueue =
        PriorityBlockingQueue<AdoptionToSave>(
            1000,
            Comparator { o1, o2 ->
                o1.priority.compareTo(o2.priority)
            },
        )

    // 현재 Save 큐에 들어가 있거나, 작업 중인 URL을 담음
    // Save 큐에 없지만, 현재 스크래핑중인 URL이 큐에 들어오지 않게 하기 위함
    private val processingUrls = ConcurrentHashMap.newKeySet<String>()

    @Async("get-html")
    fun consumeJob() {
        while (!Thread.currentThread().isInterrupted) {
            val adoptionPostToSave = adoptionToSavePriorityQueue.take() // 큐가 비면 자동 대기(Block)
            try {
                logger.info("trying to scrap information from ${adoptionPostToSave.url} ...")
                val makeAdoptionDto = adoptionPostToSave.makeAdoptionDtoFunction()
                adoptionRepositoryService.ifNewSaveElseUpdate(Adoption.from(makeAdoptionDto))
            } catch (e: EmptyHtmlException) {
                webDriverManager.removeWebDriver()
                webDriverManager.setNewWebDriver(headless = true)

                logger.error(e) { "Adoption save failed :$adoptionPostToSave" }
                errorLogRepositoryService.save(e)
            } catch (e: Exception) {
                logger.error(e) { "Adoption save failed :$adoptionPostToSave" }
                errorLogRepositoryService.save(e)
            } finally {
                processingUrls.remove(adoptionPostToSave.url)
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
