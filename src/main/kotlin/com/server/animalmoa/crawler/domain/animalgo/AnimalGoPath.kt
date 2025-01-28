package com.server.animalmoa.crawler.domain.animalgo

import com.server.animalmoa.crawler.domain.AdoptionEssentialPath

data class AnimalGoPath(
    var menuNoParam: String,
    val regionTrIndex: Int,
    val regionTdIndex: Int,
    val speciesTrIndex: Int,
    val speciesTdIndex: Int,
    val ageTrIndex: Int,
    val ageTdIndex: Int,
    val genderTrIndex: Int,
    val genderTdIndex: Int,
) {
    private var detailXpath = "//*[@class='table detail-table']/tbody"
    var animalsXpath = "//*[@class='animals-list']/li"
    val essential =
        AdoptionEssentialPath(
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
        fun freeAdoption(): AnimalGoPath =
            AnimalGoPath(
                menuNoParam = "417000",
                regionTrIndex = 1,
                regionTdIndex = 1,
                speciesTrIndex = 3,
                speciesTdIndex = 1,
                genderTrIndex = 4,
                genderTdIndex = 1,
                ageTrIndex = 5,
                ageTdIndex = 1,
            )
    }
}
