package com.server.animalmoa.crawler.crawler.source.juseyo

import com.server.animalmoa.common.adoption.domain.Species
import com.server.animalmoa.crawler.crawler.data.StringUtil

// 2025.05.24 Juseyo닷컴은 각정보에 대한 Xpath등이 너무 수시로 바뀌기 떄문에, 정규표현식으로 추출한다.
data class JuseyoData(
    var animalParam: String,
    var categoryParam: String,
    val species: Species,
) {
    // 카테고리 페이지의 Xpath
    val postTypeXpath = ".//img"
    val eachPostXpath = "//tr[@onclick]"
    val titleXpath = ".//td[4]"

    // 각 무료 분양 글 페이지의 Xpath
    val createdAtXpath = "/html/body/table[1]/tbody/tr/td[2]/table/tbody/tr/td"
    val thumbnailXpath = "//*[@id='imgg1']/img"

    // TODO [분양동물, 고양이, -, 한국, 고양이, [피해보상규정, 자세히보기]]
    // 위처럼 출력되기에 단순히 SPLIT하면 안 되어보임.
    fun breed(allText: String) =
        StringUtil.getLine(allText, "분양동물")?.map {
        }

    fun age(allText: String) = StringUtil.getLine(allText, "개월수")?.get(1)

    // gender는 age와 한 줄에 존재한다.
    fun gender(allText: String) = StringUtil.getLine(allText, "개월수")?.get(3)

    fun region(allText: String) = StringUtil.getLine(allText, "분양지역")?.get(1)

    // TODO 태그 검색으로 하는게 맞아보임
    fun content(allText: String) = ""

    companion object {
        fun dog(): JuseyoData =
            JuseyoData(
                "dog",
                "%B0%AD%BE%C6%C1%F6",
                Species.DOG,
            )

        fun cat(): JuseyoData =
            JuseyoData(
                "cat",
                "%B0%ED%BE%E7%C0%CC",
                Species.CAT,
            )
    }
    // /////// Xpath 버전
//    constructor(
//        var animalParam: String,
//        var categoryParam: String,
//        var speciesTableIndex: Int,
//        var speciesTdIndex: Int,
//        var breedTableIndex: Int,
//        var breedTdIndex: Int,
//        var ageTableIndex: Int,
//        var ageTdIndex: Int,
//        var genderTableIndex: Int,
//        var genderTdIndex: Int,
//        var regionTableIndex: Int,
//        var regionTdIndex: Int,
//        var contentTableIndex: Int,
//        var contentTdIndex: Int,
//    )

//    /**
//     * xPath 구조
//     * 번호 노출되지 않을시
//     * species = /html/body/table[2]/tbody/tr/td/table[7]/tbody/tr/td[2]
//     * breed = /html/body/table[2]/tbody/tr/td/table[7]/tbody/tr/td[2]
//     * age = /html/body/table[2]/tbody/tr/td/table[13]/tbody/tr/td[2]
//     * gender = /html/body/table[2]/tbody/tr/td/table[13]/tbody/tr/td[4]
//     * region = /html/body/table[2]/tbody/tr/td/table[9]/tbody/tr/td[2]
//     * content = /html/body/table[2]/tbody/tr/td/table[20]/tbody/tr/td[2]
//     * postType = /[@id="mtarget"]/table[5]/tbody/tr/td[2]/table[5]/tbody/tr/td/table[51]/tbody/tr/td[1]
//     *
//     * 번호 노출될시
//     * species = /html/body/table[2]/tbody/tr/td/table[7]/tbody/tr/td[2]
//     * breed = /html/body/table[2]/tbody/tr/td/table[7]/tbody/tr/td[2]
//     * age = /html/body/table[2]/tbody/tr/td/table[13]/tbody/tr/td[2]
//     * gender = /html/body/table[2]/tbody/tr/td/table[13]/tbody/tr/td[4]
//     * region = /html/body/table[2]/tbody/tr/td/table[9]/tbody/tr/td[2]
//     * content = /html/body/table[2]/tbody/tr/td/table[20]/tbody/tr/td[2]
//     */

//    val essential =
//        AdoptionCommonPath(
//            titleXpath = ".//td[4]",
//            thumbnailXpath = "//*[@id='imgg1']/img",
//            speciesXpath = getDataXPath(speciesTableIndex, speciesTdIndex),
//            breedXpath = getDataXPath(breedTableIndex, breedTdIndex),
//            ageXpath = getDataXPath(ageTableIndex, ageTdIndex),
//            genderXpath = getDataXPath(genderTableIndex, genderTdIndex),
//            regionXpath = getDataXPath(regionTableIndex, regionTdIndex),
//            contentXpath = getDataXPath(contentTableIndex, contentTdIndex) + "/table/tbody/tr/td[2]",
//        )
//
//    companion object {
//        fun dog(): JuseyoData =
//            JuseyoData(
//                animalParam = "dog",
//                categoryParam = "%B0%AD%BE%C6%C1%F6",
//                speciesTableIndex = 7,
//                speciesTdIndex = 2,
//                breedTableIndex = 7,
//                breedTdIndex = 2,
//                ageTableIndex = 13,
//                ageTdIndex = 2,
//                genderTableIndex = 13,
//                genderTdIndex = 4,
//                regionTableIndex = 9,
//                regionTdIndex = 2,
//                contentTableIndex = 20,
//                contentTdIndex = 2,
//            )
//
//        fun cat(): JuseyoData =
//            JuseyoData(
//                animalParam = "cat",
//                categoryParam = "%B0%ED%BE%E7%C0%CC",
//                speciesTableIndex = 7,
//                speciesTdIndex = 2,
//                breedTableIndex = 7,
//                breedTdIndex = 2,
//                ageTableIndex = 13,
//                ageTdIndex = 2,
//                genderTableIndex = 13,
//                genderTdIndex = 4,
//                regionTableIndex = 9,
//                regionTdIndex = 2,
//                contentTableIndex = 20,
//                contentTdIndex = 2,
//            )
//    }
//
//    private fun getDataXPath(
//        tableIndex: Int,
//        tdIndex: Int,
//    ): String = "/html/body/table[2]/tbody/tr/td/table[$tableIndex]/tbody/tr/td[$tdIndex]"
}
