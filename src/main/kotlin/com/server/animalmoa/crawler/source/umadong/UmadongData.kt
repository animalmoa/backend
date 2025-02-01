package com.server.animalmoa.crawler.source.umadong

import com.server.animalmoa.adoption.domain.Species

data class UmadongData(
    val menuParam: Int,
    val species: String,
) {
    val titleXpath: String = "//h2[@class='tit']"
    val createdAtXpath: String = "//span[@class='date font_l']"
    val contentXpath: String = "//div[@class=\"se-component se-text se-l-default\"]"
    val thumbnailXpath: String = "(//img[@class='se-image-resource'])[1]"

    companion object {
        fun cat(): UmadongData =
            UmadongData(
                menuParam = 7,
                species = Species.CAT.name,
            )
    }
}
