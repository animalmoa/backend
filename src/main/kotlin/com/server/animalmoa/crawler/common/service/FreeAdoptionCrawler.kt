package com.server.animalmoa.crawler.common.service

interface FreeAdoptionCrawler {
    fun crawlFreeAdoption()

    fun startCrawling() {
        try {
            crawlFreeAdoption()
        } finally {
            /*
            에러가 발생해도 쓰레드가 멈추지 않기 위함
             */
        }
    }
}
