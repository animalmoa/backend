package com.server.animalmoa.crawler.scraper.source.animalgo

import com.server.animalmoa.common.adoption.enum.Source
import com.server.animalmoa.common.util.RegexUtil
import com.server.animalmoa.crawler.scraper.util.AdoptionCommonPath

data class AnimalAdoptionHtmlParser(
    var menuNoParam: String,
    val regionTrIndex: Int,
    val regionTdIndex: Int,
    val speciesTrIndex: Int,
    val speciesTdIndex: Int,
    val ageTrIndex: Int,
    val ageTdIndex: Int,
    val genderTrIndex: Int,
    val genderTdIndex: Int,
    val createdAtTrIndex: Int,
    val createdAtTdIndex: Int,
) {
    val postXpathes = "//*[@id=\"contents\"]/div/ul/li/a"

    // onclick="javascript:moveUrl('441378202501009');"
    fun postIdentifier(onclickStr: String): String? {
        println(onclickStr)
        return RegexUtil.findBetweenKeyword(
            onclickStr,
            "javascript:moveUrl('",
            "');",
        )
    }

    fun postUrl(identifier: String): String =
        Source.ANIMAL_GO.url +
            "/front/awtis/protection/protectionDtl.do" +
            "?desertionNo=$identifier"

    private var detailXpath = "//*[@class='table detail-table']/tbody"
    var createdAtXpath = "$detailXpath/tr[$createdAtTrIndex]/td[$createdAtTdIndex]"
    var animalsXpath = "//*[@class='animals-list']/li"
    val essential =
        AdoptionCommonPath(
            titleXpath = "$detailXpath/tr[$speciesTrIndex]/td[$speciesTdIndex]",
            thumbnailXpath = "//*[@id='protectionForm']/div/ul/li[1]/a/img",
            speciesXpath = "$detailXpath/tr[$speciesTrIndex]/td[$speciesTdIndex]",
            breedXpath = "$detailXpath/tr[$speciesTrIndex]/td[$speciesTdIndex]",
            ageXpath = "$detailXpath/tr[$ageTrIndex]/td[$ageTdIndex]",
            genderXpath = "$detailXpath/tr[$genderTrIndex]/td[$genderTdIndex]",
            regionXpath = "$detailXpath/tr[$regionTrIndex]/td[$regionTdIndex]",
            contentXpath = "//*[@id=\"contents\"]/div/div[1]/div",
        )

    companion object {
        fun adoption(): AnimalAdoptionHtmlParser =
            AnimalAdoptionHtmlParser(
                menuNoParam = "417000",
                regionTrIndex = 1,
                regionTdIndex = 1,
                speciesTrIndex = 3,
                speciesTdIndex = 1,
                genderTrIndex = 4,
                genderTdIndex = 1,
                ageTrIndex = 5,
                ageTdIndex = 1,
                createdAtTrIndex = 10,
                createdAtTdIndex = 2,
            )
    }
}
