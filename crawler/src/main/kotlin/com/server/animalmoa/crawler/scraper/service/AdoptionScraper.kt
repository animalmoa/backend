package com.server.animalmoa.crawler.scraper.service

import com.server.animalmoa.common.adoption.enum.Source
import com.server.animalmoa.common.dto.MakeAdoptionDto
import com.server.animalmoa.crawler.scraper.manager.AdoptionSaveManager
import com.server.animalmoa.crawler.scraper.manager.AdoptionToSave
import com.server.animalmoa.crawler.webdriver.WebDriverCommandService
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
abstract class AdoptionScraper(
    protected val webDriverCommandService: WebDriverCommandService,
    protected val adoptionSaveManager: AdoptionSaveManager,
    protected val scraperErrorService: ScraperErrorService,
) {
    abstract val source: Source

    val logger = KotlinLogging.logger { source }

    init {
        scraperErrorService.logger = KotlinLogging.logger { source }
    }

    abstract fun scrapAdoptionPost()

    // 2025.07.23
    // default는 순차적으로 글을 탐색하며 이미 스크래핑했던 글이 나오면 스크래핑을 멈춘다,
    // 최신글 순서대로 정렬이 안 될 수도 있고, 이미 스크래핑했던 글이 나와도 마지막 글까지 스크래핑되지 않는 글을 찾아봐야할 수 있다.
    // 그럴 때 적절하게 override한다
    fun scrapNewPost(
        identifier: String?,
        postUrl: String?,
        makeAdoptionDtoFunc: () -> MakeAdoptionDto,
    ): Boolean {
        // 시글의 고유 번호를 식별할 수 없거나, Url이 없다면 크롤링할 수 없다.
        // 하지만 에러 발생은 하지않는다.
        if (identifier == null || postUrl == null) {
            logger.error { "Extracting fail. identifier: $identifier, postUrl: $postUrl" }
            return false
        } else {
            // 만약 DB에 있는글을 만날시에 스크래핑을 그만둬야한다면 에러 발생시켜야한다
            //                throw AlreadySavedPostException(postUrl)
            return if (adoptionSaveManager.isNewPost(source, identifier)) {
                adoptionSaveManager.addAdoptionToSaveQueue(
                    AdoptionToSave(
                        postUrl,
                        makeAdoptionDtoFunc,
                        AdoptionToSave.NEW_POST_PRIORITY,
                    ),
                )
                true
            } else {
                false
            }
        }
    }
}
