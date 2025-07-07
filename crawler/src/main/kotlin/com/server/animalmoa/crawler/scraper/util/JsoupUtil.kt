package com.server.animalmoa.crawler.scraper.util

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

object JsoupUtil {
    fun getImgSrcWithXpath(
        document: Document,
        xpath: String,
    ): String? = findElementWithXpath(document, xpath)?.attr("src")

    fun findElementWithXpath(
        document: Document,
        xpath: String,
    ): Element? = document.selectXpath(xpath).firstOrNull()
}
