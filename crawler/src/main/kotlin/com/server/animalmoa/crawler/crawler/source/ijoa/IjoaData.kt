package com.server.animalmoa.crawler.crawler.source.ijoa

import com.server.animalmoa.common.adoption.domain.Species
import com.server.animalmoa.crawler.crawler.data.StringUtil

data class IjoaData(
    val species: Species,
    val url: String,
) {
    // 카테고리 페이지의 Xpath
    val eachPostXpath = "//div[@class='list-item']"
    val titleXpath = ".//div[@class='list-title']"
    val thumbnailXpath = ".//div[@class='list-img']/img"
    val linkXpath = ".//a"

    // 각 분양 글 페이지의 Xpath
    val createdAtXpath = "//div[@class='view-info']/span[contains(text(), '등록일')]/following-sibling::span"
    val contentXpath = "//div[@class='view-content']"
    val infoTableXpath = "//div[@class='view-info-table']"

    fun breed(allText: String): String? {
        val breedLine = StringUtil.getLine(allText, "품종")
        return breedLine?.getOrNull(1)
    }

    fun age(allText: String): String? {
        val ageLine = StringUtil.getLine(allText, "나이")
        return ageLine?.getOrNull(1)
    }

    fun gender(allText: String): String? {
        val genderLine = StringUtil.getLine(allText, "성별")
        return genderLine?.getOrNull(1)
    }

    fun region(allText: String): String? {
        val regionLine = StringUtil.getLine(allText, "지역")
        return regionLine?.getOrNull(1)
    }

    fun content(allText: String): String = allText

    companion object {
        fun dog(): IjoaData = IjoaData(Species.DOG, "https://www.ijoa.co.kr/42")

        fun cat(): IjoaData = IjoaData(Species.CAT, "https://www.ijoa.co.kr/42")
    }
}