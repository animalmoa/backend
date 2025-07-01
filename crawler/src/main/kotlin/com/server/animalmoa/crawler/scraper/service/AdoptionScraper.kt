package com.server.animalmoa.crawler.scraper.service

import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
interface AdoptionScraper {
    // 새로운 쓰레드를 분배하는 AOP 기능을 적용하기 위해 default crawlAdoptionAsync 메소드를 외부에서 호출한다.
    // 각 구현체마다 override된 crawlAdoption() 에서 실행된다.
    // CrawlAsyncThreadConfig에 설정 존재
    @Async("headless-webdriver-per-thread")
    fun scrapAdoptionWithHeadlessWebDriver() {
        scrapAdoptionPost()
    }

    @Async("gui-webdriver-per-thread")
    fun scrapAdoptionWithGuiWebDriver() {
        scrapAdoptionPost()
    }

    fun scrapAdoptionPost()
}
