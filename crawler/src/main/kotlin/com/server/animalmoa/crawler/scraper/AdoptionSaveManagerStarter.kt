package com.server.animalmoa.crawler.scraper

import com.server.animalmoa.crawler.scraper.threadmanager.AdoptionSaveManager
import com.server.animalmoa.crawler.scraper.threadmanager.ScheduledScrapService
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class AdoptionSaveManagerStarter(
    private val adoptionSaveManager: AdoptionSaveManager,
    private val scheduledScrapService: ScheduledScrapService,
) : ApplicationRunner {
    // Application 시작 직후 다음을 실행한다.
    // 1. 최신글 url 스크래핑 메소드
    // 2. url에 접속 후 html 스크래핑 메소드
    override fun run(args: ApplicationArguments?) {
        adoptionSaveManager.consumeJob()
        scheduledScrapService.scrawlJob()
    }
}
