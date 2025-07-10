package com.server.animalmoa.crawler.scraper.starter

import com.server.animalmoa.common.adoption.domain.Adoption
import com.server.animalmoa.common.adoption.domain.Source
import com.server.animalmoa.common.dto.MakeAdoptionDto
import com.server.animalmoa.common.repository.AdoptionRepositoryService
import mu.KotlinLogging
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
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

//    @Async("headless")
    @Async("un-headless")
    fun consumeJob() {
        while (!Thread.currentThread().isInterrupted) {
            val post = adoptionToSavePriorityQueue.take() // 큐가 비면 자동 대기(Block)
            runCatching { post.makeAdoptionDtoFunction() }
                .onSuccess { adoptionRepositoryService.ifNewSaveElseUpdate(Adoption.from(it)) }
                .onFailure { e -> logger.error(e) { "Error while saving adoption dto $post" } }
        }
    }

    fun addAdoptionToQueue(adoptionToSave: AdoptionToSave) {
        adoptionToSavePriorityQueue.add(adoptionToSave)
    }

    fun isNewPost(
        source: Source,
        identifier: String,
    ): Boolean = adoptionRepositoryService.findBy(source, identifier) == null
}
