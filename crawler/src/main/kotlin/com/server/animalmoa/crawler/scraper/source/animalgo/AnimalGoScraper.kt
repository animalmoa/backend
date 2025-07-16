package com.server.animalmoa.crawler.scraper.source.animalgo

import com.server.animalmoa.crawler.scraper.service.AdoptionScraper
import com.server.animalmoa.crawler.scraper.service.ScraperErrorService
import com.server.animalmoa.crawler.webdriver.WebDriverCommandService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

 /*
 입양대상 동물 메뉴: 완료
 TODO 실종동물 페이지
  */
@Service
class AnimalGoScraper(
    private val webDriverCommandService: WebDriverCommandService,
    private val animalGoDataManageService: AnimalGoDataManageService,
    private val scraperErrorService: ScraperErrorService,
) : AdoptionScraper {
    private val logger = KotlinLogging.logger {}

    @Value("\${scrap-until.page}")
    private val maxPage: Int = 10

    override fun scrapAdoptionPost() {
        val adoptionPath = AnimalAdoptionHtmlParser.adoption()
        for (page in 1..maxPage) {
            val freeAdoptionUrl =
                "https://www.animal.go.kr/front/awtis/protection/protectionList.do?" +
                    "menuNo=${adoptionPath.menuNoParam}" +
                    "&page=$page"
            webDriverCommandService.navigateTo(freeAdoptionUrl)
            searchEachPage(adoptionPath)
        }
    }

    private fun searchEachPage(animalAdoptionHtmlParser: AnimalAdoptionHtmlParser) {
        val eachPostElements = webDriverCommandService.findElementsWithWaitingAlwaysAsList(animalAdoptionHtmlParser.postXpathes)
        eachPostElements.forEach { element ->
            val eachPostIdentifier = animalAdoptionHtmlParser.postIdentifier(element.getAttribute("onclick"))
            if (eachPostIdentifier == null) {
                logger.error { "extracting post url fail " }
            } else {
                println(animalAdoptionHtmlParser.postUrl(eachPostIdentifier))
            }
        }
//
//
//        var animals = webDriverCommandService.findElementsWithWaitingAlwaysAsList(animalAdoptionHtmlParser.animalsXpath)
//        for (index in animals.indices) {
//            // 매번 창은 초기화 되기 때문에 새로 검색해 주어야함
//            animals = webDriverCommandService.findElementsWithWaitingAlwaysAsList(animalAdoptionHtmlParser.animalsXpath)
//            if (index >= animals.size) break
//            scraperErrorService.catchScrawlEachPostError(
//                {
//                    webDriverCommandService.clickElementWithAction(animals[index])
//                    animalGoDataManageService.processDataAndSave(
//                        MakeAdoptionDto(
//                            species = webDriverCommandService.findElementWithWaiting(animalAdoptionHtmlParser.essential.speciesXpath)?.text,
//                            breed = webDriverCommandService.findElementWithWaiting(animalAdoptionHtmlParser.essential.breedXpath)?.text,
//                            region = webDriverCommandService.findElementWithWaiting(animalAdoptionHtmlParser.essential.regionXpath)?.text,
//                            gender = webDriverCommandService.findElementWithWaiting(animalAdoptionHtmlParser.essential.genderXpath)?.text,
//                            title = webDriverCommandService.findElementWithWaiting(animalAdoptionHtmlParser.essential.titleXpath)?.text,
//                            content = webDriverCommandService.findElementWithWaiting(animalAdoptionHtmlParser.essential.contentXpath)?.text,
//                            age = webDriverCommandService.findElementWithWaiting(animalAdoptionHtmlParser.essential.ageXpath)?.text,
//                            createdAt = webDriverCommandService.findElementWithWaiting(animalAdoptionHtmlParser.createdAtXpath)?.text,
//                            thumbnailUrl =
//                                webDriverCommandService
//                                    .findElementWithWaiting(animalAdoptionHtmlParser.essential.thumbnailXpath)
//                                    ?.getAttribute("src"),
//                            postType = PostType.FREE_ADOPTION,
//                            adoptionStatus = AdoptionStatus.ING,
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
    }
}
