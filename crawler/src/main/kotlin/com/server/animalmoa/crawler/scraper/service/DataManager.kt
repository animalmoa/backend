package com.server.animalmoa.crawler.scraper.service

import com.server.animalmoa.common.adoption.domain.Adoption
import com.server.animalmoa.common.dto.MakeAdoptionDto
import com.server.animalmoa.crawler.exception.IdentifierNotFoundException
import com.server.animalmoa.crawler.webdriver.UrlParser

abstract class DataManager(
    private val urlParser: UrlParser,
) {
    abstract fun parseDataAndSave(rawDto: MakeAdoptionDto): Adoption?

    protected fun extractIdentifier(
        identifier: String?,
        paramName: String,
    ): String =
        identifier?.let {
            val parsedIdentifier =
                urlParser.extractQueryParam(
                    identifier,
                    paramName,
                ) ?: throw IdentifierNotFoundException()
            parsedIdentifier
        } ?: throw IdentifierNotFoundException()
}
