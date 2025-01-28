package com.server.animalmoa.crawler.juseyo

data class JuseyoXpath(
    var animalParam: String,
    var categoryParam: String,
    var speciesTableIndex: Int,
    var speciesTdIndex: Int,
    var breedTableIndex: Int,
    var breedTdIndex: Int,
    var ageTableIndex: Int,
    var ageTdIndex: Int,
    var genderTableIndex: Int,
    var genderTdIndex: Int,
    var regionTableIndex: Int,
    var regionTdIndex: Int,
    var contentTableIndex: Int,
    var contentTdIndex: Int,
) {
    val titleXpath = ".//td[4]"
    val eachPostXpath = "//tr[@onclick]"
    val createdAtXpath = "/html/body/table[1]/tbody/tr/td[2]/table/tbody/tr/td"
    val thumbnailXpath = "//*[@id='imgg1']/img"

    /**
     * xPath 구조
     * species = /html/body/table[2]/tbody/tr/td/table[7]/tbody/tr/td[2]
     * breed = /html/body/table[2]/tbody/tr/td/table[7]/tbody/tr/td[2]
     * age = /html/body/table[2]/tbody/tr/td/table[13]/tbody/tr/td[2]
     * gender = /html/body/table[2]/tbody/tr/td/table[13]/tbody/tr/td[4]
     * region = /html/body/table[2]/tbody/tr/td/table[9]/tbody/tr/td[2]
     * content = /html/body/table[2]/tbody/tr/td/table[20]/tbody/tr/td[2]
     * postType = /[@id="mtarget"]/table[5]/tbody/tr/td[2]/table[5]/tbody/tr/td/table[51]/tbody/tr/td[1]
     */
    val speciesXpath = getDataXPath(speciesTableIndex, speciesTdIndex)
    val breedXPath = getDataXPath(breedTableIndex, breedTdIndex)
    val ageXPath = getDataXPath(ageTableIndex, ageTdIndex)
    val genderXPath = getDataXPath(genderTableIndex, genderTdIndex)
    val regionXPath = getDataXPath(regionTableIndex, regionTdIndex)
    val contentXPath = getDataXPath(contentTableIndex, contentTdIndex) + "/table/tbody/tr/td[2]"

    companion object {
        fun dog(): JuseyoXpath =
            JuseyoXpath(
                animalParam = "dog",
                categoryParam = "%B0%AD%BE%C6%C1%F6",
                speciesTableIndex = 7,
                speciesTdIndex = 2,
                breedTableIndex = 7,
                breedTdIndex = 2,
                ageTableIndex = 13,
                ageTdIndex = 2,
                genderTableIndex = 13,
                genderTdIndex = 4,
                regionTableIndex = 9,
                regionTdIndex = 2,
                contentTableIndex = 20,
                contentTdIndex = 2,
            )

        fun cat(): JuseyoXpath =
            JuseyoXpath(
                animalParam = "cat",
                categoryParam = "%B0%ED%BE%E7%C0%CC",
                speciesTableIndex = 5,
                speciesTdIndex = 2,
                breedTableIndex = 5,
                breedTdIndex = 2,
                ageTableIndex = 11,
                ageTdIndex = 2,
                genderTableIndex = 11,
                genderTdIndex = 4,
                regionTableIndex = 7,
                regionTdIndex = 2,
                contentTableIndex = 18,
                contentTdIndex = 2,
            )
    }

    private fun getDataXPath(
        tableIndex: Int,
        tdIndex: Int,
    ): String = "/html/body/table[2]/tbody/tr/td/table[$tableIndex]/tbody/tr/td[$tdIndex]"
}
