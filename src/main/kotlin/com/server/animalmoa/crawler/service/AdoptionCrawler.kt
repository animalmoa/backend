package com.server.animalmoa.crawler.service

import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
interface AdoptionCrawler {
    @Async("webdriver-per-thread")
    fun crawlAdoptionAsync() {
        crawlAdoption()
    }

    fun crawlAdoption()
}
