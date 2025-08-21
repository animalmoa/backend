package com.server.animalmoa.crawler.source.kara

import com.server.animalmoa.crawler.scraper.util.UrlUtil
import org.openqa.selenium.WebElement

object KaraAdoptionHtmlParser {
    val freeAdoptionPageUrl = "https://www.ekara.org/kams/adopt?status=입양가능"
    val lastPageXpath = "//*[@id=\"content\"]/div[5]/nav/ul/li[last()-1]/a"

    fun lastPageNumber(webElement: WebElement): Int? =
        webElement.getAttribute("href")?.let {
            UrlUtil.extractQueryParam(it, "page")?.toIntOrNull()
        }
}
