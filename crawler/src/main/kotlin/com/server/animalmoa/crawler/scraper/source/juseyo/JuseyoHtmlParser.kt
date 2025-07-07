package com.server.animalmoa.crawler.scraper.source.juseyo

import com.server.animalmoa.common.adoption.domain.AdoptionStatus
import com.server.animalmoa.common.adoption.domain.Source
import com.server.animalmoa.common.adoption.domain.Species
import com.server.animalmoa.common.common.PostType
import com.server.animalmoa.common.dto.MakeAdoptionDto
import com.server.animalmoa.crawler.crawler.data.StringUtil
import com.server.animalmoa.crawler.scraper.util.JsoupUtil
import com.server.animalmoa.crawler.scraper.util.UrlParser
import mu.KotlinLogging
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

// 2025.05.24 Juseyo닷컴은 각정보에 대한 Xpath등이 너무 수시로 바뀌기 떄문에, 정규표현식으로 추출한다.
class JuseyoHtmlParser(
    var animalParam: String,
    var categoryParam: String,
    val species: Species,
) {
    val logger = KotlinLogging.logger {}

    // //////////// XPath

    // 카테고리 페이지의 Xpath
    val eachPostXpath = "//tr[@onclick]"

    // 각 무료 분양 글 페이지의 Xpath
    val createdAtXpath = "/html/body/table[1]/tbody/tr/td[2]/table/tbody/tr/td"
    val thumbnailXpath = "//*[@id='imgg1']/img"
    val adoptionStatusXpath = "/html/body/table[2]/tbody/tr/td/table[13]/tbody/tr[1]/td[2]/p_style_subma/img"
    val postTypeXpath = "/html/body/table[2]/tbody/tr/td/table[1]/tbody/tr/td[2]/img"
    val contentXpath = "/html/body/table[2]/tbody/tr/td/table[18]/tbody/tr/td[2]/table/tbody/tr/td[2]"

    // /////////// End of XPath

    // ///////// property

    // TODO [분양동물, 고양이, -, 한국, 고양이, [피해보상규정, 자세히보기]]
    // 위처럼 출력되기에 단순히 SPLIT하면 안 되어보임.
    fun breed(allText: String) = StringUtil.getLine(allText, "분양동물")?.get(1)

    fun age(allText: String) = StringUtil.getLine(allText, "개월수")?.get(1)

    // gender는 age와 한 줄에 존재한다.
    fun gender(allText: String) = StringUtil.getLine(allText, "개월수")?.get(3)

    fun region(allText: String) = StringUtil.getLine(allText, "분양지역")?.get(1)

    fun content(document: Document) = JsoupUtil.findElementWithXpath(document, contentXpath)?.text()

    fun createdAt(createdAtText: String?): String? =
        try {
            val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss")
            createdAtText
                ?.let {
                    it
                        .trim()
                        .replace("등록일 :", "") // "등록일 :" 제거
                        .trim()
                        .let { dateText -> LocalDateTime.parse(dateText, formatter) }
                }.toString()
        } catch (e: DateTimeParseException) {
            logger.error("Error parsing date: ${e.message}")
            null
        }

    fun adoptionStatus(imageSrc: String?): AdoptionStatus {
        if (imageSrc == null || imageSrc.endsWith("idlog.gif")) return AdoptionStatus.ING
        return AdoptionStatus.COMPLETED // "ok.gif"
    }

    fun postType(imageSrc: String?): PostType {
        if (imageSrc == null) return PostType.UNKNOWN

        return if (imageSrc.endsWith("free.gif")) {
            PostType.FREE_ADOPTION
        } else if (imageSrc.endsWith("free2.gif")) {
            PostType.REQUEST_ADOPTION
        } else {
            PostType.UNKNOWN
        }
    }

    // ///////// End of property

    fun getMakeAdoptionDto(
        html: String,
        url: String,
    ): MakeAdoptionDto {
        val document = Jsoup.parse(html)
        val bodyHtmlText = document.body().text()

        return MakeAdoptionDto(
            originalUrl = url,
            title =
                content(document)?.substringBefore("."),
            species = species.toString(),
            breed = breed(bodyHtmlText),
            region = region(bodyHtmlText),
            gender = gender(bodyHtmlText),
            content = content(document),
            age = age(bodyHtmlText),
            thumbnailUrl = Source.JUSEYO.url + JsoupUtil.getImgSrcWithXpath(document, thumbnailXpath),
            postType = postType(JsoupUtil.getImgSrcWithXpath(document, postTypeXpath)),
            adoptionStatus = adoptionStatus(JsoupUtil.getImgSrcWithXpath(document, adoptionStatusXpath)),
            createdAt = createdAt(JsoupUtil.findElementWithXpath(document, createdAtXpath)?.text()),
            source = Source.JUSEYO,
            identifier = getIdentifier(url),
        )
    }

    companion object {
        fun dog(): JuseyoHtmlParser =
            JuseyoHtmlParser(
                "dog",
                "%B0%AD%BE%C6%C1%F6",
                Species.DOG,
            )

        fun cat(): JuseyoHtmlParser =
            JuseyoHtmlParser(
                "cat",
                "%B0%ED%BE%E7%C0%CC",
                Species.CAT,
            )

        fun getIdentifier(url: String) = UrlParser.extractQueryParam(url, "no")

        // /////// Xpath 버전
//    constructor(
//        var animalParam: String,
//        var categoryParam: String,
//        var speciesTableIndex: Int,
//        var speciesTdIndex: Int,
//        var breedTableIndex: Int,
//        var breedTdIndex: Int,
//        var ageTableIndex: Int,
//        var ageTdIndex: Int,
//        var genderTableIndex: Int,
//        var genderTdIndex: Int,
//        var regionTableIndex: Int,
//        var regionTdIndex: Int,
//        var contentTableIndex: Int,
//        var contentTdIndex: Int,
//    )

//    /**
//     * xPath 구조
//     * 번호 노출되지 않을시
//     * species = /html/body/table[2]/tbody/tr/td/table[7]/tbody/tr/td[2]
//     * breed = /html/body/table[2]/tbody/tr/td/table[7]/tbody/tr/td[2]
//     * age = /html/body/table[2]/tbody/tr/td/table[13]/tbody/tr/td[2]
//     * gender = /html/body/table[2]/tbody/tr/td/table[13]/tbody/tr/td[4]
//     * region = /html/body/table[2]/tbody/tr/td/table[9]/tbody/tr/td[2]
//     * content = /html/body/table[2]/tbody/tr/td/table[20]/tbody/tr/td[2]
//     * postType = /[@id="mtarget"]/table[5]/tbody/tr/td[2]/table[5]/tbody/tr/td/table[51]/tbody/tr/td[1]
//     *
//     * 번호 노출될시
//     * species = /html/body/table[2]/tbody/tr/td/table[7]/tbody/tr/td[2]
//     * breed = /html/body/table[2]/tbody/tr/td/table[7]/tbody/tr/td[2]
//     * age = /html/body/table[2]/tbody/tr/td/table[13]/tbody/tr/td[2]
//     * gender = /html/body/table[2]/tbody/tr/td/table[13]/tbody/tr/td[4]
//     * region = /html/body/table[2]/tbody/tr/td/table[9]/tbody/tr/td[2]
//     * content = /html/body/table[2]/tbody/tr/td/table[20]/tbody/tr/td[2]
//     */

//    val essential =
//        AdoptionCommonPath(
//            titleXpath = ".//td[4]",
//            thumbnailXpath = "//*[@id='imgg1']/img",
//            speciesXpath = getDataXPath(speciesTableIndex, speciesTdIndex),
//            breedXpath = getDataXPath(breedTableIndex, breedTdIndex),
//            ageXpath = getDataXPath(ageTableIndex, ageTdIndex),
//            genderXpath = getDataXPath(genderTableIndex, genderTdIndex),
//            regionXpath = getDataXPath(regionTableIndex, regionTdIndex),
//            contentXpath = getDataXPath(contentTableIndex, contentTdIndex) + "/table/tbody/tr/td[2]",
//        )
//
//    companion object {
//        fun dog(): JuseyoData =
//            JuseyoData(
//                animalParam = "dog",
//                categoryParam = "%B0%AD%BE%C6%C1%F6",
//                speciesTableIndex = 7,
//                speciesTdIndex = 2,
//                breedTableIndex = 7,
//                breedTdIndex = 2,
//                ageTableIndex = 13,
//                ageTdIndex = 2,
//                genderTableIndex = 13,
//                genderTdIndex = 4,
//                regionTableIndex = 9,
//                regionTdIndex = 2,
//                contentTableIndex = 20,
//                contentTdIndex = 2,
//            )
//
//        fun cat(): JuseyoData =
//            JuseyoData(
//                animalParam = "cat",
//                categoryParam = "%B0%ED%BE%E7%C0%CC",
//                speciesTableIndex = 7,
//                speciesTdIndex = 2,
//                breedTableIndex = 7,
//                breedTdIndex = 2,
//                ageTableIndex = 13,
//                ageTdIndex = 2,
//                genderTableIndex = 13,
//                genderTdIndex = 4,
//                regionTableIndex = 9,
//                regionTdIndex = 2,
//                contentTableIndex = 20,
//                contentTdIndex = 2,
//            )
//    }
//
//    private fun getDataXPath(
//        tableIndex: Int,
//        tdIndex: Int,
//    ): String = "/html/body/table[2]/tbody/tr/td/table[$tableIndex]/tbody/tr/td[$tdIndex]"
    }
}
