package com.server.animalmoa.api.crawler.service

import com.server.animalmoa.api.adoption.data.MakeAdoptionDto
import com.server.animalmoa.api.adoption.domain.Adoption
import com.server.animalmoa.api.exception.IdentifierNotFoundException
import com.server.animalmoa.api.webdriver.UrlParser

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
