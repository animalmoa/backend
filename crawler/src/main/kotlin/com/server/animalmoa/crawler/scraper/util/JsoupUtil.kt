package com.server.animalmoa.crawler.scraper.util

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

object JsoupUtil {
    fun findImgSrcWithXpath(
        document: Document,
        xpath: String,
    ): String? = findFirstElementWithXpath(document, xpath)?.attr("src")

    fun findImgSrcWithXpath(
        html: String,
        xpath: String,
    ): String? = findImgSrcWithXpath(Jsoup.parse(html), xpath)

    fun findFirstElementWithXpath(
        document: Document,
        xpath: String,
    ): Element? = document.selectXpath(xpath).firstOrNull()

    fun findFirstElementTextWithXpath(
        html: String,
        xpath: String,
    ): String? {
        val document = Jsoup.parse(html)
        return findFirstElementWithXpath(document, xpath)?.text()
    }
}
