package com.server.animalmoa.crawler.source.kara

import com.server.animalmoa.common.adoption.enum.AdoptionStatus
import com.server.animalmoa.common.adoption.enum.Source
import com.server.animalmoa.common.common.PostType
import com.server.animalmoa.common.dto.MakeAdoptionDto
import com.server.animalmoa.crawler.scraper.util.JsoupUtil
import com.server.animalmoa.crawler.scraper.util.UrlUtil
import com.server.animalmoa.crawler.source.animalgo.AnimalGoAdoptionHtmlParser.thumbnailXpath
import org.openqa.selenium.WebElement
import java.time.LocalDateTime

object KaraAdoptionHtmlParser {
    val source = Source.KARA

    val freeAdoptionPageUrl = "https://www.ekara.org/kams/adopt?status=입양가능"
    val lastPageXpath = "//*[@id=\"content\"]/div[5]/nav/ul/li[last()-1]/a"

    fun lastPageNumber(webElement: WebElement): Int? =
        webElement.getAttribute("href")?.let {
            UrlUtil.extractQueryParam(it, "page")?.toIntOrNull()
        }

    val postsXpath = "//*[@id=\"content\"]/div[4]/div/div/div/a"

    fun postIdentifier(element: WebElement): String? =
        element.getAttribute("href")?.let {
            UrlUtil.extractPathVariable(it, "adopt")
        }

    fun postUrl(identifier: String): String = "${source.url}/kams/adopt/$identifier"

    fun getMakeAdoptionDto(
        html: String,
        url: String,
        identifier: String,
    ): MakeAdoptionDto {
        val speciesXpath = "//*[@id='content']/div[1]/div/div[2]/ul/li[1]/div[2]/h3"
        // 암컷 / 중성화 O
        val genderXpath = "//*[@id='content']/div[1]/div/div[2]/ul/li[2]/div[2]/h3"
        val ageXpath = "//*[@id='content']/div[1]/div/div[2]/ul/li[3]/div[2]/h3"

        val titleXpath = "//*[@id='pills-1-code-features-1']/div/p/span[1]"
        val contentXpath = "//*[@id='pills-1-code-features-1']/div/div"

        val thumbnailXpath = "//*[@id='carouselCus1']/div[2]/div/div[1]/span[1]/img"
        val thumbnailUrl = "${source.url}/${JsoupUtil.findImgSrcWithXpath(html, thumbnailXpath)}"

        return MakeAdoptionDto(
            species = JsoupUtil.findFirstElementTextWithXpath(html, speciesXpath),
            breed = null,
            region = null,
            gender = JsoupUtil.findFirstElementTextWithXpath(html, genderXpath)?.split("/")?.first(),
            title = JsoupUtil.findFirstElementTextWithXpath(html, titleXpath),
            content = JsoupUtil.findFirstElementTextWithXpath(html, contentXpath),
            age = JsoupUtil.findFirstElementTextWithXpath(html, ageXpath),
            thumbnailUrl = thumbnailUrl,
            createdAt = LocalDateTime.now(),
            originalUrl = url,
            adoptionStatus = AdoptionStatus.ING,
            source = Source.KARA,
            postType = PostType.FREE_ADOPTION,
            identifier = identifier,
        )
    }
}
