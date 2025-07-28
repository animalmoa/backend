package com.server.animalmoa.crawler.source.wuripet

import com.server.animalmoa.common.adoption.enum.Source

object WuriPetHtmlParser {
    val source = Source.WURIPET

    fun getEachAdoptionPage(pageNumber: Int) = source.url + "/freecat?page=$pageNumber"

    val postXpathes = "//*[@id='fboardlist']/section//div[@class='row']//div[@class='thumbnail']/a"
}
