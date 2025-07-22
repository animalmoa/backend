package com.server.animalmoa.crawler.scraper.source.animalgo

import com.server.animalmoa.common.adoption.enum.AdoptionStatus
import com.server.animalmoa.common.adoption.enum.Source
import com.server.animalmoa.common.common.PostType
import com.server.animalmoa.common.dto.MakeAdoptionDto
import com.server.animalmoa.common.util.RegexUtil
import com.server.animalmoa.crawler.scraper.source.animalgo.AnimalGoAdoptionHtmlParser.getGender
import com.server.animalmoa.crawler.scraper.source.animalgo.AnimalGoAdoptionHtmlParser.getPostNumber
import com.server.animalmoa.crawler.scraper.source.animalgo.AnimalGoAdoptionHtmlParser.getSpeciesAndBreed
import com.server.animalmoa.crawler.scraper.source.animalgo.AnimalGoAdoptionHtmlParser.postUrl
import com.server.animalmoa.crawler.scraper.source.animalgo.AnimalGoAdoptionHtmlParser.proPertyCommonXpath
import com.server.animalmoa.crawler.scraper.util.JsoupUtil
import java.time.LocalDate
import java.time.LocalTime

object AnimalGoAdoptionHtmlParser {
    val postXpathes = "//*[@id=\"contents\"]/div/ul/li/a"
    val menuNoParam = "417000"

    // onclick="javascript:moveUrl('441378202501009');"
    fun postIdentifier(onclickStr: String): String? =
        RegexUtil.findBetweenKeyword(
            onclickStr,
            "javascript:moveUrl('",
            "');",
        )

    fun postUrl(identifier: String): String =
        Source.ANIMAL_GO.url +
            "/front/awtis/protection/protectionDtl.do" +
            "?desertionNo=$identifier"

    // Adoption Property

    val proPertyCommonXpath = "//*[@id='protectionForm']/div/table/tbody"

    // 광주-서구-2025-00218
    val postNumberXpath = "$proPertyCommonXpath/tr[1]/td"

    fun getPostNumber(html: String) = JsoupUtil.findFirstElementTextWithXpath(html, postNumberXpath)

    // [고양이]한국 고양이
    val speciesAndBreedXpath = "$proPertyCommonXpath/tr[3]/td[1]"

    fun getSpeciesAndBreed(html: String) = JsoupUtil.findFirstElementTextWithXpath(html, speciesAndBreedXpath)

    // 수컷
    val genderXpath = "$proPertyCommonXpath/tr[4]/td[1]"

    fun getGender(html: String) = JsoupUtil.findFirstElementTextWithXpath(html, genderXpath)

    // 2025(년생) / 0.46 (Kg)
    val ageAndWeightXPath = "$proPertyCommonXpath/tr[5]/td[1]"

    fun getAge(html: String): String? = JsoupUtil.findFirstElementTextWithXpath(html, ageAndWeightXPath)?.split("/")?.get(0)

    // 2025-07-27
    val createdAtXpath = "$proPertyCommonXpath/tr[10]/td[2]"

    fun getCreatedAtXpath(html: String) = JsoupUtil.findFirstElementTextWithXpath(html, createdAtXpath)

    // End Of Adoption Property

    val thumbnailXpath = "//*[@id='protectionForm']/div/ul/li[1]/a/img"

    fun getMakeAdoptionDto(
        html: String,
        identifier: String,
    ): MakeAdoptionDto =
        MakeAdoptionDto(
            species = getSpeciesAndBreed(html)?.let { RegexUtil.findBetweenKeyword(it, "[", "]") },
            breed = getSpeciesAndBreed(html)?.split("]")?.get(1),
            region = getPostNumber(html)?.split("-")?.get(0),
            gender = getGender(html),
            title = getPostNumber(html) + "/" + getSpeciesAndBreed(html),
            content = getPostNumber(html) + "/" + getSpeciesAndBreed(html),
            age = getAge(html),
            thumbnailUrl = Source.ANIMAL_GO.url + JsoupUtil.findImgSrcWithXpath(html, thumbnailXpath),
            createdAt = getCreatedAtXpath(html)?.let { LocalDate.parse(it).atTime(LocalTime.MIDNIGHT) },
            originalUrl = postUrl(identifier),
            adoptionStatus = AdoptionStatus.ING,
            source = Source.ANIMAL_GO,
            identifier = identifier,
            postType = PostType.FREE_ADOPTION,
        )

    fun adoption(): AnimalGoAdoptionHtmlParser = AnimalGoAdoptionHtmlParser
}
