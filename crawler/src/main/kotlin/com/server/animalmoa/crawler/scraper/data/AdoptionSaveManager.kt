package com.server.animalmoa.crawler.scraper.data

import com.server.animalmoa.common.adoption.domain.Adoption
import com.server.animalmoa.common.dto.MakeAdoptionDto
import com.server.animalmoa.common.repository.AdoptionRepositoryService
import jakarta.annotation.PostConstruct
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.util.concurrent.PriorityBlockingQueue

/**
 * URL과 해당 URL을 파싱하는 방법을 담고 있는 클래스
 */
data class AdoptionToSave(
    val url: String,
    val makeAdoptionDto: MakeAdoptionDto,
    val priority: Int,
    val parseFunction: (MakeAdoptionDto) -> Adoption?,
)

@Service
class AdoptionSaveManager(
    private val adoptionRepositoryService: AdoptionRepositoryService,
) {
    // 게시글 크롤링 요청이 많아도 최대 N개까지만 저장 가능하다.
    // N개는 새로운 게시글, 업데이트할 기존 게시글들의 합이며. 기존 게시글일 경우 최신글들이 우선 순위를 갖는다.
    private val adoptionToSavePriorityQueue =
        PriorityBlockingQueue(100, Comparator.comparingInt(AdoptionToSave::priority))

    @PostConstruct
    fun init() {
        consume()
    }

    @Async("headless-webdriver-per-thread")
    fun consume() {
        while (!Thread.currentThread().isInterrupted) {
            val job = adoptionToSavePriorityQueue.take() // 큐가 비면 자동 대기(Block)
            kotlin
                .runCatching { job.makeAdoptionDto.let(job.parseFunction) }
                .onSuccess { it?.let(adoptionRepositoryService::ifExistUpdateElseSaveBySourceAndIdentifier) }
        }
    }

    fun addAdoptionToQueue(adoptionToSave: AdoptionToSave) {
        adoptionToSavePriorityQueue.add(adoptionToSave)
    }
}
