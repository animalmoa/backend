package com.server.animalmoa.crawler.source.juseyo

import com.server.animalmoa.common.adoption.enum.AdoptionStatus
import com.server.animalmoa.common.adoption.enum.Source
import com.server.animalmoa.common.adoption.enum.Species
import com.server.animalmoa.common.common.PostType
import com.server.animalmoa.common.dto.MakeAdoptionDto
import com.server.animalmoa.common.util.RegexUtil
import com.server.animalmoa.crawler.exception.EmptyHtmlException
import com.server.animalmoa.crawler.scraper.util.JsoupUtil
import com.server.animalmoa.crawler.scraper.util.UrlParser
import mu.KotlinLogging
import org.jsoup.Jsoup
import org.openqa.selenium.WebElement
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

enum class JuseyoCategory(
    val urlParam: String,
    val species: Species,
) {
    DOG("dog", Species.DOG),
    CAT("cat", Species.CAT),
}

// 2025.05.24 Juseyo닷컴은 각정보에 대한 Xpath등이 너무 수시로 바뀌기 떄문에, 정규표현식으로 추출한다.
object JuseyoAdoptionHtmlParser {
    val logger = KotlinLogging.logger {}

    // //////////// XPath

    // 카테고리 페이지에서 게시글의 Xpath
    val postXpathes = "//tr[@onclick]"

    // 각 무료 분양 글 페이지의 Xpath
    // 주세요 닷컴은 Xpath가 자주 달라져서 데이터 추출시 정규식을 주로 사용한다.
    val thumbnailXpath = "//*[@id='imgg1']/img"
    // /////////// End of XPath

    // ////////////URL

    fun postListUrl(
        animalParam: String,
        pageNumber: Int,
    ) = Source.JUSEYO.url + "/sale/sale_list.php?animal=$animalParam&page=$pageNumber"

    // <tr onclick="ViewWin=window.open('..
    // /sale/sale_view.php?type=f&oid_no=bbag1752554732821&no=503810&page=1&kind=&area=
    // ','view','width=837,height=860,scrollbars=yes');ViewWin.focus();">
    fun postUrl(element: WebElement): String? =
        RegexUtil.findBetweenKeyword(
            element.getAttribute("onclick"),
            "window.open('..",
            "','",
        )
    // /////////// END OF URL

    fun getMakeAdoptionDto(
        html: String,
        url: String,
        identifier: String,
    ): MakeAdoptionDto {
        val document = Jsoup.parse(html)
        val bodyHtmlText = document.body().text()

        if (bodyHtmlText.isEmpty()) {
            throw EmptyHtmlException(url)
        }

        logger.info { "body: $bodyHtmlText" }

        val age = RegexUtil.findFirstWordAfterKeyword(bodyHtmlText, "개월수")

        // gender는 age와 한 줄에 존재한다.
        val gender = RegexUtil.findFirstWordAfterKeyword(bodyHtmlText, "암수구분")

        val region = RegexUtil.findFirstWordAfterKeyword(bodyHtmlText, "분양지역")

        // TODO 무료 분양 주세요와 원해요의 구분
        val postType =
            run {
                val postType = RegexUtil.findFirstWordAfterKeyword(bodyHtmlText, "책임비")?.trim()
                if (postType == null) {
                    PostType.UNKNOWN
                } else if (postType.endsWith("무료분양")) {
                    PostType.FREE_ADOPTION
                } else if (postType.endsWith("만원")) {
                    PostType.RESPONSIBLE_ADOPTION
                } else {
                    PostType.UNKNOWN
                }
            }

        // 분양동물 강아지 - 한국 고양이 [피해보상규정 자세히보기]
        val breed: String? =
            run {
                val breedTexts =
                    RegexUtil.findBetweenKeyword(bodyHtmlText, "분양동물", "[피해보상규정")
                breedTexts?.substringAfter("-")?.trim()
            }

        val species: String? =
            run {
                val speciesText =
                    RegexUtil.findBetweenKeyword(bodyHtmlText, "분양동물", "[피해보상규정")
                speciesText?.substringBefore("-")?.trim()
            }

        // 등록일 : 2025.07.08 23:02:11
        val createdAt =
            run {
                // : 2025.07.08 23:02:11
                val createdAtTexts = RegexUtil.findWordsAfterKeyword(bodyHtmlText, "등록일", 3)
                try {
                    val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss")
                    LocalDateTime.parse(createdAtTexts[1] + " " + createdAtTexts[2], formatter)
                } catch (e: Exception) {
                    logger.error(e) { "Error parsing date: $createdAtTexts" }
                    null
                }
            }

        // 내용 ~~~  ★사랑하는 반려동물이 좋은 주인을 만나 안전하게 살 수 있도록 아래의 사항을 꼭 지켜 주세요!!
        fun content(bodyHtmlText: String): String? {
            val contentText = RegexUtil.findBetweenKeyword(bodyHtmlText, "내용", "★사랑하는")
            return contentText
        }

        return MakeAdoptionDto(
            originalUrl = url,
            title = content(bodyHtmlText)?.substringBefore("."),
            species = species.toString(),
            breed = breed,
            region = region,
            gender = gender,
            content = content(bodyHtmlText),
            age = age,
            thumbnailUrl = Source.JUSEYO.url + JsoupUtil.findImgSrcWithXpath(document, thumbnailXpath),
            postType = postType,
            // TODO 전체 Img src를 검색해서 확인하는 특정 키워드로 끝나는지 확인하는 방법 분양완료시 = ok.jpg 분양중일시 idlog.gif
            adoptionStatus = AdoptionStatus.ING,
            createdAt = createdAt,
            source = Source.JUSEYO,
            identifier = identifier,
        )
    }

    fun getIdentifier(url: String) = UrlParser.extractQueryParam(url, "no")
}
