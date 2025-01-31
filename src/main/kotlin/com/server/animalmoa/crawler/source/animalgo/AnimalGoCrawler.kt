package com.server.animalmoa.crawler.source.animalgo

import com.server.animalmoa.adoption.data.MakeAdoptionDto
import com.server.animalmoa.adoption.domain.AdoptionStatus
import com.server.animalmoa.adoption.domain.Source
import com.server.animalmoa.common.PostType
import com.server.animalmoa.crawler.service.FreeAdoptionCrawler
import com.server.animalmoa.crawler.service.WebDriverService
import com.server.animalmoa.exception.DataParseException
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

/*
입양대상 동물 메뉴: 완료
TODO 실종동물 페이지
 */
@Service
class AnimalGoCrawler(
    private val webDriverService: WebDriverService,
    private val animalGoDataManageService: AnimalGoDataManageService,
) : FreeAdoptionCrawler {
    private val logger = KotlinLogging.logger {}

    @Value("\${crawl-until.animal-go}")
    private val maxPage: Int = 10

    @Async
    override fun crawlFreeAdoption() {
        println(Thread.currentThread())
        val freeAdoptionPath = AnimalGoPath.freeAdoption()
        for (page in 1..maxPage) {
            val freeAdoptionUrl =
                "https://www.animal.go.kr/front/awtis/protection/protectionList.do?" +
                    "menuNo=${freeAdoptionPath.menuNoParam}" +
                    "&page=$page"
            webDriverService.navigateTo(freeAdoptionUrl)
            searchEachPage(freeAdoptionPath)
        }
    }

    private fun searchEachPage(freeAdoptionPath: AnimalGoPath) {
        var animals = webDriverService.findElementsWithWaitingAlwaysAsList(freeAdoptionPath.animalsXpath)
        for (index in animals.indices) {
            try {
                // 매번 창은 초기화 되기 때문에 새로 검색해 주어야함
                animals = webDriverService.findElementsWithWaitingAlwaysAsList(freeAdoptionPath.animalsXpath)
                if (index >= animals.size) break
                webDriverService.clickElementWithAction(animals[index])
                animalGoDataManageService.parseDataAndSave(
                    MakeAdoptionDto(
                        species = webDriverService.findElementWithWaiting(freeAdoptionPath.essential.speciesXpath)?.text,
                        breed = webDriverService.findElementWithWaiting(freeAdoptionPath.essential.breedXpath)?.text,
                        region = webDriverService.findElementWithWaiting(freeAdoptionPath.essential.regionXpath)?.text,
                        gender = webDriverService.findElementWithWaiting(freeAdoptionPath.essential.genderXpath)?.text,
                        title = webDriverService.findElementWithWaiting(freeAdoptionPath.essential.titleXpath)?.text,
                        content = webDriverService.findElementWithWaiting(freeAdoptionPath.essential.contentXpath)?.text,
                        age = webDriverService.findElementWithWaiting(freeAdoptionPath.essential.ageXpath)?.text,
                        createdAt = webDriverService.findElementWithWaiting(freeAdoptionPath.createdAtXpath)?.text,
                        thumbnailUrl =
                            webDriverService
                                .findElementWithWaiting(freeAdoptionPath.essential.thumbnailXpath)
                                ?.getAttribute("src"),
                        postType = PostType.FREE_ADOPTION.name,
                        adoptionStatus = AdoptionStatus.ING.name,
                        originalUrl = webDriverService.webDriver.currentUrl,
                        source = Source.ANIMAL_GO,
                        identifier = webDriverService.webDriver.currentUrl,
                    ),
                )
                webDriverService.goBack()
            } catch (e: DataParseException) {
                logger.error { e.printStackTrace() }
            } catch (e: Exception) {
                // IdentifierNotFoundException을 포함함
                logger.error { e.printStackTrace() }
            }
        }
    }
}
