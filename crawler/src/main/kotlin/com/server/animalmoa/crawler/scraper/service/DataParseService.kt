package com.server.animalmoa.crawler.scraper.service

import com.server.animalmoa.crawler.exception.IdentifierNotFoundException
import com.server.animalmoa.crawler.webdriver.UrlParser

abstract class DataParseService(
    private val urlParser: UrlParser,
) {
    protected fun extractIdentifier(
        url: String?,
        paramName: String,
    ): String =
        url?.let {
            val parsedIdentifier =
                urlParser.extractQueryParam(
                    url,
                    paramName,
                ) ?: throw IdentifierNotFoundException()
            parsedIdentifier
        } ?: throw IdentifierNotFoundException()
}
