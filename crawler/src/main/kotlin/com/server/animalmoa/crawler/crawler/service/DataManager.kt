package com.server.animalmoa.crawler.crawler.service

import com.server.animalmoa.common.adoption.domain.Adoption
import com.server.animalmoa.common.dto.MakeAdoptionDto
import com.server.animalmoa.crawler.exception.IdentifierNotFoundException
import com.server.animalmoa.crawler.webdriver.UrlParser

abstract class DataManager(
    private val urlParser: UrlParser,
) {
    // 각 정보를 추출하는 것은 수행하지 않으며 정보의 변형만 수행한다.
    abstract fun processDataAndSave(rawDto: MakeAdoptionDto): Adoption?

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
