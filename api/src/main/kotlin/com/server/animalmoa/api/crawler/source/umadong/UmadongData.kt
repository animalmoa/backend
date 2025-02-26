package com.server.animalmoa.api.crawler.source.umadong

import com.server.animalmoa.api.adoption.domain.Species

data class UmadongData(
    val menuParam: Int,
    val species: String,
) {
    private val rootUrl = "https://m.cafe.naver.com/ca-fe/web/cafes/24387804/menus/"
    val url = rootUrl + menuParam
    val titleXpath: String = "//h2[@class='tit']"
    val createdAtXpath: String = "//span[@class='date font_l']"
    val contentXpath: String = "//div[@class=\"se-component se-text se-l-default\"]"
    val thumbnailXpath: String = "(//img[@class='se-image-resource'])[1]"
    val postsXpath: String = "//*[@id=\"ct\"]/div/div[1]/ul/li/div/a[@class='mainLink']"

    companion object {
        fun cat(): UmadongData =
            UmadongData(
                menuParam = 7,
                species = Species.CAT.name,
            )

        fun dog(): UmadongData =
            UmadongData(
                menuParam = 30,
                species = Species.DOG.name,
            )
    }
}
