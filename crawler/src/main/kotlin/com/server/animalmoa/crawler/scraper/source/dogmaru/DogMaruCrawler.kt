// package com.server.animalmoa.crawler.crawler.source.dogmaru
//
// import com.server.animalmoa.common.adoption.domain.Source
// import com.server.animalmoa.common.dto.MakeAdoptionDto
// import com.server.animalmoa.crawler.crawler.service.AdoptionCrawler
// import com.server.animalmoa.crawler.crawler.service.CrawlerErrorService
// import com.server.animalmoa.crawler.webdriver.WebDriverCommandService
// import mu.KotlinLogging
// import org.openqa.selenium.By
// import org.springframework.beans.factory.annotation.Value
// import org.springframework.stereotype.Service
//
// @Service
// class DogMaruCrawler(
//    private val webDriverCommandService: WebDriverCommandService,
//    private val dogMaruDataManageService: DogMaruDataManageService,
//    private val crawlerErrorService: CrawlerErrorService,
// ) : AdoptionCrawler {
//    val logger = KotlinLogging.logger {}
//
//    @Value("\${crawl-until.page}")
//    private val maxPage: Int = 10
//
//    override fun crawlAdoption() {
//        val params =
//            listOf(
//                DogMaruData.cat(),
//                DogMaruData.dog(),
//            )
//
//        for (param in params) {
//            for (page in 1..maxPage) {
//                val url = "https://www.dmanimal.co.kr/adoption?page=$page"
//                webDriverCommandService.navigateTo(url)
//                searchEachPage(param)
//            }
//        }
//    }
//
//    private fun searchEachPage(dataExtractor: DogMaruData) {
//        val postElements = webDriverCommandService.findElementsWithWaitingAlwaysAsList(dataExtractor.eachPostXpath)
//
//        for (postElement in postElements) {
//            crawlerErrorService.catchCrawlError({
//                val title = postElement.findElement(By.xpath(dataExtractor.titleXpath)).text
//                val thumbnailUrl = postElement.findElement(By.xpath(dataExtractor.thumbnailXpath)).getAttribute("src") ?: ""
//                val linkElement = postElement.findElement(By.xpath(dataExtractor.linkXpath))
//
//                webDriverCommandService.clickElementWithAction(linkElement)
//
//                // 상세 페이지에서 데이터 추출
//                val originalUrl = webDriverCommandService.getWebDriver().currentUrl
//                val content = webDriverCommandService.findElementWithWaiting(dataExtractor.contentXpath)?.text ?: ""
//                val createdAt = webDriverCommandService.findElementWithWaiting(dataExtractor.createdAtXpath)?.text
//
//                // 정보 테이블에서 데이터 추출
//                val infoTable = webDriverCommandService.findElementWithWaiting(dataExtractor.infoTableXpath)?.text ?: ""
//
//                dogMaruDataManageService.processDataAndSave(
//                    MakeAdoptionDto(
//                        originalUrl = originalUrl,
//                        title = title,
//                        content = content,
//                        thumbnailUrl = thumbnailUrl,
//                        createdAt = createdAt,
//                        region = dataExtractor.region(infoTable),
//                        species = dataExtractor.species.toString(),
//                        breed = dataExtractor.breed(infoTable),
//                        age = dataExtractor.age(infoTable),
//                        gender = dataExtractor.gender(infoTable),
//                        source = Source.DOGMARU,
//                        identifier = originalUrl,
//                        postType = "FREE_ADOPTION",
//                        adoptionStatus = "ING",
//                    ),
//                )
//
//                // 상세 페이지에서 목록으로 돌아가기
//                webDriverCommandService.goBack()
//            }, logger)
//        }
//    }
// }
