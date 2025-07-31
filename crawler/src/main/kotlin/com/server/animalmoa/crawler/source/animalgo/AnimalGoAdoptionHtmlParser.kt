package com.server.animalmoa.crawler.source.animalgo

import com.server.animalmoa.common.adoption.enum.AdoptionStatus
import com.server.animalmoa.common.adoption.enum.Source
import com.server.animalmoa.common.common.PostType
import com.server.animalmoa.common.dto.MakeAdoptionDto
import com.server.animalmoa.common.util.RegexUtil
import com.server.animalmoa.crawler.scraper.util.JsoupUtil
import java.time.LocalDate
import java.time.LocalTime

object AnimalGoAdoptionHtmlParser {
    val postXpathes = "//*[@id=\"contents\"]/div/ul/li/a"

    val thumbnailXpath = "//*[@id='protectionForm']/div/ul/li[1]/a/img"

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

    fun postListUrl(pageNumber: Int) =
        Source.ANIMAL_GO.url +
            "/front/awtis/protection/protectionList.do?" +
            "menuNo=$menuNoParam" +
            "&pageNo=$pageNumber"

    fun getMakeAdoptionDto(
        html: String,
        identifier: String,
        postUrl: String,
    ): MakeAdoptionDto {
        // Adoption Property

        val propertyCommonXpath = "//*[@id='protectionForm']/div/table/tbody"

        // 광주-서구-2025-00218
        val postNumberXpath = "$propertyCommonXpath/tr[1]/td"

        val postNumber = JsoupUtil.findFirstElementTextWithXpath(html, postNumberXpath)

        // [고양이]한국 고양이
        val speciesAndBreedXpath = "$propertyCommonXpath/tr[3]/td[1]"

        val speciesANdBreed = JsoupUtil.findFirstElementTextWithXpath(html, speciesAndBreedXpath)

        // 수컷
        val genderXpath = "$propertyCommonXpath/tr[4]/td[1]"

        val gender = JsoupUtil.findFirstElementTextWithXpath(html, genderXpath)

        // 2025(년생) / 0.46 (Kg)
        val ageAndWeightXPath = "$propertyCommonXpath/tr[5]/td[1]"

        val age: String? = JsoupUtil.findFirstElementTextWithXpath(html, ageAndWeightXPath)?.split("/")?.get(0)

        // 2025-07-27
        val createdAtXpath = "$propertyCommonXpath/tr[10]/td[2]"

        val createdAt = JsoupUtil.findFirstElementTextWithXpath(html, createdAtXpath)

        // End Of Adoption Property

        return MakeAdoptionDto(
            species = speciesANdBreed?.let { RegexUtil.findBetweenKeyword(it, "[", "]") },
            breed = speciesANdBreed?.split("]")?.get(1),
            region = postNumber?.split("-")?.get(0),
            gender = gender,
            title = "$postNumber/$speciesANdBreed",
            content = "$postNumber/$speciesANdBreed",
            age = age,
            thumbnailUrl = Source.ANIMAL_GO.url + JsoupUtil.findImgSrcWithXpath(html, thumbnailXpath),
            createdAt = createdAt?.let { LocalDate.parse(it).atTime(LocalTime.MIDNIGHT) },
            originalUrl = postUrl,
            adoptionStatus = AdoptionStatus.ING,
            source = Source.ANIMAL_GO,
            identifier = identifier,
            postType = PostType.FREE_ADOPTION,
        )
    }
}
