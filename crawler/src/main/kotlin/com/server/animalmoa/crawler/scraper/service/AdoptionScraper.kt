package com.server.animalmoa.crawler.scraper.service

import com.server.animalmoa.common.adoption.enum.Source
import com.server.animalmoa.common.dto.MakeAdoptionDto
import com.server.animalmoa.crawler.exception.IdentifierOrUrlNotFoundException
import com.server.animalmoa.crawler.scraper.manager.AdoptionSaveManager
import com.server.animalmoa.crawler.scraper.manager.AdoptionToSave
import com.server.animalmoa.crawler.scraper.manager.Priority
import com.server.animalmoa.crawler.webdriver.WebDriverCommandService
import mu.KotlinLogging
import org.springframework.stereotype.Service

@Service
abstract class AdoptionScraper(
    protected val webDriverCommandService: WebDriverCommandService,
    protected val adoptionSaveManager: AdoptionSaveManager,
    protected val findPostErrorService: FindPostErrorService,
) {
    abstract val source: Source

    protected val logger by lazy { KotlinLogging.logger { source } }

    abstract fun findNewPost()

    abstract fun scrapAdoptionInformation(
        postUrl: String,
        identifier: String,
    ): MakeAdoptionDto

    fun isSource(source: Source): Boolean = this.source == source

    // identifier, posturl을 못찾아도 일단 다음 글도 살펴봐야하기에
    // 발생할 수 있는 에러에 대하여 호출부에서 에러를 Catch해주어야한다.
    fun scrapNewPost(
        identifier: String?,
        postUrl: String?,
    ): Boolean {
        // 글의 고유 번호를 식별할 수 없거나, Url이 없다면 크롤링할 수 없다.
        if (identifier == null || postUrl == null) {
            throw IdentifierOrUrlNotFoundException("Extracting fail. identifier: $identifier, postUrl: $postUrl")
        } else {
            return if (adoptionSaveManager.isNewPost(source, identifier)) {
                logger.info { "New post found. identifier: $identifier, postUrl: $postUrl" }
                adoptionSaveManager.addAdoptionToSaveQueue(
                    AdoptionToSave(
                        postUrl,
                        Priority(Priority.NEW_POST_PRIORITY),
                        { scrapAdoptionInformation(postUrl, identifier) },
                    ),
                )
                true
            } else {
                false
            }
        }
    }
}
