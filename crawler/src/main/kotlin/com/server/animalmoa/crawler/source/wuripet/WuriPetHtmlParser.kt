package com.server.animalmoa.crawler.source.wuripet

import com.server.animalmoa.common.adoption.enum.AdoptionStatus
import com.server.animalmoa.common.adoption.enum.Source
import com.server.animalmoa.common.adoption.enum.Species
import com.server.animalmoa.common.common.PostType
import com.server.animalmoa.common.dto.MakeAdoptionDto
import com.server.animalmoa.crawler.scraper.util.JsoupUtil
import com.server.animalmoa.crawler.scraper.util.UrlUtil
import mu.KotlinLogging
import org.openqa.selenium.WebElement
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object WuriPetHtmlParser {
    val source = Source.WURIPET
    val logger = KotlinLogging.logger { source }

    fun getEachAdoptionPage(pageNumber: Int) = source.url + "/freecat?page=$pageNumber"

    val postXpathes = "//*[@id='fboardlist']/section//div[@class='row']//div[@class='thumbnail']/a"

    fun postUrl(element: WebElement): String? = element.getAttribute("href")

    fun postIdentifier(url: String): String? = UrlUtil.extractPathVariable(url, "freecat")

    fun getMakeAdoptionDto(
        html: String,
        url: String,
        identifier: String,
    ): MakeAdoptionDto {
        val breedXpath = "//*[@id='member_profile']/div/div[1]/div/div/div[2]/div[1]/table/tbody/tr[1]/td"
        val regionXpath = "//*[@id='member_profile']/div/div[1]/div/div/div[2]/div[1]/table/tbody/tr[5]/td"
        val genderXpath = "//*[@id='member_profile']/div/div[1]/div/div/div[2]/div[1]/table/tbody/tr[2]/td"
        val titleXpath = "//*[@id='bo_v_title']/span[2]"
        val contentXpath = "//*[@id='bo_v_con']"
        val ageXpath = "//*[@id='member_profile']/div/div[1]/div/div/div[2]/div[1]/table/tbody/tr[4]/td"
        val createdAtXpath = "//*[@id='bo_v_info']/div[1]/div/span[4]"
        val thumbnailXpath = "//*[@id='bo_v_img']/a[1]/img"
        val adoptionStatusXpath = "//*[@id='member_profile']/div/div[1]/div/div/div[1]/div/span"

        val adoptionStatus =
            when (
                JsoupUtil.findFirstElementTextWithXpath(html, adoptionStatusXpath)
            ) {
                "분양중" -> AdoptionStatus.ING
                else -> AdoptionStatus.COMPLETED
            }

        // 작성일 : 25-07-28 09:46
        val createdAtText = JsoupUtil.findFirstElementTextWithXpath(html, createdAtXpath)
        val createdAt =
            createdAtText?.let {
                try {
                    val datePart = createdAtText.substringAfter(":").trim()
                    val formatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm")
                    LocalDateTime.parse(datePart, formatter)
                } catch (e: Exception) {
                    logger.error("Error parsing date: $createdAtText")
                    null
                }
            }
        return MakeAdoptionDto(
            speciesSynonym = Species.CAT.synonyms.first(),
            breedSynonym = JsoupUtil.findFirstElementTextWithXpath(html, breedXpath),
            regionSynonym = JsoupUtil.findFirstElementTextWithXpath(html, regionXpath),
            genderSynonym = JsoupUtil.findFirstElementTextWithXpath(html, genderXpath),
            title = JsoupUtil.findFirstElementTextWithXpath(html, titleXpath),
            content = JsoupUtil.findFirstElementTextWithXpath(html, contentXpath),
            age = JsoupUtil.findFirstElementTextWithXpath(html, ageXpath),
            thumbnailUrl = JsoupUtil.findImgSrcWithXpath(html, thumbnailXpath),
            createdAt = createdAt,
            originalUrl = url,
            adoptionStatus = adoptionStatus,
            source = source,
            identifier = identifier,
            postType = PostType.FREE_ADOPTION,
        )
    }
}
