package com.server.animalmoa.crawler.crawler.service

import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
interface AdoptionCrawler {
    // AOP 기능을 적용하기 위해 default crawlAdoptionAsync를 default로 사용하고
    // 각 구현체마다 override된 crawlAdoption이 새로운 쓰레드에서 실행된다.
    // CrawlAsyncThreadConfig에 설정 존재
    @Async("headless-webdriver-per-thread")
    fun crawlAdoptionWithHeadlessWebDriverAsync() {
        crawlAdoption()
    }

    @Async("gui-webdriver-per-thread")
    fun crawlAdoptionWithGuiWebDriver() {
        crawlAdoption()
    }

    fun crawlAdoption()
}
