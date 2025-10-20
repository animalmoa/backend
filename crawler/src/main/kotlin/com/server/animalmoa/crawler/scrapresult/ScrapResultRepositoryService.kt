package com.server.animalmoa.crawler.scrapresult

import com.server.animalmoa.crawler.scraper.manager.AdoptionToSave
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class ScrapResultRepositoryService(
    private val scrapResultRepository: ScrapResultRepository,
) {
    fun saveScrapResult(
        adoptionToSave: AdoptionToSave,
        isSuccess: Boolean,
    ) {
        scrapResultRepository.save(
            ScrapResult(
                adoptionToSave.url,
                adoptionToSave.scrapInfo.source,
                adoptionToSave.scrapInfo.scrapType,
                isSuccess,
            ),
        )
    }

    // 스크래핑 결과는 일주일에 한 번씩 다 지운다.
    @Scheduled(fixedDelay = 1000 * 60 * 60 * 24 * 7)
    fun deleteEveryDay() {
        scrapResultRepository.deleteAll()
    }
}
