package com.server.animalmoa.api.crawler.source.animalgo

import com.server.animalmoa.api.crawler.domain.AdoptionCommonPath

data class AnimalGoData(
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
        fun adoption(): AnimalGoData =
            AnimalGoData(
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
