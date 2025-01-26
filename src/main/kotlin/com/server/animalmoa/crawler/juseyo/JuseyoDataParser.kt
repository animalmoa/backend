package com.server.animalmoa.crawler.juseyo

import com.server.animalmoa.adoption.service.AdoptionRepositoryService
import com.server.animalmoa.webdriver.UrlParser
import org.springframework.stereotype.Service

@Service
class JuseyoDataParser(
    private val adoptionRepositoryService: AdoptionRepositoryService,
    private val urlParser: UrlParser,
) {
    private val checkSumName = "no"

    fun parseData(
        currentUrl: String,
        thumbnailUrlText: String,
        animalTypeText: String,
        regionText: String,
        ageText: String,
        genderText: String,
    ) {
        var species = animalTypeText.split("-")[0]
        var breed = animalTypeText.split("-")[1]
        var region = regionText
        val postNumber = urlParser.extractQueryParam(currentUrl, checkSumName)
        println(postNumber)

//        if (regionText.endsWith("도")) {
//            // 도를 뺴줘
//        }
//        if (regionText.startsWith("전국")) {
//            region = Region.WIDE
//        } else {
//            regionText = Region.ETC
//            // 해당하지 않을시 그대로 저장해야할거같은데..
//        }
//
//        var ageByMonth = 0
//        if (ageText.endsWith("년")) {
//            // ageByMonth = ageText("년뺴고").int *12해야함
//        } else {
//            // ageByMonth = ageText에서뒤에서 개월 빼고 숫자로
//        }
    }
}
