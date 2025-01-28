package com.server.animalmoa.crawler.domain.animalgo

import com.server.animalmoa.adoption.data.MakeAdoptionDto
import com.server.animalmoa.adoption.domain.AdoptionStatus
import com.server.animalmoa.adoption.domain.Source
import com.server.animalmoa.common.PostType
import com.server.animalmoa.crawler.service.FreeAdoptionCrawler
import com.server.animalmoa.crawler.service.WebDriverService
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

/*
입양대상 동물 메뉴: 완료
 */
@Service
class AnimalGoCrawler(
    private val webDriverService: WebDriverService,
) : FreeAdoptionCrawler {
    private val logger = KotlinLogging.logger {}

    @Value("\${crawl-until.animal-go}")
    private val maxPage: Int = 10
    val freeAdoptionUrl = "https://www.animal.go.kr/front/awtis/protection/protectionList.do?menuNo="

    override fun crawlFreeAdoption() {
        val freeAdoptionPath = AnimalGoPath.freeAdoption()
        webDriverService.navigateTo(freeAdoptionUrl + freeAdoptionPath.menuNoParam)
        var animals = webDriverService.findElementsWithWaiting(freeAdoptionPath.animalsXpath)

        logger.info { animals }
        for (index in animals.indices) {
            // 매번 창은 초기화 되기 때문에 새로 검색해 주어야함
            animals = webDriverService.findElementsWithWaiting(freeAdoptionPath.animalsXpath)
            if (index >= animals.size) break
            animals[index].click()

            println(
                webDriverService
                    .findElementWithWaiting(freeAdoptionPath.essential.thumbnailXpath),
            )
            println(
                MakeAdoptionDto(
                    species = webDriverService.findElementWithWaiting(freeAdoptionPath.essential.speciesXpath)?.text,
                    breed = webDriverService.findElementWithWaiting(freeAdoptionPath.essential.breedXpath)?.text,
                    region = webDriverService.findElementWithWaiting(freeAdoptionPath.essential.regionXpath)?.text,
                    gender = webDriverService.findElementWithWaiting(freeAdoptionPath.essential.genderXpath)?.text,
                    title = webDriverService.findElementWithWaiting(freeAdoptionPath.essential.titleXpath)?.text,
                    content = webDriverService.findElementWithWaiting(freeAdoptionPath.essential.contentXpath)?.text,
                    age = webDriverService.findElementWithWaiting(freeAdoptionPath.essential.ageXpath)?.text,
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
        }
    }
}
