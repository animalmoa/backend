package com.server.animalmoa.crawler.scraper.manager

import com.server.animalmoa.common.adoption.domain.Adoption
import com.server.animalmoa.common.adoption.enum.Source
import com.server.animalmoa.common.dto.MakeAdoptionDto
import com.server.animalmoa.common.repository.AdoptionRepositoryService
import mu.KotlinLogging
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.PriorityBlockingQueue

/**
 * URL과 해당 URL 사이트의 HTML을 파싱하는 방법을 담고 있는 클래스
 */
data class AdoptionToSave(
    val url: String,
    val makeAdoptionDtoFunction: () -> MakeAdoptionDto,
    val priority: Int,
) {
    companion object {
        const val NEW_POST_PRIORITY = 0
        const val OLD_POST_PRIORITY = 1
    }
}

@Service
class AdoptionSaveManager(
    private val adoptionRepositoryService: AdoptionRepositoryService,
) {
    val logger = KotlinLogging.logger {}

    // 게시글 스크래핑 요청이 많아도 최대 N개까지만 저장 대기 가능하다.
    // N개는 새로운 게시글, 업데이트할 기존 게시글들의 합이며. 기존 게시글일 경우 최신글들이 우선 순위를 갖는다.
    private val adoptionToSavePriorityQueue =
        PriorityBlockingQueue(1000, Comparator.comparingInt(AdoptionToSave::priority))

    // 현재 큐에 들어가 있거나, 작업 중인 URL을 담음
    // 큐에 없지만, 현재 스크래핑중인 URL이 큐에 들어오지 않게 하기 위함
    private val pendingUrls = ConcurrentHashMap.newKeySet<String>()

    @Async("get-html")
    fun consumeJob() {
        while (!Thread.currentThread().isInterrupted) {
            val adoptionPostToSave = adoptionToSavePriorityQueue.take() // 큐가 비면 자동 대기(Block)

            try {
                val makeAdoptionDto = adoptionPostToSave.makeAdoptionDtoFunction()
                adoptionRepositoryService.ifNewSaveElseUpdate(Adoption.from(makeAdoptionDto))
            } catch (e: Exception) {
                logger.error(e) { "Adoption save failed :$adoptionPostToSave" }
            } finally {
                pendingUrls.remove(adoptionPostToSave.url)
            }
        }
    }

    fun addAdoptionToQueue(adoptionToSave: AdoptionToSave) {
        if (pendingUrls.add(adoptionToSave.url)) {
            adoptionToSavePriorityQueue.add(adoptionToSave)
        }
    }

    fun isNewPost(
        source: Source,
        identifier: String,
    ): Boolean = adoptionRepositoryService.findBy(source, identifier) == null
}
