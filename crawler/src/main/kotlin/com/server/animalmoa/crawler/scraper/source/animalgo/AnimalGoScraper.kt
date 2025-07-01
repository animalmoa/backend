// package com.server.animalmoa.crawler.crawler.source.animalgo
//
// import com.server.animalmoa.common.adoption.domain.AdoptionStatus
// import com.server.animalmoa.common.adoption.domain.Source
// import com.server.animalmoa.common.common.PostType
// import com.server.animalmoa.common.dto.MakeAdoptionDto
// import com.server.animalmoa.crawler.crawler.service.AdoptionCrawler
// import com.server.animalmoa.crawler.crawler.service.CrawlerErrorService
// import com.server.animalmoa.crawler.webdriver.WebDriverCommandService
// import mu.KotlinLogging
// import org.springframework.beans.factory.annotation.Value
// import org.springframework.stereotype.Service
//
// /*
// 입양대상 동물 메뉴: 완료
// TODO 실종동물 페이지
// */
// @Service
// class AnimalGoCrawler(
//    private val webDriverCommandService: WebDriverCommandService,
//    private val animalGoDataManageService: AnimalGoDataManageService,
//    private val crawlerErrorService: CrawlerErrorService,
// ) : AdoptionCrawler {
//    private val logger = KotlinLogging.logger {}
//
//    @Value("\${crawl-until.page}")
//    private val maxPage: Int = 10
//
//    override fun crawlAdoption() {
//        val adoptionPath = AnimalGoData.adoption()
//        for (page in 1..maxPage) {
//            val freeAdoptionUrl =
//                "https://www.animal.go.kr/front/awtis/protection/protectionList.do?" +
//                    "menuNo=${adoptionPath.menuNoParam}" +
//                    "&page=$page"
//            webDriverCommandService.navigateTo(freeAdoptionUrl)
//            searchEachPage(adoptionPath)
//        }
//    }
//
//    private fun searchEachPage(freeAdoptionPath: AnimalGoData) {
//        var animals = webDriverCommandService.findElementsWithWaitingAlwaysAsList(freeAdoptionPath.animalsXpath)
//        for (index in animals.indices) {
//            // 매번 창은 초기화 되기 때문에 새로 검색해 주어야함
//            animals = webDriverCommandService.findElementsWithWaitingAlwaysAsList(freeAdoptionPath.animalsXpath)
//            if (index >= animals.size) break
//            crawlerErrorService.catchCrawlError(
//                {
//                    webDriverCommandService.clickElementWithAction(animals[index])
//                    animalGoDataManageService.processDataAndSave(
//                        MakeAdoptionDto(
//                            species = webDriverCommandService.findElementWithWaiting(freeAdoptionPath.essential.speciesXpath)?.text,
//                            breed = webDriverCommandService.findElementWithWaiting(freeAdoptionPath.essential.breedXpath)?.text,
//                            region = webDriverCommandService.findElementWithWaiting(freeAdoptionPath.essential.regionXpath)?.text,
//                            gender = webDriverCommandService.findElementWithWaiting(freeAdoptionPath.essential.genderXpath)?.text,
//                            title = webDriverCommandService.findElementWithWaiting(freeAdoptionPath.essential.titleXpath)?.text,
//                            content = webDriverCommandService.findElementWithWaiting(freeAdoptionPath.essential.contentXpath)?.text,
//                            age = webDriverCommandService.findElementWithWaiting(freeAdoptionPath.essential.ageXpath)?.text,
//                            createdAt = webDriverCommandService.findElementWithWaiting(freeAdoptionPath.createdAtXpath)?.text,
//                            thumbnailUrl =
//                                webDriverCommandService
//                                    .findElementWithWaiting(freeAdoptionPath.essential.thumbnailXpath)
//                                    ?.getAttribute("src"),
//                            postType = PostType.FREE_ADOPTION.name,
//                            adoptionStatus = AdoptionStatus.ING.name,
//                            originalUrl = webDriverCommandService.getWebDriver().currentUrl,
//                            source = Source.ANIMAL_GO,
//                            identifier = webDriverCommandService.getWebDriver().currentUrl,
//                        ),
//                    )
//                    webDriverCommandService.goBack()
//                },
//                logger,
//            )
//        }
//    }
// }
